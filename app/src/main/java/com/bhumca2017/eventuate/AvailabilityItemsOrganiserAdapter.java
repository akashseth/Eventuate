package com.bhumca2017.eventuate;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by toaka on 02-05-2017.
 */

public class AvailabilityItemsOrganiserAdapter extends ArrayAdapter <AvailabilityItemsOrganiser> {

    private int mServiceId;
    AvailabilityItemsOrganiserAdapter(Context context, ArrayList<AvailabilityItemsOrganiser> availList,int serviceId){

        super(context,0,availList);
        mServiceId = serviceId;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemAdapter = convertView;

        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            listItemAdapter = layoutInflater.inflate(R.layout.availabilities_item_organiser,parent,false);
        }

        TextView availabilityNameTextView= (TextView)listItemAdapter.findViewById(R.id.availability_name_organiser);
        availabilityNameTextView.setText(getItem(position).getAvailabilityName());

        TextView availabilityPriceTextView= (TextView)listItemAdapter.findViewById(R.id.availability_price_organiser);
        availabilityPriceTextView.setText(getItem(position).getPrice());

        Button availDetailsButton= (Button)listItemAdapter.findViewById(R.id.availability_details_button_organiser);
        availDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent availabilityDetailsIntent = new Intent(getContext(),AvailabilityDetailsOrganiserActivity.class);
                availabilityDetailsIntent.putExtra("serviceProviderId",getItem(position).getServiceProviderId());
                availabilityDetailsIntent.putExtra("price",getItem(position).getPrice());
                availabilityDetailsIntent.putExtra("serviceId",mServiceId);
                availabilityDetailsIntent.putExtra("serviceAvailabilityId",getItem(position).getServiceAvailabilityId());
                availabilityDetailsIntent.putExtra("availabilityName",getItem(position).getAvailabilityName());
                getContext().startActivity(availabilityDetailsIntent);
            }
        });

        TextView providerNameTextView= (TextView)listItemAdapter.findViewById(R.id.service_provider_name);
        providerNameTextView.setText(getItem(position).getServiceProviderName());

        return listItemAdapter;
    }
}
