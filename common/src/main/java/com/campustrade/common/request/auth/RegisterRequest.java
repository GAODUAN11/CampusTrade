package com.campustrade.common.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * Register request object.
 */
public class RegisterRequest implements Serializable {
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 32, message = "Username length must be 3-32")
    private String username;

    @NotBlank(message = "Nickname cannot be blank")
    @Size(max = 32, message = "Nickname length must be <= 32")
    private String nickname;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 64, message = "Password length must be 6-64")
    private String password;

    @NotBlank(message = "Confirm password cannot be blank")
    private String confirmPassword;

    @Pattern(regexp = "^1\\d{10}$", message = "Phone format is invalid")
    private String phone;

    @Email(message = "Email format is invalid")
    private String email;

    @Size(max = 64, message = "School length must be <= 64")
    private String school;

    @Size(max = 64, message = "Campus length must be <= 64")
    private String campus;

    @NotNull(message = "agreeTerms is required")
    private Boolean agreeTerms;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public Boolean getAgreeTerms() {
        return agreeTerms;
    }

    public void setAgreeTerms(Boolean agreeTerms) {
        this.agreeTerms = agreeTerms;
    }
}
