package com.example.satisotomasyonu;

public class User extends Role{

    private String username,password,name,surName,role_title,valData;
    private int role_id;
    private long cellPhone;

    public User(String username,String password){
        this.username=username;
        this.password=password;
        this.role_id=Login.getDb().getRole_id();
        this.role_title=Login.getDb().getRole_title();
       this.cellPhone=Login.getDb().getCellPhone();
        this.name=Login.getDb().getName();
        this.surName=Login.getDb().getSurName();
        this.valData=Login.getDb().getValData();

    }
    protected String getUsername(){return this.username;}
    protected String getPassword(){return this.password;}
    protected long getCellPhone(){return this.cellPhone;}
    protected void setCellPhone(long phone){
        this.cellPhone=phone;
    }
    protected String getName(){return this.name;}
    protected void setPassword(String hashed){
        this.password=hashed;
    }
    protected void setValData(String hashed){
        this.valData=hashed;
    }




}
