package com.userservice.repository;

import com.userservice.entity.CardInfo;
import com.userservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardInfoRepository extends JpaRepository<CardInfo, Long> {

    void createCardInfo(CardInfo cardInfo);

    @Query(value = "SELECT * FROM card_info WHERE id = :id", nativeQuery = true)
    Optional<CardInfo> getCardInfoById(@Param("id") Long id);

    @Query(value = "SELECT c FROM CardInfo c", countQuery = "SELECT COUNT(c) FROM CardInfo c")
    Page<CardInfo> getAllCardsInfo(Pageable pageable);

    @Modifying
    @Query("UPDATE CardInfo c SET c.number = :#{#cardInfo.number}," +
            " c.holder = :#{#cardInfo.holder}," +
            " c.expirationDate = :#{#cardInfo.expirationDate}" +
            " WHERE c.id = :id")
    void updateCardInfoById(@Param("id") Long id,@Param("cardInfo") CardInfo cardInfo);

    @Modifying
    @Query(value = "DELETE FROM card_info WHERE id = :id", nativeQuery = true)
    void deleteCardInfoById(@Param("id")Long id);
}
