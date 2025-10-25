package com.userservice.service;

import com.userservice.dto.CardInfoDTO;
import com.userservice.exception.CardInfoAlreadyExistsException;
import com.userservice.exception.CardInfoFoundAfterDeletingException;
import com.userservice.exception.CardInfoNotFoundException;
import com.userservice.exception.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardInfoService {

    CardInfoDTO createCardInfo(CardInfoDTO cardInfoDTO) throws CardInfoAlreadyExistsException, UserNotFoundException, CardInfoNotFoundException;

    CardInfoDTO getCardInfo(Long id) throws CardInfoNotFoundException;

    Page<CardInfoDTO> getAllCardsInfo(Pageable pageable) throws CardInfoNotFoundException;

    CardInfoDTO updateCardInfoById(Long id, CardInfoDTO cardInfoDTO) throws CardInfoNotFoundException;

    void deleteCardInfo(Long id) throws CardInfoNotFoundException, CardInfoFoundAfterDeletingException;

}
