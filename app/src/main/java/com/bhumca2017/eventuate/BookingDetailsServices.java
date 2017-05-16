package com.bhumca2017.eventuate;

/**
 * Created by Dell on 20-04-2017.
 */

public class BookingDetailsServices {

    Integer sno, amountPaid, amountDue,mQuantity,mServiceId,mBookingId;
    String date, typeService, mUserName, serviceSpecification,mBookingStatus,mUserEmail,mBookingDate;

    public BookingDetailsServices(Integer sno, String date, String typeService,
                                   String userName, String serviceSpecification,
                                   Integer amountPaid, Integer amountDue, String userEmail,
                                   String bookingStatus, Integer quantity,Integer serviceId, Integer bookingId, String bookingDate)
    {
        this.sno = sno;
        this.date = date;
        this.typeService = typeService;
        mUserName = userName;
        this.serviceSpecification = serviceSpecification;
        this.amountPaid = amountPaid;
        this.amountDue = amountDue;
        mBookingStatus = bookingStatus;
        mUserEmail = userEmail;
        mQuantity = quantity;
        mServiceId = serviceId;
        mBookingId = bookingId;
        mBookingDate =bookingDate;

    }

    public void setSno(Integer sno) {
        this.sno = sno;
    }

    public Integer getSno() {
        return sno;
    }

    public void setdate(String date) {
        this.date = date;
    }

    public String getdate() {
        return date;
    }

    public void setTypeService(String typeService) {
        this.typeService = typeService;
    }

    public String getTypeService() {
        return typeService;
    }



    public String getNameUser() {
        return mUserName;
    }

    public void setServiceSpecification(String serviceSpecification) {
        this.serviceSpecification = serviceSpecification;
    }

    public String getServiceSpecification() {
        return serviceSpecification;
    }

    public void setAmountPaid(Integer amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Integer getAmountPaid() {
        return amountPaid;
    }

    public void setAmountDue(Integer amountDue) {
        this.amountDue = amountDue;
    }

    public Integer getAmountDue() {
        return amountDue;
    }

    public String getUserEmail(){

        return  mUserEmail;
    }

    public String getBookingStatus(){

        return mBookingStatus;
    }
    public Integer getQuantity(){

        return mQuantity;
    }

    public Integer getServiceId(){

        return mServiceId;
    }
    public Integer getBookingId(){

        return mBookingId;
    }
    public String getBookingDate (){

        return mBookingDate;
    }
}
