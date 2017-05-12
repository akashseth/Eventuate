package com.bhumca2017.eventuate;

/**
 * Created by Dell on 20-04-2017.
 */

public class BookingDetailsOrganizer {

    Integer sno, amountPaid, amountDue,mServiceProviderId,mQuantity,mServiceId;
    String date, typeService, nameServiceProvider, serviceSpecification,mBookingStatus;

    public BookingDetailsOrganizer(Integer sno, String date, String typeService,
                                   String nameServiceProvider, String serviceSpecification,
                                   Integer amountPaid, Integer amountDue, Integer serviceProviderId,
                                   String bookingStatus, Integer quantity,Integer serviceId)
    {
        this.sno = sno;
        this.date = date;
        this.typeService = typeService;
        this.nameServiceProvider = nameServiceProvider;
        this.serviceSpecification = serviceSpecification;
        this.amountPaid = amountPaid;
        this.amountDue = amountDue;
        mBookingStatus = bookingStatus;
        mServiceProviderId = serviceProviderId;
        mQuantity = quantity;
        mServiceId = serviceId;

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

    public void setNameServiceProvider(String nameServiceProvider) {
        this.nameServiceProvider = nameServiceProvider;
    }

    public String getNameServiceProvider() {
        return nameServiceProvider;
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

    public Integer getServiceProviderId(){

        return  mServiceProviderId;
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
}
