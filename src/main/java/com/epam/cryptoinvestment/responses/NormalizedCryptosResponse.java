package com.epam.cryptoinvestment.responses;

import com.epam.cryptoinvestment.model.CryptoPrice;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "This represents the list of normalized cryptos")
public class NormalizedCryptosResponse {

  @Schema(example = "[]")
  @JsonProperty("normalized_prices")
  private List<CryptoPrice> normalizedPrices;

}
