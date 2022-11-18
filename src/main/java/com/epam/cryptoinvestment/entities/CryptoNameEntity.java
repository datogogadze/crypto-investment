package com.epam.cryptoinvestment.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
    name = "crypto_names",
    uniqueConstraints={ @UniqueConstraint(columnNames = {"name" })}
)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "  this is an entity class for crypto names (which cryptos we have in "
                      + "database) it consists of 2 fields (id auto generated and name is the "
                      + "name of the crypto)")
public class CryptoNameEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
}
