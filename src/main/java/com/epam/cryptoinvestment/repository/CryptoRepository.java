package com.epam.cryptoinvestment.repository;

import com.epam.cryptoinvestment.entities.CryptoEntity;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CryptoRepository extends JpaRepository<CryptoEntity, Long> {

  /*
    1. first query selects min timestamp where the timestamp is between the provided values
    2. second query selects max timestamp where the timestamp is between the provided values
    3. third query selects all rows where the price is equal to min price and timestamp is in range
    4. fourth query selects all rows where the price is equal to max price and timestamp is in range

    3 and 4 return lists because maybe the minimum or maximum price occurred several times
    but for 1 and 2 I consider that for specific timestamp there should only be 1 price

    this type of queries gives us the flexibility to select records from whatever time range
    we want, so if in future any range is required (last 1,2,3 etc. months or whatever) we can
    just provide the start and end ranges accordingly and the queries will work
  */

  @Query(   "SELECT crypto "
         +  "FROM CryptoEntity crypto "
         +  "WHERE crypto.timestamp >= ?2 AND crypto.timestamp < ?3 AND crypto.cryptoName.id = ?1 AND crypto.timestamp = "
         +    "(SELECT MIN (crypto.timestamp) "
         +    "FROM CryptoEntity crypto "
         +    "WHERE crypto.cryptoName.id = ?1 AND crypto.timestamp >= ?2 AND crypto.timestamp < ?3 AND crypto.cryptoName.id = ?1 )")
  Optional<CryptoEntity> findOldestInRange(Long id, ZonedDateTime start, ZonedDateTime end);

  @Query(   "SELECT crypto "
            +  "FROM CryptoEntity crypto "
            +  "WHERE crypto.timestamp >= ?2 AND crypto.timestamp < ?3 AND crypto.cryptoName.id = ?1 AND crypto.timestamp = "
            +    "(SELECT MAX (crypto.timestamp) "
            +    "FROM CryptoEntity crypto "
            +    "WHERE crypto.cryptoName.id = ?1 AND crypto.timestamp >= ?2 AND crypto.timestamp < ?3 AND crypto.cryptoName.id = ?1 )")
  Optional<CryptoEntity> findNewestInRange(Long id, ZonedDateTime start, ZonedDateTime end);

  @Query(   "SELECT crypto "
            +  "FROM CryptoEntity crypto "
            +  "WHERE crypto.timestamp >= ?2 AND crypto.timestamp < ?3 AND crypto.cryptoName.id = ?1 AND crypto.price = "
            +    "(SELECT MIN (crypto.price) "
            +    "FROM CryptoEntity crypto "
            +    "WHERE crypto.cryptoName.id = ?1 AND crypto.timestamp >= ?2 AND crypto.timestamp < ?3 AND crypto.cryptoName.id = ?1 )")
  List<CryptoEntity> findMinPriceInRange(Long id, ZonedDateTime start, ZonedDateTime end);

  @Query(   "SELECT crypto "
            +  "FROM CryptoEntity crypto "
            +  "WHERE crypto.timestamp >= ?2 AND crypto.timestamp < ?3 AND crypto.cryptoName.id = ?1 AND crypto.price = "
            +    "(SELECT MAX (crypto.price) "
            +    "FROM CryptoEntity crypto "
            +    "WHERE crypto.cryptoName.id = ?1 AND crypto.timestamp >= ?2 AND crypto.timestamp < ?3 AND crypto.cryptoName.id = ?1 )")
  List<CryptoEntity> findMaxPriceInRange(Long id, ZonedDateTime start, ZonedDateTime end);

  @Query("SELECT crypto FROM CryptoEntity crypto WHERE crypto.cryptoName.id = ?1")
  List<CryptoEntity> findByNameId(Long nameId);

}
