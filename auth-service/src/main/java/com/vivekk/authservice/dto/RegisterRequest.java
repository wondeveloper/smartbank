package com.vivekk.authservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterRequest implements Serializable {

    @NotBlank
    private String firstName;
    private String lastName;
    @NotBlank
    private String emailId;
    @NotNull
    private AuthRequest authRequest;

    public void setEmailId(String emailId) {
        this.emailId = emailId;
        // Also propagate to inner authRequest
        if (this.authRequest != null) {
            this.authRequest.setEmail(emailId);
        }
    }
}
