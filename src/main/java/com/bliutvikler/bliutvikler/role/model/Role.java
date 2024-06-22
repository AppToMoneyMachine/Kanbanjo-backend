package com.bliutvikler.bliutvikler.role.model;

import com.bliutvikler.bliutvikler.user.model.User;
import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;


    public Role() {
        // Nødvendig tom konstruktør for JPA
    }

    public Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // setters
    public void setName(String name) {
        this.name = name;
    }
}
