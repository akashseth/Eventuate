package com.bhumca2017.eventuate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ServicesBookedActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_booked);

        Button transaction=(Button) findViewById(R.id.transaction_detail);
        transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookieProfile=new Intent(getApplicationContext(),TransactionActivity.class);
                startActivity(bookieProfile);
            }
        });

    }

}
