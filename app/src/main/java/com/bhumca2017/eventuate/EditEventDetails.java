package com.bhumca2017.eventuate;

import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Time;
import java.text.DateFormat;
import java.util.Date;

public class EditEventDetails extends AppCompatActivity {

    TextView eventtype, selecteventtext;
    Button selecteventtype, submiteventtype;
    RadioGroup selectEventType;
    RadioButton wedding, ringCeremony, birthday, party, fest, businessMeet, showsConcerts, snackTreat, teaTalk;

    TextView eventDate;
    Button selectdate;
    TextView selectdatetext;
    DatePicker selectDate;
    Button submitdate;

    TextView eventTimeFrom, eventTimeToText, eventTimeTo, inputtimetext, inputtimecolon;
    Button selecttimefrom, selecttimeto, submittime;
    EditText inputtimehours, inputtimeminutes;

    EditText eventBudget;

    Button submitEventDetails;

    String OrganizerEmail;
    String EventType="";
    Integer EventDateYear=null, EventDateMonth=null, EventDateDayOfMonth=null;
    Integer EventTimeFromHour=null, EventTimeFromMinute=null, EventTimeToHour=null, EventTimeToMinute=null;
    int DayOfMonth=-1, Month=-1, Year=-1, CurrentTimeHours=-1, CurrentTimeMinutes=-1;
    Integer EventBudget=null;

    String json_string;
    JSONObject jsonObject;

    boolean drawer_flag = SetDrawerFlag.getDrawerFlagEvent();
    // if drawer_flag=false => first time event is edited, i.e., in php, data is inserted into the table
    // if drawer_flag=true => event is edited previously, only updated this time, i.e., in php, data is updated into the table

    boolean drawer_flag_input = SetDrawerFlag.getDrawerFlagEventInput();

