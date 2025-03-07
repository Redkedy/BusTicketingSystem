package com.example.satisotomasyonu;

public class Ticket {
    private Route route;
    private Long id,cellPhone;
    private String date;
    private Integer seatNo,groupId,type;
    private Double price;
    private Customer customer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Route getRouteId() {
        return route;
    }

    public void setRouteId(Route route) {
        this.route = route;
    }

    public Customer getCustomerId() {
        return customer;
    }

    public void setCustomerId(Customer customerId) {
        this.customer = customerId;
    }

    public Long getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(Long cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(Integer seatNo) {
        this.seatNo = seatNo;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public  Ticket(Route route, Long cellPhone, String date, Integer seatNo, Integer groupId, Integer type, Double price,Customer customer) {
        this.route = route;
        this.cellPhone = cellPhone;
        this.date = date;
        this.seatNo = seatNo;
        this.groupId = groupId;
        this.type = type;
        this.price = price;
        this.customer=customer;
    }
    public Ticket(Long id,Route route, Long cellPhone, String date, Integer seatNo, Integer groupId, Integer type, Double price,Customer customer){
        this( route,  cellPhone,  date,  seatNo,  groupId,  type,  price,customer);
        this.id=id;
    }
    protected void addTicket(){
        this.id= Login.getDb().addTicket(this);
    }
}
