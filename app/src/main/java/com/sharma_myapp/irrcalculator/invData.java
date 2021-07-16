package com.sharma_myapp.irrcalculator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Date;

public class invData {
    String inDay;
    double amount;
    Date acc_date;
    int sno;
    public invData(String i,double a,int sno) {
        inDay=i;
        amount=a;
        try {
            acc_date= new SimpleDateFormat("dd/MM/YY").parse(inDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.sno=sno;

    }
    public String getInDay() {
        return inDay;
    }

    public double getAmount() {
        return amount;
    }
    public Date getAccDate() {
        return acc_date;
    }
    public int getSno() {
        return sno;
    }

}
