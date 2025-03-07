package com.example.satisotomasyonu;

public class Customer {
    private String name,surname;
    private Long trId,customerId;
    private int gender;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Long getTrId() {
        return trId;
    }

    public void setTrId(Long trId) {
        this.trId = trId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public Customer(String name, String surname, Long trId, int gender) {
        this.name = name;
        this.surname = surname;
        this.trId = trId;
        this.gender = gender;
    }

    public Customer(String name, String surname, Long trId, Long customerId, int gender) {
        this.name = name;
        this.surname = surname;
        this.trId = trId;
        this.customerId = customerId;
        this.gender = gender;
    }
    protected void addCustomer(){
        this.customerId= Login.getDb().addCustomer(this);
    }
}
