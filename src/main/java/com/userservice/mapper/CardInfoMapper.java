package com.userservice.mapper;

import com.userservice.dto.CardInfoDTO;
import com.userservice.entity.CardInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CardInfoMapper {


    CardInfoDTO toCardInfoDTO(CardInfo cardInfo);


    CardInfo toCardInfo(CardInfoDTO cardInfoDTO);

    default Page<CardInfoDTO> toCardInfoDTOPage(Page<CardInfo> cardInfoPage){
        List<CardInfoDTO> dtos = cardInfoPage.stream().map(this::toCardInfoDTO).toList();
        return new PageImpl<>(dtos, cardInfoPage.getPageable(), cardInfoPage.getTotalElements());
    }



}
