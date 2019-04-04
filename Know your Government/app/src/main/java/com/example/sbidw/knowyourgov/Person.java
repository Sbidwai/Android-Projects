package com.example.sbidw.knowyourgov;

import java.io.Serializable;
import java.util.HashMap;

public class Person implements Serializable {

    String personName;
    String personDesignation;
    String party;
    String LineOne;
    String LineTwo;
    String city;
    String state;

    String email;
    String website;

    String zipcode;
    String phone;
    String urls;
    String photourls;
    HashMap<String,String> channels;


    public String getUrls() {
        return urls;
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }

    public String getPhotourls() {
        return photourls;
    }

    public void setPhotourls(String photourls) {
        this.photourls = photourls;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Person(String city, String state, String zipcode) {
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
    }

    public Person(String personDesignation) {
        this.personDesignation = personDesignation;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }


    public Person(){}


    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonDesignation() {
        return personDesignation;
    }

    public void setPersonDesignation(String personDesignation) {
        this.personDesignation = personDesignation;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getLineOne() {
        return LineOne;
    }

    public void setLineOne(String lineOne) {
        this.LineOne = lineOne;
    }

    public String getLineTwo() {
        return LineTwo;
    }

    public void setLineTwo(String lineTwo) {
        this.LineTwo = lineTwo;
    }

    public String getCity() {
        return city;
    }

    public HashMap<String, String> getChannels() {
        return channels;
    }

    public void setChannels(HashMap<String, String> channels) {
        this.channels = channels;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
