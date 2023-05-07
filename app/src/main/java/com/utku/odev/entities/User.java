package com.utku.odev.entities;

import android.graphics.Bitmap;

import com.google.firebase.storage.StorageReference;

public class User {

    private String id;
    private String name;
    private String lastname;
    private int entranceYear;
    private int gradYear;
    private String email;
    private String password;

    private String country;
    private String address;

    public User(String id, String name, String lastname, int entranceYear, int gradYear, String email, String password, String address, String company, String contact, String education) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.entranceYear = entranceYear;
        this.gradYear = gradYear;
        this.email = email;
        this.password = password;
        this.address = address;
        this.company = company;
        this.contact = contact;
        this.education = education;
    }

    private String company;
    private String contact;
    private String education;


    public User(){

    }
    public User(String id, String name, String lastname, int entranceYear, int gradYear, String email, String password) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.entranceYear = entranceYear;
        this.gradYear = gradYear;
        this.email = email;
        this.password = password;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getEntranceYear() {
        return entranceYear;
    }

    public void setEntranceYear(int entranceYear) {
        this.entranceYear = entranceYear;
    }

    public int getGradYear() {
        return gradYear;
    }

    public void setGradYear(int gradYear) {
        this.gradYear = gradYear;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }




    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCompany() {
        return company;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
