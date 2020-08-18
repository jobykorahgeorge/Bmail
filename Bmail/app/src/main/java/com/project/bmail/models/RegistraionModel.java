package com.project.bmail.models;

public class RegistraionModel {
    public String name;
    public String email;
    public String password;

    public RegistraionModel() {
    }

    public RegistraionModel(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
