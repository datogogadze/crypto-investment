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
    name = "imported_files",
    uniqueConstraints={ @UniqueConstraint(columnNames = { "name" })}
)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "  this is an entity class for imported files "
                      + "imported file means that it's data has already been imported to "
                      + "database it consists of 2 fields (id auto generated and name is the "
                      + "name of the file)")
public class ImportedFileEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
}
