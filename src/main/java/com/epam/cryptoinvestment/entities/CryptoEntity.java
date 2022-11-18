package com.epam.cryptoinvestment.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
    name = "cryptos",
    uniqueConstraints={ @UniqueConstraint(columnNames = { "timestamp", "crypto_name_id" })}
)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "  this is an entity class for each row in csv file "
                      + "it consists of 4 fields (1 of them id is auto generated, the rest of "
                      + "them is from file) cryptoName (crypto_name_id) is a foreign  key "
                      + "which references the crypto name entry")
public class CryptoEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonIgnore
  private Long id;
  private ZonedDateTime timestamp;
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "crypto_name_id")
  @JsonIgnore
  private CryptoNameEntity cryptoName;
  private double price;
}
