package model;

public class Student extends Person {
    private int studentId;
    private String department;
    private int enrollmentYear;
    private String currentStatus;
    private String sponsorshipStatus;

    // Constructor
    public Student(int studentId, String fullName, String gender, String contactInfo,
                   String department, int enrollmentYear, String currentStatus, String sponsorshipStatus) {
        super(fullName, gender, contactInfo);
        this.studentId = studentId;
        this.department = department;
        this.enrollmentYear = enrollmentYear;
        this.currentStatus = currentStatus;
        this.sponsorshipStatus = sponsorshipStatus;
    }

    // Getters
    public int getStudentId() {
        return studentId;
    }

    public String getDepartment() {
        return department;
    }

    public int getEnrollmentYear() {
        return enrollmentYear;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public String getSponsorshipStatus() {
        return sponsorshipStatus;
    }

    // Setters
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setEnrollmentYear(int enrollmentYear) {
        this.enrollmentYear = enrollmentYear;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public void setSponsorshipStatus(String sponsorshipStatus) {
        this.sponsorshipStatus = sponsorshipStatus;
    }
}
