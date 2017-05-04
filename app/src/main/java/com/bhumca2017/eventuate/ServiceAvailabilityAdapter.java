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
 * Created by toaka on 03-04-2017.
 */

public class ServiceAvailabilityAdapter extends ArrayAdapter<ServiceAvailability> {

    public ServiceAvailabilityAdapter(Context context, ArrayList<ServiceAvailability> serviceAvailibilities)
    {
        super(context,0,serviceAvailibilities);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView=convertView;
        if(listItemView==null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            listItemView = layoutInflater.inflate(R.layout.list_of_availibities, parent, false);
        }
        ServiceAvailability serviceAvailability=getItem(position);

        TextView availabilityNameView=(TextView)listItemView.findViewById(R.id.availability_name);
        availabilityNameView.setText(serviceAvailability.getAvailabilityName());

        TextView availabilityQuantity=(TextView)listItemView.findViewById(R.id.quantity);
        availabilityQuantity.setText(serviceAvailability.getQuantity());

        final Button availabilityEdit=(Button) listItemView.findViewById(R.id.availability_edit_id);
        availabilityEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editAvailIntent=new Intent(getContext(),EditAvailabilitiesActivity.class);
                ServiceAvailability serviceAvailability = getItem(position);
                editAvailIntent.putExtra("serviceAvailabilityId",serviceAvailability.getServiceAvailabilityId());
                editAvailIntent.putExtra("availabilityName",serviceAvailability.getAvailabilityName());
                editAvailIntent.putExtra("price",serviceAvailability.getPrice());
                editAvailIntent.putExtra("quantity",serviceAvailability.getQuantity());
                getContext().startActivity(editAvailIntent);

                /*Log.e("id","this "+getItem(position).getAvailabilityId());
                Intent availabilityEditIntent=new Intent(getContext(),EditAvailabilitiesActivity.class);
                getContext().startActivity(availabilityEditIntent);*/
            }
        });

        return listItemView;
    }
}
