package com.epam.cryptoinvestment.responses;

import com.epam.cryptoinvestment.entities.CryptoEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Represents the stats for specific crypto in some range of time")
public class CryptoStatsResponse {
  @Schema(example = "BTC")
  String crypto;
  CryptoEntity oldest;
  CryptoEntity newest;
  List<CryptoEntity> minPrice;
  List<CryptoEntity> maxPrice;
}
