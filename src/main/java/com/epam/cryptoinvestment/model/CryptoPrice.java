package com.epam.cryptoinvestment.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Used to represent the name of crypto alongside price at some moment")
public class CryptoPrice {
  @Schema(example = "BTC")
  private String name;
  @Schema(example = "0.5")
  private double price;
}
