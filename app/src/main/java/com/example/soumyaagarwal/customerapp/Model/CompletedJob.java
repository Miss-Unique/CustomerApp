package com.example.soumyaagarwal.customerapp.Model;

/**
 * Created by RajK on 07-08-2017.
 */

public class CompletedJob {
    private String empId;
    private String dateassigned;
    private String datecompleted;
    private String assignedByUsername,assignedByName;
    private String coordinatorNote,empployeeNote;

    public String getAssignedByUsername() {
        return assignedByUsername;
    }

    public void setAssignedByUsername(String assignedByUsername) {
        this.assignedByUsername = assignedByUsername;
    }

    public String getAssignedByName() {
        return assignedByName;
    }

    public void setAssignedByName(String assignedByName) {
        this.assignedByName = assignedByName;
    }

    public CompletedJob(String empId, String dateassigned, String datecompleted, String assignedByUsername, String assignedByName, String coordinatorNote, String empployeeNote) {
        this.empId = empId;
        this.dateassigned = dateassigned;
        this.datecompleted = datecompleted;
        this.assignedByUsername = assignedByUsername;
        this.assignedByName = assignedByName;
        this.coordinatorNote = coordinatorNote;
        this.empployeeNote = empployeeNote;
    }

    public CompletedJob() {
    }

    public String getCoordinatorNote() {
        return coordinatorNote;
    }

    public void setCoordinatorNote(String coordinatorNote) {
        this.coordinatorNote = coordinatorNote;
    }

    public String getEmpployeeNote() {
        return empployeeNote;
    }

    public void setEmpployeeNote(String empployeeNote) {
        this.empployeeNote = empployeeNote;
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

}
