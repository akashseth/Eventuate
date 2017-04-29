package com.bhumca2017.eventuate;

public class SetDrawerFlag {

    // input flags are true only when the info exists in the database
    public static boolean drawer_flag_profile_input=false;
    public static boolean drawer_flag_event_input=false;

    // these flags are true only if the edit forms are reached from the drawer activity
    public static boolean drawer_flag_profile=false;
    public static boolean drawer_flag_event=false;

    // this flag is true only if the dashboard is reached from EditExpenditure
    public static boolean drawer_flag_expenditure=false;

    public static void setDrawerFlagProfileInput(boolean input)
    {
        drawer_flag_profile_input=input;
    }

    public static void setDrawerFlagEventInput(boolean input)
    {
        drawer_flag_event_input=input;
    }

    public static void setDrawerFlagProfile(boolean val)
    {
        drawer_flag_profile=val;
    }

    public static void setDrawerFlagEvent(boolean val)
    {
        drawer_flag_event=val;
    }

    public static void setDrawerFlagExpenditure(boolean val)
    {
        drawer_flag_expenditure=val;
    }

    public static boolean getDrawerFlagProfileInput() { return drawer_flag_profile_input;  }

    public static boolean getDrawerFlagEventInput()
    {
        return drawer_flag_event_input;
    }

    public static boolean getDrawerFlagProfile()
    {
        return drawer_flag_profile;
    }

    public static boolean getDrawerFlagEvent()
    {
        return drawer_flag_event;
    }

    public static boolean getDrawerFlagExpenditure()
    {
        return drawer_flag_expenditure;
    }
}
