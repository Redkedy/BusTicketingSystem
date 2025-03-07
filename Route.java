package com.example.satisotomasyonu;

public class Route {
    private int id,price;
    private Driver driver1,driver2,driver3;
    private String departureDate,arrivalDate,departureCity,arrivalCity;
    private Bus bus;

    public Route(int price,String departureDate,String arrivalDate,Bus bus,Driver driver1,String departureCity,String arrivalCity){
        this.price=price;
        this.departureDate=departureDate;
        this.arrivalDate=arrivalDate;
        this.bus=bus;
        this.driver1=driver1;
        this.departureCity=departureCity;
        this.arrivalCity=arrivalCity;

    }
    public Route(int price,String departureDate,String arrivalDate,Bus bus,Driver driver1,Driver driver2,String departureCity,String arrivalCity){
        this(price, departureDate, arrivalDate, bus, driver1, departureCity, arrivalCity);
        this.driver2=driver2;


    }
    public Route(int price,String departureDate,String arrivalDate,Bus bus,Driver driver1,Driver driver2,Driver driver3,String departureCity,String arrivalCity){
        this(price, departureDate, arrivalDate, bus, driver1, driver2, departureCity, arrivalCity);
        this.driver3=driver3;
    }
    public Route(int id,int price,String departureDate,String arrivalDate,Bus bus,Driver driver1,Driver driver2,Driver driver3,String departureCity,String arrivalCity){
        this(price, departureDate, arrivalDate, bus, driver1, driver2, departureCity, arrivalCity);
        this.driver3=driver3;
        this.id=id;
    }

    public String getDepartureDate(){return this.departureDate;}
    public String getArrivalDate(){return this.arrivalDate;}
    public String getDepartureCity(){return this.departureCity;}
    public String getArrivalCity(){return this.arrivalCity;}
    public Integer getId(){return Integer.valueOf(this.id);}
    public Integer getTicketPrice(){return Integer.valueOf(this.price);}
    public String getDriver1(){
        if(driver1!=null)
            return this.driver1.getName()+" "+this.driver1.getSurname();
        return "tanımlanmadı";
    }
    public String getDriver2(){
        if(driver2!=null)
            return this.driver2.getName()+" "+this.driver2.getSurname();
        return "tanımlanmadı";
    }
    public String getDriver3(){
        if(driver3!=null)
            return this.driver3.getName()+" "+this.driver3.getSurname();
        return "tanımlanmadı";
    }
    public int getBusId(){
        if(bus!=null)
        return Integer.valueOf(this.bus.getId());
        return Integer.valueOf(-1);
    }
    public String getBusPlate(){
        if(bus!=null)
            return this.bus.getPlate();
        return "tanımlanmadı";
    }
    protected Bus getBus(){return this.bus;}

}

