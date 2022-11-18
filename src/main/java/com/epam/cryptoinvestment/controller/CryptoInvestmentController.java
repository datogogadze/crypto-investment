package com.epam.cryptoinvestment.controller;

import com.epam.cryptoinvestment.exceptions.TooManyRequestsException;
import com.epam.cryptoinvestment.model.CryptoPrice;
import com.epam.cryptoinvestment.requests.DayRequest;
import com.epam.cryptoinvestment.requests.MonthRequest;
import com.epam.cryptoinvestment.responses.CryptoStatsResponse;
import com.epam.cryptoinvestment.responses.NormalizedCryptosResponse;
import com.epam.cryptoinvestment.service.CryptoInvestment;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.time.LocalDate;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
@Validated
public class CryptoInvestmentController {

  private final CryptoInvestment cryptoInvestment;

  @Value("${request.limit.number}")
  private Integer limitNumber;

  @Value("${request.limit.time.minutes}")
  private Integer limitTime;

  private static final String CRYPTO_SERVICE = "cryptoService";

  public NormalizedCryptosResponse normalizedResponseFallback(Exception e) {
    throw new TooManyRequestsException(limitNumber, limitTime);
  }

  public CryptoStatsResponse cryptoStatsResponseFallback(Exception e) {
    throw new TooManyRequestsException(limitNumber, limitTime);
  }

  public CryptoPrice cryptoPriceResponseFallback(Exception e) {
    throw new TooManyRequestsException(limitNumber, limitTime);
  }

  @RateLimiter(name = CRYPTO_SERVICE, fallbackMethod = "normalizedResponseFallback")
  @Operation(summary = "Get normalized prices for every crypto in descending order")
  @ApiResponse(responseCode = "200", description = "success")
  @ApiResponse(responseCode = "400", description = "year or month incorrect")
  @ApiResponse(responseCode = "429", description = "too many requests")
  @PostMapping("stats/normalized")
  public NormalizedCryptosResponse getMonthStats(HttpServletRequest request, @Valid @RequestBody MonthRequest month) {
    var monthStats = cryptoInvestment.getNormalizedPricesForMonth(month);
    return new NormalizedCryptosResponse(monthStats);
  }
  @RateLimiter(name = CRYPTO_SERVICE, fallbackMethod = "cryptoStatsResponseFallback")
  @Operation(summary = "Get stats for specific crypto (oldest/newest/min price/max price)")
  @ApiResponse(responseCode = "200", description = "success")
  @ApiResponse(responseCode = "400", description = "crypto not supported; year or month incorrect")
  @ApiResponse(responseCode = "429", description = "too many requests")
  @PostMapping("stats/crypto/{crypto}")
  public CryptoStatsResponse getCryptoStats(HttpServletRequest request, @Parameter(required = true,
                                           description = "crypto which we want to check stats for")
                                           @PathVariable("crypto") String crypto,
                                           @Valid @RequestBody MonthRequest month) {
    return cryptoInvestment.getCryptoStatsForMonth(crypto, month);
  }

  @RateLimiter(name = CRYPTO_SERVICE, fallbackMethod = "cryptoPriceResponseFallback")
  @Operation(summary = "Gets crypto with the highest normalized range for specific day")
  @ApiResponse(responseCode = "200", description = "success")
  @ApiResponse(responseCode = "400", description = "crypto not supported; year or month incorrect")
  @ApiResponse(responseCode = "429", description = "too many requests")
  @PostMapping("max/normalized")
  public CryptoPrice getMaxNormalizedCrypto(HttpServletRequest request, @Valid @RequestBody DayRequest day) {
    return cryptoInvestment.getMaxNormalizedCrypto(day);
  }

  /*
    making this 2 endpoints becomes very easy because we can just hardcode
    -1 for last month's stats and -6 for last 6 months' stats and use now as the start date.
  */

  @RateLimiter(name = CRYPTO_SERVICE, fallbackMethod = "normalizedResponseFallback")
  @Operation(summary = "Get normalized for last month")
  @ApiResponse(responseCode = "200", description = "success")
  @ApiResponse(responseCode = "429", description = "too many requests")
  @GetMapping("stats/normalized/last-month")
  public NormalizedCryptosResponse getLastMonthStats(HttpServletRequest request) {
    var month = new MonthRequest(LocalDate.now().toString(), -1);
    var monthStats = cryptoInvestment.getNormalizedPricesForMonth(month);
    return new NormalizedCryptosResponse(monthStats);
  }

  @RateLimiter(name = CRYPTO_SERVICE, fallbackMethod = "normalizedResponseFallback")
  @Operation(summary = "Get normalized for last 6 months")
  @ApiResponse(responseCode = "200", description = "success")
  @ApiResponse(responseCode = "429", description = "too many requests")
  @GetMapping("stats/normalized/last-six-months")
  public NormalizedCryptosResponse getLastSixMonthStats(HttpServletRequest request) {
    var month = new MonthRequest(LocalDate.now().toString(), -6);
    var monthStats = cryptoInvestment.getNormalizedPricesForMonth(month);
    return new NormalizedCryptosResponse(monthStats);
  }

}
