package com.epam.cryptoinvestment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epam.cryptoinvestment.exceptions.CryptoNotSupportedException;
import com.epam.cryptoinvestment.exceptions.IncorrectDaysOrMonthsValueException;
import com.epam.cryptoinvestment.requests.DayRequest;
import com.epam.cryptoinvestment.requests.MonthRequest;
import com.epam.cryptoinvestment.service.CryptoInvestment;
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
public class CryptoInvestmentServiceTest {

  @Autowired
  private CryptoInvestment cryptoService;

  @Test
  void getNormalizedPricesForMonthTestWithNullDate() {
    assertThrows(NullPointerException.class,
                 () -> cryptoService.getNormalizedPricesForMonth(
                     new MonthRequest(null, 1)));
  }

  @Test
  void getNormalizedPricesForMonthTestWithZeroDays() {
    assertThrows(IncorrectDaysOrMonthsValueException.class,
                 () -> cryptoService.getNormalizedPricesForMonth(
                     new MonthRequest("2022-1-1", 0)));
  }

  /*
    The values are placed in a way that the order of the cryptos in descending order
    should be the same as BTC, DOGE, ETH, LTC, XRP
  */
  @Test
  void getNormalizedPricesForMonthTest() {
    var list =
        cryptoService.getNormalizedPricesForMonth(new MonthRequest("2022-1-1", 1));
    assertEquals(5, list.size());
    assertEquals("BTC", list.get(0).getName());
    assertEquals("DOGE", list.get(1).getName());
    assertEquals("ETH", list.get(2).getName());
    assertEquals("LTC", list.get(3).getName());
    assertEquals("XRP", list.get(4).getName());
  }

  @Test
  void getCryptoStatsForMonthTestWithNonExistingCrypto() {
    assertThrows(CryptoNotSupportedException.class,
                 () -> cryptoService.getCryptoStatsForMonth(
                     "NOT EXISTS",
                     new MonthRequest("2022-1-1", 1)));
  }

  @Test
  void getCryptoStatsForMonthTest() {
    var data = cryptoService.getCryptoStatsForMonth("BTC",
                                         new MonthRequest("2022-1-1", 1));
    assertEquals("1641009600000",
                  data.getOldest().getTimestamp().toInstant().toEpochMilli() + "");
    assertEquals("1641308400000",
                 data.getNewest().getTimestamp().toInstant().toEpochMilli() + "");
    assertEquals(1, data.getMinPrice().get(0).getPrice());
    assertEquals(10, data.getMaxPrice().get(0).getPrice());
  }

  @Test
  void getMaxNormalizedCryptoTestWithEmptyResponse() {
    var data =
        cryptoService.getMaxNormalizedCrypto(new DayRequest("2022-2-1", 1));
    assertEquals(null, data.getName());
    assertEquals(-1, data.getPrice());
  }

  @Test
  void getMaxNormalizedCryptoTest() {
    var data =
        cryptoService.getMaxNormalizedCrypto(new DayRequest("2022-1-1", 1));
    assertEquals("BTC", data.getName());
    assertEquals(4, data.getPrice());
  }
}
