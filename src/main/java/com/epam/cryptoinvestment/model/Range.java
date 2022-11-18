package com.epam.cryptoinvestment.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Used to represent range, we have start date and end date"
                      + " which the data will be queried between this dates")
public class Range {
  @Schema(example = "2022-1-1")
  private ZonedDateTime start;
  @Schema(example = "2022-1-10")
  private ZonedDateTime end;
}
