package com.inwi.KpiDashboardAuth.dtos;
    
public class RegisterUserDto {
    private String email;
    
    private String password;
    
    //private String fullName;

    /*modifications */
    private String firstName;
    private String lastName;
    
    // getters and setters here...


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
    /*public void setFullName(String fullName) {
        this.fullName = fullName;
    }*/
  /*   public String getFullName() {
        return fullName;
    } */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getFirstName() {
        return firstName;
    }
     public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getLastName() {
        return lastName;
    }
    
}