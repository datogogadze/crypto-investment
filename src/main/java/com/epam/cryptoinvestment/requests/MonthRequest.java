package com.epam.cryptoinvestment.requests;

import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request form when we want data according to months")
public class MonthRequest {
  @Schema(required = true,
          example = "2022-1-1",
          pattern = "yyyy-M-d",
          description = "start should be provided in corresponding format")
  @NotNull
  String start;
  @Schema(required = true, example = "1", description = "This parameter shouldn't be 0!; "
                                         + " it represents the number of months before or after the, "
                                         + " start date; if negative it means we go from start to past; "
                                         + " if positive we go from start to future")
  int months;
}
