package com.bhumca2017.eventuate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

public class DashboardActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        ArrayList<Services> servicesItem=new ArrayList<>();
        servicesItem.add(new Services("Hotel",1,R.drawable.hotel));
        servicesItem.add(new Services("Catering",2,R.drawable.catering));
        servicesItem.add(new Services("Lawns & Banquettes",3,R.drawable.lawns));
        servicesItem.add(new Services("Decorator",4,R.drawable.decorator));
        servicesItem.add(new Services("Tenting",5,R.drawable.tenting));
        servicesItem.add(new Services("Travel",6,R.drawable.travel));
        servicesItem.add(new Services("Restaurants",7,R.drawable.restaurants));
        servicesItem.add(new Services("Video/Photograph",8,R.drawable.video));
        servicesItem.add(new Services("Musical",9,R.drawable.musical));



        GridView servicesGridView=(GridView)findViewById(R.id.dashbord_item);
        final ServicesItemAdapter adapter=new ServicesItemAdapter(this,servicesItem);
        servicesGridView.setAdapter(adapter);

        servicesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent=new Intent(getApplicationContext(),ServicesAvailabilitiesActivity.class);
                intent.putExtra("ServiceId",adapter.getItem(position).getServiceId());
               // Log.e("id","this "+adapter.getItem(position).getServiceId());
                startActivity(intent);
            }
        });


    }
}
