package com.autowp.canclient;

public class CanMessage {
    protected int id;
    
    protected byte[] data;
    
    final public static int ID_MIN = 0;
    final public static int ID_MAX = 0x07FF; // 11bits
    
    public CanMessage(int id, byte[] data) throws CanMessageException
    {
        if (!isValidId(id)) {
            throw new CanMessageException("Invalid id " + id);
        }
        
        this.id = id;
        this.data = new byte[data.length];
        System.arraycopy(data, 0, this.data, 0, data.length);
    }
    
    public int getId()
    {
        return id;
    }
    
    public byte[] getData()
    {
        return data;
    }
    
    public static boolean isValidId(int id)
    {
        return id >= ID_MIN && id <= ID_MAX;
    }
}
