package com.userservice.unit;

import com.userservice.dto.CardInfoDTO;
import com.userservice.exception.CardInfoAlreadyExistsException;
import com.userservice.exception.CardInfoFoundAfterDeletingException;
import com.userservice.exception.CardInfoNotFoundException;
import com.userservice.mapper.CardInfoMapper;
import com.userservice.service.CardInfoServiceImpl;
import com.userservice.entity.CardInfo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import com.userservice.repository.CardInfoRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardInfoServiceTest {

    @Mock
    private CardInfoRepository cardInfoRepository;

    @Mock
    private CardInfoMapper cardInfoMapper;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private CardInfoServiceImpl cardInfoService;

    private static CardInfoDTO cardInfoDTO;
    private static CardInfo cardInfo;
    private static CardInfo saved;
    private static Pageable pageable;
    private static PageImpl<CardInfo> pageImpl;
    private static Page<CardInfo> page;
    private static PageImpl<CardInfoDTO> pageImplDTO;
    private static Page<CardInfoDTO> pageDTO;
    private static long id;

    @BeforeEach
    public void setUp() throws Exception {

        cardInfoDTO = new CardInfoDTO();
        cardInfoDTO.setNumber("1111 1111 1111 1111");
        cardInfoDTO.setHolder("DANILA RAINCHYK");
        cardInfoDTO.setExpirationDate("(11/29)");

        cardInfo = new CardInfo();
        cardInfo.setNumber("1111 1111 1111 1111");
        cardInfo.setHolder("DANILA RAINCHYK");
        cardInfo.setExpirationDate("(11/29)");

        saved = new CardInfo();
        saved.setId(1L);
        saved.setNumber("1111 1111 1111 1111");
        saved.setHolder("DANILA RAINCHYK");
        saved.setExpirationDate("(11/29)");

        pageable = Pageable.ofSize(10);
        pageImpl = new PageImpl<>(List.of(cardInfo), pageable, 1);
        page = pageImpl;

        pageImplDTO = new PageImpl<>(List.of(cardInfoDTO), pageable, 1);
        pageDTO = pageImplDTO;

        id = 1;

    }
    @Test
    public void testCreateCardInfo() throws Exception {
        when(cardInfoRepository.existsCardInfoByNumberAndHolderAndAndExpirationDate(anyString(), anyString(), anyString())).thenReturn(false);
        when(cardInfoMapper.toCardInfo(cardInfoDTO)).thenReturn(cardInfo);
        when(cardInfoRepository.existsById(1L)).thenReturn(true);
        when(cardInfoMapper.toCardInfoDTO(cardInfo)).thenReturn(cardInfoDTO);

        CardInfoDTO result = cardInfoService.createCardInfo(cardInfoDTO);

        Assertions.assertNotNull(result);
        verify(cardInfoRepository).createCardInfo(cardInfo);

        verify(cardInfoRepository, times(1)).createCardInfo(any(CardInfo.class));
    }

    @Test
    public void testCreateCardInfo__WhenCardInfoAlreadyExists() throws Exception {
        when(cardInfoRepository
                .existsCardInfoByNumberAndHolderAndAndExpirationDate(anyString(), anyString(), anyString()))
                .thenReturn(true);

        Assertions.assertThrows(CardInfoAlreadyExistsException.class,
                () -> cardInfoService.createCardInfo(cardInfoDTO));

        verify(cardInfoRepository, never()).createCardInfo(any(CardInfo.class));

    }

    @Test
    public void testCreateCardInfo__WhenCardNotFound() throws Exception {
        when(cardInfoRepository
                .existsCardInfoByNumberAndHolderAndAndExpirationDate(anyString(), anyString(), anyString()))
                .thenReturn(false);
        when(cardInfoMapper.toCardInfo(cardInfoDTO)).thenReturn(cardInfo);
        when(cardInfoRepository.existsById(cardInfo.getId())).thenReturn(false);

        Assertions.assertThrows(CardInfoNotFoundException.class,
                () -> cardInfoService.createCardInfo(cardInfoDTO));

        verify(cardInfoRepository, times(1)).createCardInfo(any(CardInfo.class));

    }

    @Test
    public void testGetCardInfo() throws Exception {

        when(cardInfoRepository.getCardInfoById(id)).thenReturn(Optional.of(saved));
        when(cardInfoMapper.toCardInfoDTO(saved)).thenReturn(cardInfoDTO);

        CardInfoDTO result = cardInfoService.getCardInfo(id);

        Assertions.assertNotNull(result);
        verify(cardInfoRepository).getCardInfoById(id);

    }

    @Test
    public void testGetCardInfo__WhenCardInfoNotFound() throws Exception {

        when(cardInfoRepository.getCardInfoById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(CardInfoNotFoundException.class,
                () -> cardInfoService.getCardInfo(id));

    }

    @Test
    public void testGetAllCardsInfo() throws Exception {

        when(cardInfoRepository.getAllCardsInfo(pageable)).thenReturn(page);
        when(cardInfoMapper.toCardInfoDTOPage(page)).thenReturn(pageDTO);

        Page<CardInfoDTO> result = cardInfoService.getAllCardsInfo(pageable);

        Assertions.assertNotNull(result);

        verify(cardInfoRepository).getAllCardsInfo(any(Pageable.class));
    }

    @Test
    public void testGetAllCardsInfo__WhenCardInfoNotFound() throws Exception {
        when(cardInfoRepository.getAllCardsInfo(pageable)).thenReturn(Page.empty());

        Assertions.assertThrows(CardInfoNotFoundException.class,
                () -> cardInfoService.getAllCardsInfo(pageable));
    }

    @Test
    public void testUpdateCardInfoById() throws Exception {

        when(cardInfoRepository.existsById(id)).thenReturn(true);
        when(cardInfoMapper.toCardInfo(cardInfoDTO)).thenReturn(cardInfo);
        when(cardInfoRepository.getCardInfoById(id)).thenReturn(Optional.of(saved));
        when(cardInfoMapper.toCardInfoDTO(saved)).thenReturn(cardInfoDTO);

        CardInfoDTO result = cardInfoService.updateCardInfoById(id, cardInfoDTO);

        Assertions.assertNotNull(result);
        verify(cardInfoRepository, times(1))
                .updateCardInfoById(eq(id), eq(cardInfo));
        verify(cardInfoRepository, times(1)).flush();
    }

    @Test
    public void testUpdateCardInfoById__WhenCardInfoNotFoundBeforeUpdating() throws Exception {

        when(cardInfoRepository.existsById(id)).thenReturn(false);

        Assertions.assertThrows(CardInfoNotFoundException.class,
                () -> cardInfoService.updateCardInfoById(id, cardInfoDTO));

        verify(cardInfoRepository, never()).updateCardInfoById(eq(id), eq(cardInfo));
    }

    @Test
    public void testUpdateCardInfoById__WhenCardInfoNotFoundAfterUpdating() throws Exception {

        when(cardInfoRepository.existsById(id)).thenReturn(false);
        when(cardInfoMapper.toCardInfo(cardInfoDTO)).thenReturn(cardInfo);
        when(cardInfoRepository.getCardInfoById(id)).thenReturn(Optional.empty());
        Assertions.assertThrows(CardInfoNotFoundException.class,
                () -> cardInfoService.updateCardInfoById(id, cardInfoDTO));

        verify(cardInfoRepository, times(1)).updateCardInfoById(eq(id), eq(cardInfo));
        verify(cardInfoRepository, times(1)).flush();
    }

    @Test
    public void testDeleteCardInfoById() throws Exception {

        when(cardInfoRepository.existsById(id)).thenReturn(true)
                .thenReturn(false);

        cardInfoService.deleteCardInfo(id);

        verify(cardInfoRepository, times(1)).deleteCardInfoById(eq(id));
        verify(cardInfoRepository, times(1)).flush();
    }

    @Test
    public void testDeleteCardInfoById__WhenCardInfoNotFoundBeforeDeleting() throws Exception {

        when(cardInfoRepository.existsById(id)).thenReturn(false);

        Assertions.assertThrows(CardInfoNotFoundException.class,
                () -> cardInfoService.deleteCardInfo(id));

        verify(cardInfoRepository, never()).deleteCardInfoById(eq(id));
    }

    @Test
    public void testDeleteCardInfoById__WhenCardInfoFoundAfterDeleting() throws Exception {

        when(cardInfoRepository.existsById(id)).thenReturn(true)
        .thenReturn(true);
        Assertions.assertThrows(CardInfoFoundAfterDeletingException.class,
                () -> cardInfoService.deleteCardInfo(id));

        verify(cardInfoRepository, times(1)).deleteCardInfoById(eq(id));
        verify(cardInfoRepository, times(1)).flush();
    }

}
