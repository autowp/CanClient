package com.autowp.peugeot;

import com.autowp.canclient.CanClient;
import com.autowp.canclient.CanFrame;

public class CanComfort {
    public static final int ID_IGNITION = 0x036;
    public static final int ID_VIN = 0x2B6;
    public static final int ID_TRACK_LIST = 0x125;
    
    public static final int IGNITION_PERIOD = 200;
    
    public static final int VIN_DELAY = 300;
    public static final int VIN_LENGTH = 8;
    
    public static final int TRACK_LIST_TRACK_AUTHOR_LENGTH = 20;
    public static final int TRACK_LIST_TRACK_NAME_LENGTH = 20;
    
    public static CanFrame ignitionFrame() throws Exception
    {
        return new CanFrame(CanComfort.ID_IGNITION, "0E00000F010000A0");
    }
    
    public static CanFrame vinFrame(String vin) throws Exception
    {
        int length = vin.length();
        if (length < VIN_LENGTH) {
            throw new Exception("Vin require at least " + VIN_LENGTH + " last digits");
        }
        
        String lastDigits = vin.substring(length - VIN_LENGTH, length);
        return new CanFrame(CanComfort.ID_VIN, lastDigits.getBytes("ISO-8859-1"));
    }
    
    public static void emulateCar(CanClient client, String vin) throws Exception
    {
        client.addTimerTaskFrame(ignitionFrame(), 0, IGNITION_PERIOD);
        client.sendDelayedFrame(vinFrame(vin), VIN_DELAY);
    }
}
