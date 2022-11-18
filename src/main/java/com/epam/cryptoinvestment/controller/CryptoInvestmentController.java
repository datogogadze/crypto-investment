package com.epam.cryptoinvestment.controller;

import com.epam.cryptoinvestment.exceptions.TooManyRequestsException;
import com.epam.cryptoinvestment.model.CryptoPrice;
import com.epam.cryptoinvestment.requests.DayRequest;
import com.epam.cryptoinvestment.requests.MonthRequest;
import com.epam.cryptoinvestment.responses.CryptoStatsResponse;
import com.epam.cryptoinvestment.responses.NormalizedCryptosResponse;
import com.epam.cryptoinvestment.service.CryptoInvestment;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.time.Duration;
import java.time.LocalDate;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
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

  /*
  create bucket that gives the limit for 10 requests per 1 minute
  the bucket will be created for each ip, it is saved in session
  and the key will be throttler-{ip}, if it already exists we use this bucket
  if it is null then we have the 1st request from ip, and we create new bucket
  every time we try to consume bucket and if the limit is reached we return
  status code 429 for too many requests
*/
  private Bucket createNewBucket() {
    var refill = Refill.intervally(limitNumber, Duration.ofMinutes(limitTime));
    var limit = Bandwidth.classic(limitNumber, refill);
    return Bucket4j.builder().addLimit(limit).build();
  }

  private Bucket getBucket(HttpServletRequest request) {
    var session = request.getSession(true);
    var remoteAddr = request.getRemoteAddr();
    var bucket = (Bucket) session.getAttribute("throttler-" + remoteAddr);
    if (bucket == null) {
      bucket = createNewBucket();
      session.setAttribute("throttler-" + remoteAddr, bucket);
    }
    return bucket;
  }

  @Operation(summary = "Get normalized prices for every crypto in descending order")
  @ApiResponse(responseCode = "200", description = "success")
  @ApiResponse(responseCode = "400", description = "year or month incorrect")
  @ApiResponse(responseCode = "429", description = "too many requests")
  @PostMapping("stats/normalized")
  public NormalizedCryptosResponse getMonthStats(HttpServletRequest request, @Valid @RequestBody MonthRequest month) {
    var bucket = getBucket(request);
    if (!bucket.tryConsume(1)) {
      throw new TooManyRequestsException(limitNumber, limitTime);
    }
    var monthStats = cryptoInvestment.getNormalizedPricesForMonth(month);
    return new NormalizedCryptosResponse(monthStats);
  }

  @Operation(summary = "Get stats for specific crypto (oldest/newest/min price/max price)")
  @ApiResponse(responseCode = "200", description = "success")
  @ApiResponse(responseCode = "400", description = "crypto not supported; year or month incorrect")
  @ApiResponse(responseCode = "429", description = "too many requests")
  @PostMapping("stats/crypto/{crypto}")
  public CryptoStatsResponse getCryptoStats(HttpServletRequest request, @Parameter(required = true,
                                           description = "crypto which we want to check stats for")
                                           @PathVariable("crypto") String crypto,
                                           @Valid @RequestBody MonthRequest month) {
    var bucket = getBucket(request);
    if (!bucket.tryConsume(1)) {
      throw new TooManyRequestsException(limitNumber, limitTime);
    }
    return cryptoInvestment.getCryptoStatsForMonth(crypto, month);
  }

  @Operation(summary = "Gets crypto with the highest normalized range for specific day")
  @ApiResponse(responseCode = "200", description = "success")
  @ApiResponse(responseCode = "400", description = "crypto not supported; year or month incorrect")
  @ApiResponse(responseCode = "429", description = "too many requests")
  @PostMapping("max/normalized")
  public CryptoPrice getMaxNormalizedCrypto(HttpServletRequest request, @Valid @RequestBody DayRequest day) {
    var bucket = getBucket(request);
    if (!bucket.tryConsume(1)) {
      throw new TooManyRequestsException(limitNumber, limitTime);
    }
    return cryptoInvestment.getMaxNormalizedCrypto(day);
  }

  /*
    making this 2 endpoints becomes very easy because we can just hardcode
    -1 for last month's stats and -6 for last 6 months' stats and use now as the start date.
  */

  @Operation(summary = "Get normalized for last month")
  @ApiResponse(responseCode = "200", description = "success")
  @ApiResponse(responseCode = "429", description = "too many requests")
  @GetMapping("stats/normalized/last-month")
  public NormalizedCryptosResponse getLastMonthStats(HttpServletRequest request) {
    var bucket = getBucket(request);
    if (!bucket.tryConsume(1)) {
      throw new TooManyRequestsException(limitNumber, limitTime);
    }
    var month = new MonthRequest(LocalDate.now().toString(), -1);
    var monthStats = cryptoInvestment.getNormalizedPricesForMonth(month);
    return new NormalizedCryptosResponse(monthStats);
  }

  @Operation(summary = "Get normalized for last 6 months")
  @ApiResponse(responseCode = "200", description = "success")
  @ApiResponse(responseCode = "429", description = "too many requests")
  @GetMapping("stats/normalized/last-six-months")
  public NormalizedCryptosResponse getLastSixMonthStats(HttpServletRequest request) {
    var bucket = getBucket(request);
    if (!bucket.tryConsume(1)) {
      throw new TooManyRequestsException(limitNumber, limitTime);
    }
    var month = new MonthRequest(LocalDate.now().toString(), -6);
    var monthStats = cryptoInvestment.getNormalizedPricesForMonth(month);
    return new NormalizedCryptosResponse(monthStats);
  }

}
