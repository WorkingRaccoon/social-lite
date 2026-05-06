package com.workingraccoon.backend.DTO;

import lombok.Data;

@Data
public class User {
    private String Username;
    private String Phone;
    private String Password;
    private String Email;
    private String Intro;
}
