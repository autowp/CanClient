package com.autowp.peugeot;

import java.nio.charset.Charset;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import com.autowp.canclient.CanClient;
import com.autowp.canclient.CanFrame;
import com.autowp.canclient.CanFrameException;

public class CanComfort {
    public static final int SPEED = 125; // kbit
    
    public static final int ID_IGNITION = 0x036;
    public static final int ID_VIN = 0x2B6;
    public static final int ID_TRACK_LIST = 0x125;
    public static final int ID_VOLUME = 0x1A5;
    public static final int ID_CURRENT_CD_TRACK = 0x0A4;
    
    public static final int IGNITION_PERIOD = 200;
    
    public static final int VIN_DELAY = 300;
    public static final int VIN_LENGTH = 8;
    
    public static final int TRACK_LIST_TRACK_AUTHOR_LENGTH = 20;
    public static final int TRACK_LIST_TRACK_NAME_LENGTH = 20;
    
    public static final Charset charset = Charset.forName("ISO-8859-1");
    
    public static CanFrame ignitionFrame() throws DecoderException, CanFrameException
    {
        byte[] bytes = Hex.decodeHex("0E00000F010000A0".toCharArray());
        return new CanFrame(CanComfort.ID_IGNITION, bytes);
    }
    
    public static CanFrame vinFrame(String vin) throws CanComfortException, CanFrameException
    {
        int length = vin.length();
        if (length < VIN_LENGTH) {
            throw new CanComfortException("Vin require at least " + VIN_LENGTH + " last digits");
        }
        
        String lastDigits = vin.substring(length - VIN_LENGTH, length);
        return new CanFrame(CanComfort.ID_VIN, lastDigits.getBytes(charset));
    }
    
    public static void emulateCar(CanClient client, String vin) throws CanComfortException 
    {
        try {
            client.addTimerTaskFrame(ignitionFrame(), 0, IGNITION_PERIOD);
            client.sendDelayedFrame(vinFrame(vin), VIN_DELAY);
        } catch (DecoderException | CanFrameException e) {
            throw new CanComfortException(e.getMessage());
        }
    }
}
