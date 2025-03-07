package com.example.satisotomasyonu;

public class Driver {
    private String name,surname,address,dateOfBirth;
    private int id,gender;
    private long cellphone,TRid;

    public Driver(int id,String name,String surname,String address,String dateOfBirth,int gender,long cellphone,long TRid){
        this.id=id;
        this.name=name;
        this.surname=surname;
        this.address=address;
        this.dateOfBirth=dateOfBirth;
        this.gender=gender;
        this.cellphone=cellphone;
        this.TRid=TRid;
    }
    protected void setId(int id){this.id=id;}
    protected String getName(){
        return this.name;
    }
    protected String getSurname(){
        return this.surname;
    }
}
