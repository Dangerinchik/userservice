package com.userservice.service;

import com.userservice.dto.CardInfoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardInfoService {

    void createCardInfo(CardInfoDTO cardInfoDTO);

    CardInfoDTO getCardInfo(Long id);

    Page<CardInfoDTO> getAllCardsInfo(Pageable pageable);

    void updateCardInfoById(Long id, CardInfoDTO cardInfoDTO);

    void deleteCardInfo(Long id);

}
