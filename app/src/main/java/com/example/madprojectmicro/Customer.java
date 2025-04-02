package com.example.madprojectmicro;

import java.util.Date;

public class Customer {
    private int id;
    private String name;
    private String mobileNo;
    private String gender;
    private String nationality;
    private String aadhar;
    private String address;
    private String email;
    private String phone;
    private int roomNoAllocated;
    private String roomType;
    private Date checkInDate;
    private int noOfDays;
    private boolean occupiedOrNot;
    private int noOfBeds;


    public Customer() {
        // Default constructor
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getAadhar() {
        return aadhar;
    }

    public void setAadhar(String aadhar) {
        this.aadhar = aadhar;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getRoomNoAllocated() {
        return roomNoAllocated;
    }

    public void setRoomNoAllocated(int roomNoAllocated) {
        this.roomNoAllocated = roomNoAllocated;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public Date getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }

    public int getNoOfDays() {
        return noOfDays;
    }

    public void setNoOfDays(int noOfDays) {
        this.noOfDays = noOfDays;
    }

    public int getNoOfBeds() {
        return noOfBeds;
    }

    public void setNoOfBeds(int noOfBeds) {
        this.noOfBeds = noOfBeds;
    }


    public boolean isOccupiedOrNot() {
        return occupiedOrNot;
    }

    public void setOccupiedOrNot(boolean occupiedOrNot) {
        this.occupiedOrNot = occupiedOrNot;
    }
}