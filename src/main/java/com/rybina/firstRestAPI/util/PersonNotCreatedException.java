package com.rybina.firstRestAPI.util;

public class PersonNotCreatedException extends RuntimeException{
    public PersonNotCreatedException(String msg) {
        super(msg);
    }
}
