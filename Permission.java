package com.example.satisotomasyonu;

public class Permission {
    private int permissionId;
    private String permissionTitle;

    public Permission(int id ,String title){
        this.permissionId=id;
        this.permissionTitle=title;

    }
    protected int getPermissionId(){
        return permissionId;
    }
    protected String getPermissionTitle(){
        return permissionTitle;
    }



}
