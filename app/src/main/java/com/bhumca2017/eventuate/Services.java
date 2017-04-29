package com.bhumca2017.eventuate;

/**
 * Created by toaka on 24-03-2017.
 */

public class Services {

    private String mServiceName;
    private int mServiceImageResourceId;
    private int mServiceId;

    public Services(String serviceName, int serviceId, int serviceImageResourceId)
    {
        mServiceName=serviceName;
        mServiceId=serviceId;
        mServiceImageResourceId=serviceImageResourceId;
    }

    String getServiceName()
    {
        return mServiceName;
    }

    int getServiceId()
    {
        return mServiceId;
    }

    int getServiceImagesourceId()
    {
        return mServiceImageResourceId;
    }

}
