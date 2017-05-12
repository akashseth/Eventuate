package com.bhumca2017.eventuate;

public class ExpenditureDetails {

    String Email, date, amount, details;
    Integer sno;

    public ExpenditureDetails(String Email, Integer sno, String date, Integer amount, String details)
    {
        this.setEmail(Email);
        this.setSno(sno);
        this.setDate(date);
        this.setAmount(amount.toString());
        this.setDetails(details);
    }

    public String getEmail() { return Email; }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public Integer getSno() { return sno; }

    public void setSno(Integer sno) {
        this.sno = sno;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
