package com.example.soumyaagarwal.customerapp.Model;

import java.io.Serializable;

/**
 * Created by RajK on 03-06-2017.
 */

public class Task implements Serializable {
    private String taskId,name,startDate,expEndDate,quotationId,qty,desc,customerId;
    private int color = -1;
    private Boolean measurementApproved;

    public Boolean getMeasurementApproved() {
        return measurementApproved;
    }

    public void setMeasurementApproved(Boolean measurementApproved) {
        this.measurementApproved = measurementApproved;
    }


    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Task() {
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getExpEndDate() {
        return expEndDate;
    }

    public void setExpEndDate(String expEndDate) {
        this.expEndDate = expEndDate;
    }

    public String getQuotationId() {
        return quotationId;
    }

    public void setQuotationId(String quotationId) {
        this.quotationId = quotationId;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Task(String taskId, String name, String startDate, String expEndDate, String qty, String desc, String customerId, int color) {
        this.taskId = taskId;
        this.name = name;
        this.startDate = startDate;
        this.expEndDate = expEndDate;
        this.qty = qty;
        this.desc = desc;
        this.customerId = customerId;
        this.color = color;
        measurementApproved=false;
    }
}
