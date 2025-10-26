package com.userservice.service;

import com.userservice.dto.CardInfoDTO;
import com.userservice.entity.CardInfo;
import com.userservice.entity.User;
import com.userservice.exception.CardInfoAlreadyExistsException;
import com.userservice.exception.CardInfoFoundAfterDeletingException;
import com.userservice.exception.CardInfoNotFoundException;

import com.userservice.mapper.CardInfoMapper;
import com.userservice.repository.CardInfoRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
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
    public CardInfoServiceImpl(CardInfoRepository cardInfoRepository, CardInfoMapper cardInfoMapper, CacheManager cacheManager) {
        this.cardInfoRepository = cardInfoRepository;
        this.cardInfoMapper = cardInfoMapper;

    }

    @Override
    public CardInfoDTO createCardInfo(CardInfoDTO cardInfoDTO) throws CardInfoAlreadyExistsException, CardInfoNotFoundException {
        if(cardInfoRepository.existsCardInfoByNumberAndHolderAndAndExpirationDate(cardInfoDTO.getNumber(),
                cardInfoDTO.getHolder(), cardInfoDTO.getExpirationDate())) {
            throw new CardInfoAlreadyExistsException("Card with these credentials already exists");
        }
        CardInfo cardInfo = cardInfoMapper.toCardInfo(cardInfoDTO);

        cardInfoRepository.save(cardInfo);

        cacheCardInfo(cardInfo);

//        Cache cache = cacheManager.getCache("cards");
        //        cache.putIfAbsent(cardInfo.getId(), result);
        return cardInfoMapper.toCardInfoDTO(cardInfo);
    }

    @CachePut(value = "cards", key = "#cardInfo.id")
    public CardInfo cacheCardInfo(CardInfo cardInfo) {
        return cardInfo;
    }

    @Override
    @Cacheable(value = "cards", key = "#id")
    public CardInfoDTO getCardInfo(Long id) throws CardInfoNotFoundException {
        Optional<CardInfo> cardInfo = cardInfoRepository.getCardInfoById(id);
        if (cardInfo.isPresent()) {
            return cardInfoMapper.toCardInfoDTO(cardInfo.get());
        }
        else{
            throw new CardInfoNotFoundException("Card with id: " + id + " not found");
        }
    }

    @Override
    public Page<CardInfoDTO> getAllCardsInfo(Pageable pageable) throws CardInfoNotFoundException {
        Page<CardInfo> cardsInfo = cardInfoRepository.getAllCardsInfo(pageable);
        if(!cardsInfo.hasContent()) {
            throw new CardInfoNotFoundException("There are not any cards");
        }
        return cardInfoMapper.toCardInfoDTOPage(cardsInfo);
    }

    @Override
    @Transactional
    @CachePut(value = "cards", key = "#id")
    public CardInfoDTO updateCardInfoById(Long id, CardInfoDTO cardInfoDTO) throws CardInfoNotFoundException {
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
