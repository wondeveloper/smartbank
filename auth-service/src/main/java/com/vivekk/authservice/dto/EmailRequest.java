package com.vivekk.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class EmailRequest implements Serializable {

    @NotBlank
    @NotNull
    private String emailHash;
}
