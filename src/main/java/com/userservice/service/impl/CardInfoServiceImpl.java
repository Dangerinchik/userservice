package com.userservice.service.impl;

import com.userservice.dto.CardInfoDTO;
import com.userservice.dto.ResponseCardInfoDTO;
import com.userservice.entity.CardInfo;
import com.userservice.exception.CardInfoAlreadyExistsException;
import com.userservice.exception.CardInfoFoundAfterDeletingException;
import com.userservice.exception.CardInfoNotFoundException;

import com.userservice.mapper.CardInfoMapper;
import com.userservice.repository.CardInfoRepository;
import com.userservice.service.CardInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CardInfoServiceImpl implements CardInfoService {

    private final CardInfoRepository cardInfoRepository;
    private final CardInfoMapper cardInfoMapper;

    @Autowired
    public CardInfoServiceImpl(CardInfoRepository cardInfoRepository, CardInfoMapper cardInfoMapper) {
        this.cardInfoRepository = cardInfoRepository;
        this.cardInfoMapper = cardInfoMapper;

    }

    @Override
    @CachePut(value = "cards", key = "#result.id")
    @Transactional
    public ResponseCardInfoDTO createCardInfo(CardInfoDTO cardInfoDTO) throws CardInfoAlreadyExistsException, CardInfoNotFoundException {
        if(cardInfoRepository.existsCardInfoByNumberAndHolderAndExpirationDate(cardInfoDTO.getNumber(),
                cardInfoDTO.getHolder(), cardInfoDTO.getExpirationDate())) {
            throw new CardInfoAlreadyExistsException("Card with these credentials already exists");
        }
        CardInfo cardInfo = cardInfoMapper.toCardInfo(cardInfoDTO);

        cardInfoRepository.save(cardInfo);

        return cardInfoMapper.toCardInfoDTO(cardInfo);
    }

    @Override
    @Cacheable(value = "cards", key = "#id")
    public ResponseCardInfoDTO getCardInfo(Long id) throws CardInfoNotFoundException {
        Optional<CardInfo> cardInfo = cardInfoRepository.getCardInfoById(id);
        if (cardInfo.isPresent()) {
            return cardInfoMapper.toCardInfoDTO(cardInfo.get());
        }
        else{
            throw new CardInfoNotFoundException("Card with id: " + id + " not found");
        }
    }

    @Override
    public Page<ResponseCardInfoDTO> getAllCardsInfo(Pageable pageable) throws CardInfoNotFoundException {
        Page<CardInfo> cardsInfo = cardInfoRepository.getAllCardsInfo(pageable);
        if(!cardsInfo.hasContent()) {
            throw new CardInfoNotFoundException("There are not any cards");
        }
        return cardInfoMapper.toCardInfoDTOPage(cardsInfo);
    }

    @Override
    @Transactional
    @CachePut(value = "cards", key = "#id")
    public ResponseCardInfoDTO updateCardInfoById(Long id, CardInfoDTO cardInfoDTO) throws CardInfoNotFoundException {
        if(!cardInfoRepository.existsById(id)){
            throw new CardInfoNotFoundException("Card with id: " + id + " not found for updating");
        }
        CardInfo cardInfo = cardInfoMapper.toCardInfo(cardInfoDTO);
        cardInfoRepository.updateCardInfoById(id, cardInfo);
        cardInfoRepository.flush();
        CardInfo updated = cardInfoRepository.getCardInfoById(id)
                .orElseThrow(() -> new CardInfoNotFoundException("Card not found after updating"));

        return cardInfoMapper.toCardInfoDTO(updated);
    }

    @Override
    @Transactional
    @CacheEvict(value = "cards", key = "#id")
    public void deleteCardInfo(Long id) throws CardInfoFoundAfterDeletingException, CardInfoNotFoundException {
        if(!cardInfoRepository.existsById(id)){
            throw new CardInfoNotFoundException("Card with id: " + id + " not found for deleting");
        }
        cardInfoRepository.deleteCardInfoById(id);
        cardInfoRepository.flush();

        if(cardInfoRepository.existsById(id)){
            throw new CardInfoFoundAfterDeletingException("Card with id: " + id + " found after deleting");
        }
    }
}
