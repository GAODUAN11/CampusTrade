package com.campustrade.common.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * Login request object.
 */
public class LoginRequest implements Serializable {
    @NotBlank(message = "Account cannot be blank")
    @Size(max = 64, message = "Account length must be <= 64")
    private String account;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 64, message = "Password length must be 6-64")
    private String password;

    @NotNull(message = "rememberMe is required")
    private Boolean rememberMe = Boolean.FALSE;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
}
