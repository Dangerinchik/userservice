package com.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardInfoDTO {

    @NotBlank
    @Size(min = 19, max=19)
    @Pattern(regexp = "\\d{4} \\d{4} \\d{4} \\d{4}")
    private String number;

    @NotBlank
    @Size(max = 50)
    @Pattern(regexp = "^[A-Z\\s']*$")
    private String holder;

    @NotBlank
    @Size(min = 5, max = 5)
    @Pattern(regexp = "((0[1-9]|1[0-2])/\\d{2})")
    private String expirationDate;

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

}
