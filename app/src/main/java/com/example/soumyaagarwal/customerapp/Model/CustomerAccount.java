package com.example.soumyaagarwal.customerapp.Model;

/**
 * Created by RajK on 17-06-2017.
 */

public class CustomerAccount {
    private Integer advance,total;


    public CustomerAccount() {

    }

    public CustomerAccount(Integer advance, Integer total) {
        this.advance = advance;
        this.total = total;
    }

    public Integer getAdvance() {
        return advance;
    }

    public void setAdvance(Integer advance) {
        this.advance = advance;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
