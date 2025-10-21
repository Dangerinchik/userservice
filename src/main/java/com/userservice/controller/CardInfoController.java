package com.userservice.controller;


import com.userservice.dto.CardInfoDTO;
import com.userservice.service.CardInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/card")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CardInfoController {
    private final CardInfoService cardInfoService;

    @GetMapping("/{id}")
    public ResponseEntity<CardInfoDTO> createCardInfo(@PathVariable Long id) {
        return ResponseEntity.ok(cardInfoService.getCardInfo(id));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<CardInfoDTO>> getAllCardsInfo(){
        Page<CardInfoDTO> response = cardInfoService.getAllCardsInfo(Pageable.ofSize(10));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<CardInfoDTO> createCardInfo(@Valid @RequestBody CardInfoDTO cardInfoDTO) {
        CardInfoDTO response = cardInfoService.createCardInfo(cardInfoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<CardInfoDTO> updateCardInfo(@PathVariable Long id, @Valid @RequestBody CardInfoDTO cardInfoDTO) {
        CardInfoDTO response = cardInfoService.updateCardInfoById(id, cardInfoDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteCardInfo(@PathVariable Long id) {
        cardInfoService.deleteCardInfo(id);
        return ResponseEntity.noContent().build();
    }
}
