package com.example.soumyaagarwal.customerapp.Model;

/**
 * Created by RajK on 04-06-2017.
 */

public class CompletedBy {
    private String empId;
    private String dateassigned;
    private String datecompleted;
    private String note;


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getDateassigned() {
        return dateassigned;
    }

    public void setDateassigned(String dateassigned) {
        this.dateassigned = dateassigned;
    }

    public String getDatecompleted() {
        return datecompleted;
    }

    public void setDatecompleted(String datecompleted) {
        this.datecompleted = datecompleted;
    }

    public CompletedBy() {
    }

    public CompletedBy(String empId, String dateassigned, String datecompleted, String note) {
        this.empId = empId;
        this.dateassigned = dateassigned;
        this.datecompleted = datecompleted;
        this.note = note;
    }

}
