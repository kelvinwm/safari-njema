package com.beyondthehorizon.safarinjema.models;

public class SaccoDetails {
    private String saccoName;
    private String saccoNo;
    private String saccoPhone;
    private String saccoEmail;
    private String saccoPassword;

    public SaccoDetails() {
    }

    public SaccoDetails(String saccoName, String saccoNo, String saccoPhone,
                        String saccoEmail, String saccoPassword) {
        this.saccoName = saccoName;
        this.saccoNo = saccoNo;
        this.saccoPhone = saccoPhone;
        this.saccoEmail = saccoEmail;
        this.saccoPassword = saccoPassword;
    }

    public String getSaccoEmail() {
        return saccoEmail;
    }

    public String getSaccoPassword() {
        return saccoPassword;
    }

    public String getSaccoName() {
        return saccoName;
    }

    public String getSaccoNo() {
        return saccoNo;
    }

    public String getSaccoPhone() {
        return saccoPhone;
    }
}
