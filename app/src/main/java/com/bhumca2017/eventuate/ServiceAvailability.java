package com.bhumca2017.eventuate;

/**
 * Created by toaka on 03-04-2017.
 */

public class ServiceAvailability {
    private String  mAvailabilityName;
    private String  mPrice;
    private int mAvailabilityId;
    private int mServiceAvailabilityId;
    private String mQuantity;

    public ServiceAvailability(String availabilityName, String price, int availabilityId, int serviceAvailabilityId ,String quantity)
    {
        mAvailabilityName=availabilityName;
        mPrice=price;
        mAvailabilityId=availabilityId;
        mServiceAvailabilityId = serviceAvailabilityId;
        mQuantity = quantity;
    }

    public String getAvailabilityName()
    {
        return mAvailabilityName;
    }

    public String getPrice()
    {
        return mPrice;
    }

    public int getAvailabilityId()
    {
        return mAvailabilityId;
    }

    public int getServiceAvailabilityId() {
        return mServiceAvailabilityId;
    }

    public String getQuantity(){
        return mQuantity;
    }
}
