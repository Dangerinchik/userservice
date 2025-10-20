package com.userservice.service;

import com.userservice.dto.CardInfoDTO;
import com.userservice.entity.CardInfo;
import com.userservice.entity.User;
import com.userservice.mapper.CardInfoMapper;
import com.userservice.repository.CardInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CardInfoServiceImpl implements CardInfoService {

    private final CardInfoRepository cardInfoRepository;
    private final CardInfoMapper cardInfoMapper;

    @Override
    public void createCardInfo(CardInfoDTO cardInfoDTO) {
        CardInfo cardInfo = cardInfoMapper.toCardInfo(cardInfoDTO);
        cardInfoRepository.createCardInfo(cardInfo);
    }

    @Override
    public CardInfoDTO getCardInfo(Long id) {
        Optional<CardInfo> cardInfo = cardInfoRepository.getCardInfoById(id);
        if (cardInfo.isPresent()) {
            return cardInfoMapper.toCardInfoDTO(cardInfo.get());
        }
        return null;
    }

    @Override
    public Page<CardInfoDTO> getAllCardsInfo(Pageable pageable) {
        Page<CardInfo> cardsInfo = cardInfoRepository.getAllCardsInfo(pageable);
        return cardInfoMapper.toCardInfoDTOPage(cardsInfo);
    }

    @Override
    @Transactional
    public void updateCardInfoById(Long id, CardInfoDTO cardInfoDTO) {
        CardInfo cardInfo = cardInfoMapper.toCardInfo(cardInfoDTO);
        cardInfoRepository.updateCardInfoById(id, cardInfo);
    }

    @Override
    @Transactional
    public void deleteCardInfo(Long id) {
        cardInfoRepository.deleteCardInfoById(id);
    }
}
