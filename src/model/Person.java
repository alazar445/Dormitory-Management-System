package model;

public abstract class Person {
    protected String fullName;
    protected String gender;
    protected String contactInfo;

    // Constructor
    public Person(String fullName, String gender, String contactInfo) {
        this.fullName = fullName;
        this.gender = gender;
        this.contactInfo = contactInfo;
    }

    // Getters
    public String getFullName() {
        return fullName;
    }

    public String getGender() {
        return gender;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    // Setters
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}
