package com.bhumca2017.eventuate;

/**
 * Created by toaka on 02-05-2017.
 */

public class AvailabilityItemsOrganiser {

    private Integer mServiceAvailabilityId;
    private Integer mServiceProviderId;
    private Integer mAvailabilityId;
    private String mAvailabilityName;
    private String mPrice;
    private String mServiceProviderName;

    public AvailabilityItemsOrganiser(Integer serviceAvailabilityId,Integer serviceProviderId,Integer availabilityId,
                                      String availabilityName, String price,String serviceProviderName){

        mServiceAvailabilityId = serviceAvailabilityId;
        mServiceProviderId = serviceProviderId;
        mAvailabilityId = availabilityId;
        mAvailabilityName = availabilityName;
        mPrice = price;
        mServiceProviderName =serviceProviderName;
    }

    public Integer getServiceAvailabilityId() {

        return mServiceAvailabilityId;
    }

    public Integer getServiceProviderId() {

        return mServiceProviderId;
    }

    public Integer getAvailabilityId() {

        return mAvailabilityId;
    }

    public String getAvailabilityName() {

        return mAvailabilityName;
    }

    public String getPrice() {

        return mPrice;
    }
    public String getServiceProviderName() {

        return mServiceProviderName;
    }

}
