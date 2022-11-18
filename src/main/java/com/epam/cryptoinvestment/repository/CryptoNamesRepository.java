package com.epam.cryptoinvestment.repository;

import com.epam.cryptoinvestment.entities.CryptoNameEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CryptoNamesRepository extends JpaRepository<CryptoNameEntity, Long> {

  Optional<CryptoNameEntity> findByName(String name);

}
