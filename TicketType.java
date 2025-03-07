package com.example.satisotomasyonu;

public class TicketType {
    private Integer id,discountRate;
    private String title;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(Integer discountRate) {
        this.discountRate = discountRate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TicketType(Integer discountRate, String title){
        this.discountRate=discountRate;
        this.title=title;
    }
    public TicketType(Integer id ,String title,Integer discountRate){
        this.title=title;
        this.discountRate=discountRate;
        this.id=id;
    }
}
