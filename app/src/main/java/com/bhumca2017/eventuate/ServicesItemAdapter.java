package com.bhumca2017.eventuate;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by toaka on 24-03-2017.
 */

public class ServicesItemAdapter extends ArrayAdapter<Services> {

    public ServicesItemAdapter(Activity context, ArrayList<Services> services) {
        super(context, 0, services);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View servicesItemView = convertView;
        if (servicesItemView == null) {
            servicesItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.services_item, parent, false);
        }

        Services currentService=getItem(position);

        LinearLayout serviceId=(LinearLayout)servicesItemView;
        serviceId.setId(currentService.getServiceId());

        ImageView serviceImage=(ImageView)servicesItemView.findViewById(R.id.service_image);
        if(currentService.getServiceImagesourceId()!=-1)
        serviceImage.setImageResource(currentService.getServiceImagesourceId());

        TextView serviceName=(TextView)servicesItemView.findViewById(R.id.service_name);
        serviceName.setText(currentService.getServiceName());
        return servicesItemView;
    }
}