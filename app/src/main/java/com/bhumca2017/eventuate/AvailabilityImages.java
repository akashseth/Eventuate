package com.bhumca2017.eventuate;

/**
 * Created by toaka on 29-04-2017.
 */

public class AvailabilityImages {

    private int mImageId;
    private String mImageLocation;

    public  AvailabilityImages(int imageId,String imageLocation) {

        mImageId = imageId;
        mImageLocation = imageLocation;
    }

    public int getImageId() {

        return mImageId;
    }

    public String getImageLocation() {

        return mImageLocation;
    }
}
