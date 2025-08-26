package com.vivekk.authservice.dto;

import com.vivekk.authservice.utils.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class AuthRequest implements Serializable {

    @NotBlank
    @Size(min = 5, max = 30)
    private String email;
    @ValidPassword
    private String password;
    private String grantType;
}
