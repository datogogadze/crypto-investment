package com.epam.cryptoinvestment.repository;

import com.epam.cryptoinvestment.entities.ImportedFileEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportedFilesRepository extends JpaRepository<ImportedFileEntity, Long> {

  Optional<ImportedFileEntity> findByName(String name);

}
