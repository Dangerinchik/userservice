package com.userservice.dto;

import com.userservice.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
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
    @Size(min = 7, max = 7)
    @Pattern(regexp = "((0[1-9]|1[0-2])/\\d{2})")
    private String expirationDate;

    private Long userId;

}
