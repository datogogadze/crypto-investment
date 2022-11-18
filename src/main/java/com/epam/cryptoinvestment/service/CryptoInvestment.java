package com.epam.cryptoinvestment.service;

import com.epam.cryptoinvestment.model.CryptoPrice;
import com.epam.cryptoinvestment.requests.DayRequest;
import com.epam.cryptoinvestment.requests.MonthRequest;
import com.epam.cryptoinvestment.responses.CryptoStatsResponse;
import java.util.List;

public interface CryptoInvestment {

  List<CryptoPrice> getNormalizedPricesForMonth(MonthRequest month);

  CryptoStatsResponse getCryptoStatsForMonth(String crypto, MonthRequest month);

  CryptoPrice getMaxNormalizedCrypto(DayRequest day);

}
