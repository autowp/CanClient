package com.autowp.canhacker.command;

/**
 * tiiiLDDDDDDDDDDDDDDDD[CR]
 * 
 * This command transmits a standard 11 Bit CAN frame. 
 * It works only if controller is in operational mode after command “O”.
 * 
 * iii = Identifier in hexadecimal (000-7FF)
 * L   = Data length code (0-8)
 * DD  = Data byte value in hexadecimal (00-FF). 
 * 
 * Number of given data bytes will be checked against given data length code.
 * 
 * Return: [CR] or [BEL] 
 */
public class TransmitCommand extends Command {
    protected int id;
    
    protected byte[] data;
    
    protected final int MAX_DATA_LENGTH = 8;
    
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    
    public TransmitCommand(int id, byte[] newData) throws Exception
    {
        this.name = "t";
        
        if (id > 0x07FF) {
            throw new Exception("ID cannot be greater than 11 bits int");
        }
        
        this.id = id;
        
        if (newData.length > MAX_DATA_LENGTH) {
            throw new Exception("Data length must be in " + MAX_DATA_LENGTH + " bytes");
        }
        
        this.data = new byte[newData.length];

        System.arraycopy(newData, 0, this.data, 0, newData.length);
    }
    
    public int getId()
    {
        return id;
    }
    
    public byte[] getData()
    {
        return data;
    }
    
    protected static String idToHex(int id)
    {
        return String.format("%03X", id);
    }
    
    protected static String dataLengthToHex(int value)
    {
        return String.format("%01X", value);
    }
    
    protected static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    
    @Override
    public String toString() {
        return this.name + idToHex(this.id) + dataLengthToHex(this.data.length) + bytesToHex(this.data);
    }

}
