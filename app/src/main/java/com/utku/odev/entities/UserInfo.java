package com.utku.odev.entities;

public class UserInfo extends  User{

     private String country;
     private String address;
     private String company;
     private String contact;
     private String education;


    public UserInfo(User user, String address, String company, String contact, String education) {
        super(user.getId(), user.getName(), user.getLastname(), user.getEntranceYear(), user.getGradYear(), user.getEmail(), user.getPassword());
        this.country = country;
        this.address = address;
        this.company = company;
        this.contact = contact;
        this.education = education;
    }
    public UserInfo(User user){
        super(user.getId(), user.getName(), user.getLastname(), user.getEntranceYear(), user.getGradYear(), user.getEmail(), user.getPassword());
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return address;
    }

    public void setCity(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }
}
