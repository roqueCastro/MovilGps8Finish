package com.example.asus.movilgps.Globals;

/**
 * Created by ASUS on 27/04/2018.
 */

public class Globals {

    private static Globals intanse;
    private static String coUser;

    private Globals(){}

    public void setCoUser(String coU){
        Globals.coUser=coU;
    }

    public  String getCoUser (){
        return Globals.coUser;
    }

    public static synchronized Globals getIntanse() {
        if(intanse==null) {
            intanse = new Globals();
        }
        return intanse;
    }

}
