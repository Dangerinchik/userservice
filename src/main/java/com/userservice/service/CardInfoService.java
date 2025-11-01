package com.userservice.service;

import com.userservice.dto.CardInfoDTO;
import com.userservice.dto.ResponseCardInfoDTO;
import com.userservice.exception.CardInfoAlreadyExistsException;
import com.userservice.exception.CardInfoFoundAfterDeletingException;
import com.userservice.exception.CardInfoNotFoundException;
import com.userservice.exception.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardInfoService {

    ResponseCardInfoDTO createCardInfo(CardInfoDTO cardInfoDTO) throws CardInfoAlreadyExistsException, UserNotFoundException, CardInfoNotFoundException;

    ResponseCardInfoDTO getCardInfo(Long id) throws CardInfoNotFoundException;

    Page<ResponseCardInfoDTO> getAllCardsInfo(Pageable pageable) throws CardInfoNotFoundException;

    ResponseCardInfoDTO updateCardInfoById(Long id, CardInfoDTO cardInfoDTO) throws CardInfoNotFoundException;

    void deleteCardInfo(Long id) throws CardInfoNotFoundException, CardInfoFoundAfterDeletingException;

}
