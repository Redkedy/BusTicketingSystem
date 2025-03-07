package com.example.satisotomasyonu;

public class WrongPassOrNameException extends Exception{
    public WrongPassOrNameException(String error){
        super(error);

    }
}
