package com.epam.cryptoinvestment.service.impl;

import com.epam.cryptoinvestment.entities.CryptoNameEntity;
import com.epam.cryptoinvestment.entities.ImportedFileEntity;
import com.epam.cryptoinvestment.entities.CryptoEntity;
import com.epam.cryptoinvestment.exceptions.CryptoNotSupportedException;
import com.epam.cryptoinvestment.exceptions.IncorrectDaysOrMonthsValueException;
import com.epam.cryptoinvestment.model.CryptoPrice;
import com.epam.cryptoinvestment.model.Range;
import com.epam.cryptoinvestment.repository.CryptoRepository;
import com.epam.cryptoinvestment.repository.CryptoNamesRepository;
import com.epam.cryptoinvestment.repository.ImportedFilesRepository;
import com.epam.cryptoinvestment.requests.DayRequest;
import com.epam.cryptoinvestment.requests.MonthRequest;
import com.epam.cryptoinvestment.responses.CryptoStatsResponse;
import com.epam.cryptoinvestment.service.CryptoInvestment;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CryptoInvestmentImpl implements CryptoInvestment {

  private final CryptoRepository cryptoRepository;
  private final ImportedFilesRepository importedFilesRepository;
  private final CryptoNamesRepository cryptoNamesRepository;
  @Value("${prices.directory.path}")
  private String pricesDirPath;
  @Value("${date.pattern}")
  private String datePattern;

  /*
    after the service bean gets created we check if there is new data
    get all csv files from prices directory (some of them will already be imported)
    loop over them and import data to database (first check id it is already imported)
    if the file has already been imported (it is in database) then we skip the file
    if its name is not in the database yet, we import data from this file
    in the end we add the name in imported_files table, so we won't import this data again
  */
  @PostConstruct
  private void postConstruct() throws IOException {
    var resource =
        Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(pricesDirPath));
    BufferedReader br = new BufferedReader(new InputStreamReader(resource));

    br.lines()
      .filter(file -> file.endsWith(".csv"))
          .forEach(file -> {
            if (importedFilesRepository.findByName(file).isEmpty()) {
              try {
                addRecordsFromCsvFile(file);
              } catch (IOException e) {
                log.error("Error while importing data from file: {}", file, e);
              }
              log.info("records from {} were added", file);
              importedFilesRepository.save(new ImportedFileEntity(null, file));
            } else {
              log.info("File {} already imported", file);
            }
          });
  }

  /*
    create new crypto record and save it to the database
    just in case if the same record is being added again we skip it
    so there won't be duplicated data in the database (same timestamp and crypto name)
  */
  private void saveCryptoPriceRecord(String fileName,
                                    String timestamp,
                                    CryptoNameEntity cryptoNameEntity,
                                    String price) {
    try {
      var parse = ZonedDateTime
          .ofInstant(Instant.ofEpochMilli(Long.parseLong(timestamp)), ZoneOffset.UTC);
      var cryptoEntity = new CryptoEntity(null,
                                          parse,
                                          cryptoNameEntity,
                                          Double.parseDouble(price));
      cryptoRepository.save(cryptoEntity);
    } catch (DataIntegrityViolationException e) {
      // skip row if it violates constraint (timestamp and crypto name pair should be unique)
      log.error("Skipping a row from csv file: {}; line: {} {} {};",
                fileName, timestamp, cryptoNameEntity.getName(), price);
    }
  }

  /*
    we get the file object, it represents one of the csv files in the prices' directory
    CSVReader is used to iterate over the rows and parse data
    the first row is skipped, because it is name of columns
    we check if CryptoName exists, if it doesn't the row is skipped
    otherwise we get values from the line and save data to cryptos table
  */
  private void addRecordsFromCsvFile(String name) throws IOException {
    // save all crypto names in memory, so we won't have to query database for every check
    var cryptoNameToEntity = cryptoNamesRepository.findAll()
                             .stream()
                             .collect(Collectors.
                                          toMap(CryptoNameEntity::getName, cn -> cn));
    var resource =
        Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(pricesDirPath+"/"+name));
    var reader = new CSVReader(new InputStreamReader(resource));
    String[] line;
    int lineNum = 0;
    while (true) {

      /*
        while reading the file if there is an error on some line I still continue to read
        because maybe there is some other valid data. I think for this application it is
        more important to get as many records as possible even if the file is corrupted
        somewhere, so if there is some exception we still continue reading
      */
      try {
        line = reader.readNext();
        if (line != null && line.length != 3) {
          throw new CsvValidationException();
        }
      } catch (CsvValidationException e) {
        log.error("There was an error on line {} in file {}, skipping line", lineNum, name);
        continue;
      } finally {
        lineNum++;
      }

      /*
        if we reach the end of the file we finish this method all the data is already
        imported to the database
      */
      if (line == null) {
        break;
      }

      var timestamp = line[0];
      var cryptoName = line[1];
      var price = line[2];

      /*
        skip the first line if it is just the column name
      */
      if (timestamp.equals("timestamp")) {
        log.debug("addRecordsFromCsvFile: skipping the first line in csv file");
        continue;
      }

      /*
        if we don't have this crypto name in the database we save it
        this check provides us with future extension possibility
        if there is a new file added with new crypto name it will be seamlessly added
        and after this we will be able to provide info about this crypto too
      */
      if (!cryptoNameToEntity.containsKey(cryptoName)) {
        var cryptoNameEntity = cryptoNamesRepository.save(
            new CryptoNameEntity(null, cryptoName));
        cryptoNameToEntity.put(cryptoName, cryptoNameEntity);
      }

      saveCryptoPriceRecord(name, timestamp, cryptoNameToEntity.get(cryptoName), price);
    }
  }

  /*
    if the months is negative that means we want to check data for last months for example
    if it is 6 then we will check data for last 6 months if it is +6 then we will check
    data starting from this day till next 6 months

    so if it is negative set start day as the end of the day to include all data
    from this day

    if it is positive then hours 0 minutes 0 means that data from this day will be counted
    without any changes

    at the end we choose min date as the start and max date as the end
  */
  private Range getStartAndEndForMonths(LocalDate date, int months) {
    /*
      with date and number of months, we construct the range which we want data from
    */
    var start = ZonedDateTime.of(
        date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 0, 0, 0, 0,
        ZoneId.of("UTC"));

    if (months < 0) {
      start = start.plusDays(1).minusNanos(1);
    }

    var end = start.plusMonths(months);
    return new Range(end.isAfter(start) ? start : end, end.isAfter(start) ? end : start);
  }

  private List<CryptoPrice> getNormalizedPricesDescending(ZonedDateTime start, ZonedDateTime end) {
    /*
      then we get the data for the requested month for each crypto
    */
    var normalizedPrices = new ArrayList<CryptoPrice>();
    cryptoNamesRepository.findAll().forEach(cn -> {
      var minPrice = cryptoRepository.findMinPriceInRange(cn.getId(), start, end);
      var maxPrice = cryptoRepository.findMaxPriceInRange(cn.getId(), start, end);

      if (minPrice.isEmpty()) {
        log.error("Crypto {} not available in range {} - {}", cn.getName(), start, end);
        return;
      }

      double min = minPrice.get(0).getPrice();
      double max = maxPrice.get(0).getPrice();
      /*
        division by zero, we just skip, (other way would be to add some delta and divide by
        that, so we would get some big number, or maybe return inf straight away)
      */
      if (min != 0) {
        normalizedPrices.add(new CryptoPrice(cn.getName(), (max - min) / min));
      } else {
        log.error("Can't calculate normalized price, min is 0: {}", cn.getName());
      }
    });

    /*
      sort values with comparator sorts values in descending order according to price
    */
    normalizedPrices.sort((x, y) -> {
      if (x.getPrice() == y.getPrice()) {
        return 0;
      }
      return x.getPrice() > y.getPrice() ? -1 : 1;
    });
    return normalizedPrices;
  }

  private CryptoStatsResponse getCryptoStatsForRange(String crypto, ZonedDateTime start, ZonedDateTime end) {
    var cryptoNameEntity = cryptoNamesRepository.findByName(crypto).orElseThrow(
        CryptoNotSupportedException::new);
    var oldest = cryptoRepository
        .findOldestInRange(cryptoNameEntity.getId(), start, end).orElse(null);
    var newest = cryptoRepository
        .findNewestInRange(cryptoNameEntity.getId(), start, end).orElse(null);
    var minPrice = cryptoRepository
        .findMinPriceInRange(cryptoNameEntity.getId(), start, end);
    var maxPrice = cryptoRepository
        .findMaxPriceInRange(cryptoNameEntity.getId(), start, end);
    return new CryptoStatsResponse(cryptoNameEntity.getName(), oldest, newest, minPrice, maxPrice);
  }

  LocalDate parseStringToDate(String date) {
    var parser = DateTimeFormatter.ofPattern(datePattern);
    var parsed = LocalDate.parse(date, parser);
    return parsed;
  }

  public List<CryptoPrice> getNormalizedPricesForMonth(MonthRequest monthReq) {
    if (monthReq.getMonths() == 0) {
      throw new IncorrectDaysOrMonthsValueException();
    }
    var date = parseStringToDate(monthReq.getStart());
    var range = getStartAndEndForMonths(date, monthReq.getMonths());
    return getNormalizedPricesDescending(range.getStart(), range.getEnd());
  }

  public CryptoStatsResponse getCryptoStatsForMonth(String crypto, MonthRequest monthReq) {
    if (monthReq.getMonths() == 0) {
      throw new IncorrectDaysOrMonthsValueException();
    }
    var date = parseStringToDate(monthReq.getStart());
    var range = getStartAndEndForMonths(date, monthReq.getMonths());
    return getCryptoStatsForRange(crypto, range.getStart(), range.getEnd());
  }

  /*
    if the days is negative that means we want to check data for last days for example
    if it is -7 then we will check data for last 7 days if it is +7 then we will check
    data starting from this day till next 7 days

    so if it is negative set start day as the end of the day to include all data
    from this day

    if it is positive then hours 0 minutes 0 means that data from this day will be counted
    without any changes

    at the end we choose min date as the start and max date as the end
  */
  private Range getStartAndEndForDays(LocalDate date, int days) {
    /*
      date and number of days, we construct the range which we want data from
    */
    var start = ZonedDateTime.of(
        date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 0, 0, 0, 0,
        ZoneId.of("UTC"));

    if (days < 0) {
      start = start.plusDays(1).minusNanos(1);
    }

    var end = start.plusDays(days);

    return new Range(end.isAfter(start) ? start : end, end.isAfter(start) ? end : start);
  }

  @Override
  public CryptoPrice getMaxNormalizedCrypto(DayRequest dayReq) {
    if (dayReq.getDays() == 0) {
      throw new IncorrectDaysOrMonthsValueException();
    }
    var date = parseStringToDate(dayReq.getStart());
    var range = getStartAndEndForDays(date, dayReq.getDays());
    List<CryptoPrice> normalizedPrices = getNormalizedPricesDescending(range.getStart(), range.getEnd());
    if (normalizedPrices.isEmpty()) {
      log.error("No cryptos found for day {}", dayReq.getStart());
      return new CryptoPrice(null, -1);
    }

    /*
      we get the normalized prices sorted in descending order,
      so we can just return the 0th element
    */

    return normalizedPrices.get(0);
  }

}
