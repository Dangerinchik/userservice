package com.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ResponseCardInfoDTO {

    private long id;
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
}
