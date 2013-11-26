package com.autowp.canclient;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

public class CanFrame {
    protected int id;
    
    protected byte[] data;
    
    final public static int ID_MIN = 0;
    final public static int ID_MAX = 0x07FF; // 11bits
    
    final public static char DATA_LENGTH_MIN = 0;
    final public static char DATA_LENGTH_MAX = 8;
    
    public CanFrame(int id, byte[] data) throws Exception
    {
        if (!isValidId(id)) {
            throw new Exception("Invalid id " + id);
        }
        
        if (!isValidData(data)) {
            String hex = new String(Hex.encodeHex(data));
            throw new Exception("Invalid data `" + hex + "` (" + data.length + " bytes)");
        }
        
        this.id = id;
        this.data = new byte[data.length];
        System.arraycopy(data, 0, this.data, 0, data.length);
    }
    
    public CanFrame(int id, String string) throws Exception {
        this(id, Hex.decodeHex(string.toCharArray()));
    }

    public int getId() {
        return this.id;
    }

    public byte[] getData() {
        return this.data;
    }
    
    public static boolean isValidId(int id)
    {
        return id >= ID_MIN && id <= ID_MAX;
    }
    
    public static boolean isValidData(byte[] data)
    {
        return data.length >= DATA_LENGTH_MIN && data.length <= DATA_LENGTH_MAX;
    }
    
    public String toString()
    {
        String idHex = Integer.toHexString(this.id);
        idHex = StringUtils.leftPad(idHex, 3, '0');
        
        String dataLengthHex = Integer.toHexString(this.data.length);
        String dataLength = "" + dataLengthHex.charAt(dataLengthHex.length() - 1);
        
        return (idHex + " " + dataLength + " " + new String(Hex.encodeHex(this.data))).toUpperCase();
    }
}
