package com.epam.cryptoinvestment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epam.cryptoinvestment.entities.CryptoEntity;
import com.epam.cryptoinvestment.entities.CryptoNameEntity;
import com.epam.cryptoinvestment.repository.CryptoNamesRepository;
import com.epam.cryptoinvestment.repository.CryptoRepository;
import java.util.List;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
public class TestCsvFilesImport {

  @Autowired
  private CryptoRepository cryptoRepository;

  @Autowired
  private CryptoNamesRepository cryptoNamesRepository;


  /*
    I left duplicate values in XRP, so if it is ignores there should be
    10 values instead of 11
  */

  @Test
  void testDuplicateNameTimestampValuesAreNotImported() {
    CryptoNameEntity xrp = cryptoNamesRepository.findByName("XRP").orElseThrow();
    List<CryptoEntity> byNameId = cryptoRepository.findByNameId(xrp.getId());
    assertEquals(10, byNameId.size());
  }

  /*
    I left non csv line in BTC, so there will be CsvValidationException, but
    it should continue and read the other lines, so if there are 10 values in
    BTC it wotks.
  */

  @Test
  void testCorruptedCsvFileForBTC() {
    CryptoNameEntity xrp = cryptoNamesRepository.findByName("BTC").orElseThrow();
    List<CryptoEntity> byNameId = cryptoRepository.findByNameId(xrp.getId());
    assertEquals(10, byNameId.size());
  }

}
