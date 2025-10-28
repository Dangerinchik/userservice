package com.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "card_info", indexes = {
        @Index(name = "idx_card_info_user_id", columnList = "user_id"),
        @Index(name = "idx_card_info_number_holder_expiration_date", columnList = "number, holder, expiration_date")
})
@AllArgsConstructor
@NoArgsConstructor
public class CardInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "number", nullable = false, length = 19)
    private String number;

    @Column(name = "holder", nullable = false, length = 50)
    private String holder;

    @Column(name = "expiration_date", nullable = false, length = 7)
    private String expirationDate;

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public String getHolder() {
        return holder;
    }
    public void setHolder(String holder) {
        this.holder = holder;
    }
    public String getExpirationDate() {
        return expirationDate;
    }
    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

}
