package com.example.satisotomasyonu;

import java.sql.SQLException;

class Login {
    private String username,password,valData;
    private static DbManager db=new DbManager();
    Login(String username, String password) {
        this.username=username;
        this.password=password;

    }
    protected String tryLogin() throws SQLException, DisabledUser {

        if (db.Login(username, password)){
            valData = db.getValData();
            return valData;
        }
        else
            return "-1";
    }
     static DbManager getDb(){
        return db;
    }

}
