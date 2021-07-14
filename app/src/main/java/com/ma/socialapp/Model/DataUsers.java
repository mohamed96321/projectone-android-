package com.ma.socialapp.Model;

public class DataUsers {

    private String userId, accepted, careerImage, profileImage, fullName, phoneNumber, city, address, country, career, specification;

    public DataUsers(){

    }

    public DataUsers(String userId, String accepted, String careerImage, String profileImage, String fullName, String phoneNumber, String city, String address, String country, String career, String specification) {
        this.userId = userId;
        this.accepted = accepted;
        this.careerImage = careerImage;
        this.profileImage = profileImage;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.address = address;
        this.country = country;
        this.career = career;
        this.specification = specification;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccepted() {
        return accepted;
    }

    public void setAccepted(String accepted) {
        this.accepted = accepted;
    }

    public String getCareerImage() {
        return careerImage;
    }

    public void setCareerImage(String careerImage) {
        this.careerImage = careerImage;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }
}
