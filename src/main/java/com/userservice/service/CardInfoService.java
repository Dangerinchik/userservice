package com.userservice.service;

import com.userservice.dto.CardInfoDTO;
import com.userservice.exception.CardInfoAlreadyExistsException;
import com.userservice.exception.CardInfoNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardInfoService {

    CardInfoDTO createCardInfo(CardInfoDTO cardInfoDTO) throws CardInfoAlreadyExistsException;

    CardInfoDTO getCardInfo(Long id) throws CardInfoNotFoundException;

    Page<CardInfoDTO> getAllCardsInfo(Pageable pageable) throws CardInfoNotFoundException;

    CardInfoDTO updateCardInfoById(Long id, CardInfoDTO cardInfoDTO) throws CardInfoNotFoundException;

    void deleteCardInfo(Long id) throws CardInfoNotFoundException;

}
