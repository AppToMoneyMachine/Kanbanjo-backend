package com.bliutvikler.bliutvikler.user.dto;

import java.util.List;

public class UserInfoResponse {
    private String username;
    private String email;
    private List<String> roles;
    private Long id;

    public UserInfoResponse(String username, String email, List<String> roles, Long id) {
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public Long getId() {
        return id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

}
