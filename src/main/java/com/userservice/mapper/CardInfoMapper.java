package com.userservice.mapper;

import com.userservice.dto.CardInfoDTO;
import com.userservice.entity.CardInfo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.Page;

import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CardInfoMapper {

    CardInfoDTO toCardInfoDTO(CardInfo cardInfo);

    CardInfo toCardInfo(CardInfoDTO cardInfoDTO);

    Page<CardInfoDTO> toCardInfoDTOPage(Page<CardInfo> cardInfoPage);

    Set<CardInfoDTO> ToCardInfoDTOSet(Set<CardInfo> cards);


}
