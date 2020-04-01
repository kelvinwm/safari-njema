package com.beyondthehorizon.safarinjema.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class TripModel {
    String saccoName;
    String driverName;
    String conductorName;
    String numberPlate;
    String from;
    String destination;
    String dateTime;
    String names;
    String idNo;
    String phoneNo;
    String passengerCount;
    String seatNo;

    public TripModel() {
    }

    public TripModel(String saccoName, String driverName, String conductorName, String numberPlate,
                     String from, String destination, String dateTime, String names, String idNo,
                     String phoneNo, String passengerCount, String seatNo) {
        this.saccoName = saccoName;
        this.driverName = driverName;
        this.conductorName = conductorName;
        this.numberPlate = numberPlate;
        this.from = from;
        this.destination = destination;
        this.dateTime = dateTime;
        this.names = names;
        this.idNo = idNo;
        this.phoneNo = phoneNo;
        this.passengerCount = passengerCount;
        this.seatNo = seatNo;
    }

    public String getSeatNo() {
        return seatNo;
    }

    public String getSaccoName() {
        return saccoName;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getConductorName() {
        return conductorName;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    public String getFrom() {
        return from;
    }

    public String getDestination() {
        return destination;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getNames() {
        return names;
    }

    public String getIdNo() {
        return idNo;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getPassengerCount() {
        return passengerCount;
    }
}