    boolean flagFromInput=false;     // true if 'from' time is being input, false otherwise
    boolean flagFrom=false;          // true if 'from' time is already input, false otherwise

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event_details);

        eventtype = (TextView) findViewById(R.id.eventtype);
        selecteventtype = (Button) findViewById(R.id.button_selecteventtype);
        selecteventtext = (TextView) findViewById(R.id.selecteventtext);
        selecteventtext.setVisibility(View.GONE);

        selectEventType = (RadioGroup) findViewById(R.id.radio_button_eventtype);
        selectEventType.setVisibility(View.GONE);
        wedding = (RadioButton) findViewById(R.id.radio_button_wedding);
        ringCeremony = (RadioButton) findViewById(R.id.radio_button_ringceremony);
        birthday = (RadioButton) findViewById(R.id.radio_button_birthday);
        party = (RadioButton) findViewById(R.id.radio_button_party);
        fest = (RadioButton) findViewById(R.id.radio_button_fest);
        businessMeet = (RadioButton) findViewById(R.id.radio_button_businessmeet);
        showsConcerts = (RadioButton) findViewById(R.id.radio_button_show);
        snackTreat = (RadioButton) findViewById(R.id.radio_button_snacktreat);
        teaTalk = (RadioButton) findViewById(R.id.radio_button_teatalk);

        submiteventtype = (Button) findViewById(R.id.button_submiteventtype);
        submiteventtype.setVisibility(View.GONE);

        eventDate = (TextView) findViewById(R.id.eventdate);
        selectdate = (Button) findViewById(R.id.button_selecteventdate);
        selectdatetext = (TextView) findViewById(R.id.selectdate_text);
        selectdatetext.setVisibility(View.GONE);
        selectDate = (DatePicker) findViewById(R.id.selectdate);
        selectDate.setVisibility(View.GONE);
        submitdate = (Button) findViewById(R.id.button_submiteventdate);
        submitdate.setVisibility(View.GONE);

        eventTimeFrom = (TextView) findViewById(R.id.eventtimefrom);
        selecttimefrom = (Button) findViewById(R.id.button_selecteventtimefrom);
        eventTimeToText = (TextView) findViewById(R.id.eventtimeto_text);
        eventTimeTo = (TextView) findViewById(R.id.eventtimeto);
        selecttimeto = (Button) findViewById(R.id.button_selecteventtimeto);
        inputtimetext = (TextView) findViewById(R.id.inputtime_text);
        inputtimetext.setVisibility(View.GONE);
        inputtimehours = (EditText) findViewById(R.id.inputtimehours);
        inputtimehours.setVisibility(View.GONE);
        inputtimecolon = (TextView) findViewById(R.id.inputtimecolon);
        inputtimecolon.setVisibility(View.GONE);
        inputtimeminutes = (EditText) findViewById(R.id.inputtimeminutes);
        inputtimeminutes.setVisibility(View.GONE);
        submittime = (Button) findViewById(R.id.button_submiteventtime);
        submittime.setVisibility(View.GONE);

        eventBudget = (EditText) findViewById(R.id.event_budget);

        submitEventDetails = (Button) findViewById(R.id.button_submit_eventdetails);


        // parsing the received json string

        // extracting the json string
        json_string = getIntent().getExtras().getString("json_data");

        if(drawer_flag_input == false)       // organizer data doesn't exist in the database
        {
            // parsing the json string
            try {
                jsonObject = new JSONObject(json_string);
                OrganizerEmail = jsonObject.getString("EmailId");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // setting the form data to the previous input data if the event info is available in the database
        if(drawer_flag_input == true)       // organizer data exists in the database
        {
            // parsing the json string
            try {
                jsonObject = new JSONObject(json_string);
                OrganizerEmail = jsonObject.getString("EmailId");
                EventType = jsonObject.getString("EventType");
                EventDateDayOfMonth = jsonObject.getInt("EventDateDayOfMonth");
                EventDateMonth = jsonObject.getInt("EventDateMonth");
                EventDateYear = jsonObject.getInt("EventDateYear");
                EventTimeFromHour = jsonObject.getInt("EventTimeFromHours");
                EventTimeFromMinute = jsonObject.getInt("EventTimeFromMinutes");
                EventTimeToHour = jsonObject.getInt("EventTimeToHours");
                EventTimeToMinute = jsonObject.getInt("EventTimeToMinutes");
                EventBudget = jsonObject.getInt("EventBudget");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            eventtype.setText(EventType);
            if(EventType.equals("Wedding"))
                wedding.setChecked(true);
            else if(EventType.equals("Ring Ceremony"))
                ringCeremony.setChecked(true);
            else if(EventType.equals("Birthday"))
                birthday.setChecked(true);
            else if(EventType.equals("Party"))
                party.setChecked(true);
            else if(EventType.equals("Fest"))
                fest.setChecked(true);
            else if(EventType.equals("Business Meet"))
                businessMeet.setChecked(true);
            else if(EventType.equals("Show or Concert"))
                showsConcerts.setChecked(true);
            else if(EventType.equals("Snack Treat"))
                snackTreat.setChecked(true);
            else if(EventType.equals("Tea Talk"))
                teaTalk.setChecked(true);


            String DayOfMonth, Month, Year, date;

            if(EventDateDayOfMonth < 10)
                DayOfMonth = "0"+EventDateDayOfMonth.toString();
            else
                DayOfMonth = EventDateDayOfMonth.toString();

            if(EventDateMonth < 10)
                Month = "0"+EventDateMonth.toString();
            else
                Month = EventDateMonth.toString();

            if(EventDateYear < 10)
                Year = "0"+EventDateYear.toString();
            else
                Year = EventDateYear.toString();

            date = DayOfMonth + " / " + Month + " / " + Year;
            eventDate.setText(date);


            String Hours, Minutes, TimeFrom, TimeTo;

            if(EventTimeFromHour < 10)
                Hours = "0"+EventTimeFromHour.toString();
            else
                Hours = EventTimeFromHour.toString();
            if(EventTimeFromMinute < 10)
                Minutes = "0"+EventTimeFromMinute.toString();
            else
                Minutes = EventTimeFromMinute.toString();

            TimeFrom = Hours + " : " + Minutes;
            eventTimeFrom.setText(TimeFrom);
            flagFrom = true;

            if(EventTimeToHour < 10)
                Hours = "0"+EventTimeToHour.toString();
            else
                Hours = EventTimeToHour.toString();
            if(EventTimeToMinute < 10)
                Minutes = "0"+EventTimeToMinute.toString();
            else
                Minutes = EventTimeToMinute.toString();

            TimeTo = Hours + " : " + Minutes;
            eventTimeTo.setText(TimeTo);


            eventBudget.setText(EventBudget.toString());
        }
    }


    public void submitEventDetails(View view)
    {
        EventBudget = Integer.parseInt(eventBudget.getText().toString());

        if(EventType.equals("") || EventDateDayOfMonth==null || EventDateMonth==null || EventDateYear==null || EventTimeFromHour==null || EventTimeFromMinute==null || EventTimeToHour==null || EventTimeToMinute==null || EventBudget==null)
            Toast.makeText(this, "All fields are mandatory...", Toast.LENGTH_SHORT).show();
        else
            new BackgroundTask_updateEventDetails().execute();
    }


    public void selectEvent(View view)
    {
        selecteventtext.setVisibility(View.VISIBLE);
        selectEventType.setVisibility(View.VISIBLE);
        submiteventtype.setVisibility(View.VISIBLE);
    }

    public void submitEvent(View view)
    {
        if(selectEventType.getCheckedRadioButtonId() == -1)
            Toast.makeText(this, "Select your event...", Toast.LENGTH_SHORT).show();
        else
        {
            if (wedding.isChecked())
                EventType = "Wedding";
            else if (ringCeremony.isChecked())
                EventType = "Ring Ceremony";
            else if (birthday.isChecked())
                EventType = "Birthday";
            else if (party.isChecked())
                EventType = "Party";
            else if (fest.isChecked())
                EventType = "Fest";
            else if (businessMeet.isChecked())
                EventType = "Business Meet";
            else if (showsConcerts.isChecked())
                EventType = "Show or Concert";
            else if (snackTreat.isChecked())
                EventType = "Snack Treat";
            else if (teaTalk.isChecked())
                EventType = "Tea Talk";

            eventtype.setText(EventType);

            selecteventtext.setVisibility(View.GONE);
            selectEventType.setVisibility(View.GONE);
            submiteventtype.setVisibility(View.GONE);
        }
    }

    public void selectDate(View view)
    {
        selectdatetext.setVisibility(View.VISIBLE);
        selectDate.setVisibility(View.VISIBLE);
        submitdate.setVisibility(View.VISIBLE);
    }

    public void submitDate(View view)
    {
        Integer dateDayOfMonth, dateMonth, dateYear;

        dateDayOfMonth = selectDate.getDayOfMonth();
        dateMonth = selectDate.getMonth();
        dateYear = selectDate.getYear();

        // checking for validity of the input date

        java.util.Calendar c = java.util.Calendar.getInstance();
        DayOfMonth = c.get(java.util.Calendar.DAY_OF_MONTH);
        Month = c.get(java.util.Calendar.MONTH);
        Year = c.get(java.util.Calendar.YEAR);


        if (Year > dateYear) {
            Toast.makeText(this, "Invalid Date....", Toast.LENGTH_SHORT).show();
        } else if (Year == dateYear) {
            if (Month > dateMonth)
                Toast.makeText(this, "Invalid Date....", Toast.LENGTH_SHORT).show();
            else if (Month == dateMonth) {
                if (DayOfMonth > dateDayOfMonth)
                    Toast.makeText(this, "Invalid Date....", Toast.LENGTH_SHORT).show();
                else {
                    dateMonth++;

                    String date;
                    if(dateDayOfMonth < 10)
                    {
                        if(dateMonth < 10)
                            date = "0"+dateDayOfMonth.toString() + "/" + "0"+dateMonth.toString() + "/" + dateYear.toString();
                        else
                            date = "0"+dateDayOfMonth.toString() + "/" + dateMonth.toString() + "/" + dateYear.toString();
                    }
                    else
                    {
                        if(dateMonth < 10)
                            date = dateDayOfMonth.toString() + "/" + "0"+dateMonth.toString() + "/" + dateYear.toString();
                        else
                            date = dateDayOfMonth.toString() + "/" + dateMonth.toString() + "/" + dateYear.toString();
                    }

                    eventDate.setText(date);
                    CurrentTimeHours = c.get(java.util.Calendar.HOUR_OF_DAY);
                    CurrentTimeMinutes = c.get(java.util.Calendar.MINUTE);

                    selectdatetext.setVisibility(View.GONE);
                    selectDate.setVisibility(View.GONE);
                    submitdate.setVisibility(View.GONE);

                    EventDateDayOfMonth = dateDayOfMonth;
                    EventDateMonth = dateMonth;
                    EventDateYear = dateYear;
                }
            }
            else{
                dateMonth++;

                String date;
                if(dateDayOfMonth < 10)
                {
                    if(dateMonth < 10)
                        date = "0"+dateDayOfMonth.toString() + "/" + "0"+dateMonth.toString() + "/" + dateYear.toString();
                    else
                        date = "0"+dateDayOfMonth.toString() + "/" + dateMonth.toString() + "/" + dateYear.toString();
                }
                else
                {
                    if(dateMonth < 10)
                        date = dateDayOfMonth.toString() + "/" + "0"+dateMonth.toString() + "/" + dateYear.toString();
                    else
                        date = dateDayOfMonth.toString() + "/" + dateMonth.toString() + "/" + dateYear.toString();
                }

                eventDate.setText(date);
                CurrentTimeHours = c.get(java.util.Calendar.HOUR_OF_DAY);
                CurrentTimeMinutes = c.get(java.util.Calendar.MINUTE);

                selectdatetext.setVisibility(View.GONE);
                selectDate.setVisibility(View.GONE);
                submitdate.setVisibility(View.GONE);

                EventDateDayOfMonth = dateDayOfMonth;
                EventDateMonth = dateMonth;
                EventDateYear = dateYear;
            }
        }
        else{
            dateMonth++;

            String date;
            if(dateDayOfMonth < 10)
            {
                if(dateMonth < 10)
                    date = "0"+dateDayOfMonth.toString() + "/" + "0"+dateMonth.toString() + "/" + dateYear.toString();
                else
                    date = "0"+dateDayOfMonth.toString() + "/" + dateMonth.toString() + "/" + dateYear.toString();
            }
            else
            {
                if(dateMonth < 10)
                    date = dateDayOfMonth.toString() + "/" + "0"+dateMonth.toString() + "/" + dateYear.toString();
                else
                    date = dateDayOfMonth.toString() + "/" + dateMonth.toString() + "/" + dateYear.toString();
            }

            eventDate.setText(date);
            CurrentTimeHours = c.get(java.util.Calendar.HOUR_OF_DAY);
            CurrentTimeMinutes = c.get(java.util.Calendar.MINUTE);

            selectdatetext.setVisibility(View.GONE);
            selectDate.setVisibility(View.GONE);
            submitdate.setVisibility(View.GONE);

            EventDateDayOfMonth = dateDayOfMonth;
            EventDateMonth = dateMonth;
            EventDateYear = dateYear;
        }
    }

    public void selectTimeFrom(View view)
    {
        if(CurrentTimeHours == -1)
            Toast.makeText(this, "Select Event Date first...", Toast.LENGTH_SHORT).show();
        else
        {
            flagFromInput=true;

            inputtimetext.setVisibility(View.VISIBLE);
            inputtimehours.setVisibility(View.VISIBLE);
            inputtimecolon.setVisibility(View.VISIBLE);
            inputtimeminutes.setVisibility(View.VISIBLE);
            submittime.setVisibility(View.VISIBLE);
        }
    }

    public void selectTimeTo(View view)
    {
        if(flagFrom==false)
            Toast.makeText(this, "Select event starting time first", Toast.LENGTH_SHORT).show();
        else
        {
            flagFromInput=false;

            inputtimetext.setVisibility(View.VISIBLE);
            inputtimehours.setVisibility(View.VISIBLE);
            inputtimecolon.setVisibility(View.VISIBLE);
            inputtimeminutes.setVisibility(View.VISIBLE);
            submittime.setVisibility(View.VISIBLE);
        }
    }

    public void submitTime(View view)
    {
        String time;
        Integer timeHours, timeMinutes;

        if(inputtimehours.getText().toString().equals("") || inputtimeminutes.getText().toString().equals(""))
            Toast.makeText(this, "Input time first...", Toast.LENGTH_SHORT).show();
        else
        {
            timeHours = Integer.parseInt(inputtimehours.getText().toString());
            timeMinutes = Integer.parseInt(inputtimeminutes.getText().toString());

            if(EventDateDayOfMonth==DayOfMonth && EventDateMonth==Month && EventDateYear==Year)     // on selecting the current date
            {
                if(flagFromInput == true)   // from time is being input
                {
                    if(timeHours < CurrentTimeHours)
                    {
                        Toast.makeText(this, "Invalid Time Input...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(timeHours == CurrentTimeHours)
                    {
                        if(timeMinutes < CurrentTimeMinutes)
                        {
                            Toast.makeText(this, "Invalid Time Input...", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
            }

            if(timeHours < 0 || timeHours >= 24)
                Toast.makeText(this, "Invalid Time Input...", Toast.LENGTH_SHORT).show();
            else if(timeMinutes < 0 || timeMinutes >= 60)
                Toast.makeText(this, "Invalid Time Input...", Toast.LENGTH_SHORT).show();
            else {

                if(flagFromInput == false)      // 'To' time is being input
                {
                    if(timeHours < EventTimeFromHour)
                        Toast.makeText(this, "Invalid Duration...", Toast.LENGTH_SHORT).show();
                    else if(timeHours == EventTimeFromHour)
                    {
                        if(timeMinutes <= EventTimeFromMinute)
                            Toast.makeText(this, "Invalid Duration...", Toast.LENGTH_SHORT).show();
                        else
                        {
                            // valid time

                            if(timeHours < 10)
                            {
                                if(timeMinutes < 10)
                                    time = "0"+timeHours.toString() + ":" + "0"+timeMinutes.toString();
                                else
                                    time = "0"+timeHours.toString() + ":" + timeMinutes.toString();
                            }
                            else
                            {
                                if(timeMinutes < 10)
                                    time = timeHours.toString() + ":" + "0"+timeMinutes.toString();
                                else
                                    time = timeHours.toString() + ":" + timeMinutes.toString();
                            }

                            eventTimeTo.setText(time);

                            EventTimeToHour = timeHours;
                            EventTimeToMinute = timeMinutes;

                            inputtimetext.setVisibility(View.GONE);
                            inputtimehours.setVisibility(View.GONE);
                            inputtimecolon.setVisibility(View.GONE);
                            inputtimeminutes.setVisibility(View.GONE);
                            submittime.setVisibility(View.GONE);
                        }
                    }
                    else
                    {
                        // valid time

                        if(timeHours < 10)
                        {
                            if(timeMinutes < 10)
                                time = "0"+timeHours.toString() + ":" + "0"+timeMinutes.toString();
                            else
                                time = "0"+timeHours.toString() + ":" + timeMinutes.toString();
                        }
                        else
                        {
                            if(timeMinutes < 10)
                                time = timeHours.toString() + ":" + "0"+timeMinutes.toString();
                            else
                                time = timeHours.toString() + ":" + timeMinutes.toString();
                        }

                        eventTimeTo.setText(time);

                        EventTimeToHour = timeHours;
                        EventTimeToMinute = timeMinutes;

                        inputtimetext.setVisibility(View.GONE);
                        inputtimehours.setVisibility(View.GONE);
                        inputtimecolon.setVisibility(View.GONE);
                        inputtimeminutes.setVisibility(View.GONE);
                        submittime.setVisibility(View.GONE);
                    }
                }
                else                            // 'From' time is being input
                {
                    // valid time

                    if(timeHours < 10)
                    {
                        if(timeMinutes < 10)
                            time = "0"+timeHours.toString() + ":" + "0"+timeMinutes.toString();
                        else
                            time = "0"+timeHours.toString() + ":" + timeMinutes.toString();
                    }
                    else
                    {
                        if(timeMinutes < 10)
                            time = timeHours.toString() + ":" + "0"+timeMinutes.toString();
                        else
                            time = timeHours.toString() + ":" + timeMinutes.toString();
                    }

                    eventTimeFrom.setText(time);

                    EventTimeFromHour = timeHours;
                    EventTimeFromMinute = timeMinutes;

                    flagFrom = true;

                    inputtimetext.setVisibility(View.GONE);
                    inputtimehours.setVisibility(View.GONE);
                    inputtimecolon.setVisibility(View.GONE);
                    inputtimeminutes.setVisibility(View.GONE);
                    submittime.setVisibility(View.GONE);
                }
            }
        }
    }



    // Background tasks to execute the php script updateEventDetails.php

    public class BackgroundTask_updateEventDetails extends AsyncTask<Void, Void, String> {

        String url_updateEventDetails, json_string, flag_val;

        @Override
        protected void onPreExecute()
        {
            // url of php script for handling profile updation tasks for the organizer
            url_updateEventDetails=getString(R.string.ip_address)+"/eventuate/updateEventDetails.php";

            if(drawer_flag_input == true)
                flag_val = "true";
            else
                flag_val = "false";

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                // connecting to the url
                URL url = new URL(url_updateEventDetails);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data = URLEncoder.encode("DrawerFlag", "UTF-8") + "=" + URLEncoder.encode(flag_val, "UTF-8") + "&" +
                        URLEncoder.encode("Email", "UTF-8") + "=" + URLEncoder.encode(OrganizerEmail, "UTF-8") + "&" +
                        URLEncoder.encode("EventType", "UTF-8") + "=" + URLEncoder.encode(EventType, "UTF-8") + "&" +
                        URLEncoder.encode("EventDateDayOfMonth", "UTF-8") + "=" + EventDateDayOfMonth + "&" +
                        URLEncoder.encode("EventDateMonth", "UTF-8") + "=" + EventDateMonth + "&" +
                        URLEncoder.encode("EventDateYear", "UTF-8") + "=" + EventDateYear + "&" +
                        URLEncoder.encode("EventTimeFromHours", "UTF-8") + "=" + EventTimeFromHour + "&" +
                        URLEncoder.encode("EventTimeFromMinutes", "UTF-8") + "=" + EventTimeFromMinute + "&" +
                        URLEncoder.encode("EventTimeToHours", "UTF-8") + "=" + EventTimeToHour + "&" +
                        URLEncoder.encode("EventTimeToMinutes", "UTF-8") + "=" + EventTimeToMinute + "&" +
                        URLEncoder.encode("EventBudget", "UTF-8") + "=" + EventBudget;

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();


                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS));

                String line = "";
                StringBuilder stringBuilder = new StringBuilder();  // returns the JSON response

                while((line=bufferedReader.readLine())!=null)
                    stringBuilder.append((line+"\n"));

                bufferedReader.close();
                IS.close();
                httpURLConnection.disconnect();

                // returning a json object string containing email id of the user
                return stringBuilder.toString().trim();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {

            if((result.equals("")))
                Toast.makeText(getApplicationContext(), "Event details not updated...Try again!!", Toast.LENGTH_LONG).show();
            else {
                Toast.makeText(getApplicationContext(), "Event details updated successfully...", Toast.LENGTH_LONG).show();
                SetDrawerFlag.setDrawerFlagEventInput(true);

                // pass the json data to the next activity
                Intent intent = new Intent(getApplicationContext(), SigningIn.class);
                intent.putExtra("EmailId", OrganizerEmail);
                startActivity(intent);
                finish();
            }
        }
    }
}