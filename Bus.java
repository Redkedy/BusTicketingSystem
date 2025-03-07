package com.example.satisotomasyonu;

public class Bus {
    private int id,firstReg,numberOfSeats;
    private String make,model,plate;

    public Bus(int id,int firstReg,int numberOfSeats,String make,String model,String plate){
        this(firstReg,numberOfSeats,make,model,plate);
        this.id=id;
    }
    public Bus(int firstReg,int numberOfSeats,String make,String model,String plate){
        this.firstReg=firstReg;
        this.make=make;
        this.model=model;
        this.plate=plate;
        this.numberOfSeats=numberOfSeats;
    }
    public int getId(){
        return this.id;
    }
    public String getPlate(){return this.plate;}
    protected int getNumberOfSeats(){return this.numberOfSeats;}
}
