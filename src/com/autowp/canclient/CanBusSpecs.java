package com.autowp.canclient;

public class CanBusSpecs {
    protected int speed; // kbit
    
    protected int[] multiframeAbitrationID;
    
    public int getSpeed()
    {
        return speed;
    }
    
    public boolean isMultiFrame(int id)
    {
        boolean result = false;
        for (int i=0; i<multiframeAbitrationID.length; i++) {
            if (multiframeAbitrationID[i] == id) {
                result = true;
                break;
            }
        }
        
        return result;
    }
}
