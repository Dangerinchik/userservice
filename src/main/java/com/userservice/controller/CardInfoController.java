package com.userservice.controller;


import com.userservice.dto.CardInfoDTO;
import com.userservice.exception.CardInfoAlreadyExistsException;
import com.userservice.exception.CardInfoFoundAfterDeletingException;
import com.userservice.exception.CardInfoNotFoundException;
import com.userservice.exception.UserNotFoundException;
import com.userservice.service.CardInfoService;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/card")
public class CardInfoController {
    private final CardInfoService cardInfoService;

    @Autowired
    public CardInfoController(CardInfoService cardInfoService) {
        this.cardInfoService = cardInfoService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardInfoDTO> getCardInfo(@PathVariable Long id) throws CardInfoNotFoundException {
        return ResponseEntity.ok(cardInfoService.getCardInfo(id));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<CardInfoDTO>> getAllCardsInfo(@RequestParam("offset") Integer offset,
                                                             @RequestParam("limit") Integer limit) throws CardInfoNotFoundException {
        Page<CardInfoDTO> response = cardInfoService.getAllCardsInfo(PageRequest.of(offset, limit));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<CardInfoDTO> createCardInfo(@Valid @RequestBody CardInfoDTO cardInfoDTO) throws UserNotFoundException, CardInfoAlreadyExistsException, CardInfoNotFoundException {
        CardInfoDTO response = cardInfoService.createCardInfo(cardInfoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<CardInfoDTO> updateCardInfo(@PathVariable Long id, @Valid @RequestBody CardInfoDTO cardInfoDTO) throws CardInfoNotFoundException {
        CardInfoDTO response = cardInfoService.updateCardInfoById(id, cardInfoDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteCardInfo(@PathVariable Long id) throws CardInfoNotFoundException, CardInfoFoundAfterDeletingException {
        cardInfoService.deleteCardInfo(id);
        return ResponseEntity.noContent().build();
    }
}
