package com.autowp;

public class CanMessage {
    protected int id;
    
    protected byte dataLength;
    
    protected byte[] data;
    
    public static final int ID_IGNITION = 0x036;
    public static final int ID_VIN = 0x2B6;
}
