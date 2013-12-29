package com.autowp.elm327.command;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import com.autowp.elm327.Elm327Exception;

public class TransmitCommand extends Command {

protected int id;
    
    protected byte[] data;
    
    protected final int MAX_DATA_LENGTH = 8;
    
    public TransmitCommand(byte[] newData) throws Elm327Exception
    {
        if (newData.length > MAX_DATA_LENGTH) {
            throw new Elm327Exception("Data length must be in " + MAX_DATA_LENGTH + " bytes");
        }
        
        this.data = new byte[newData.length];

        System.arraycopy(newData, 0, this.data, 0, newData.length);
    }
    
    public byte[] getData()
    {
        return data;
    }
    
    @Override
    public String toString() {
        return StringUtils.join(Hex.encodeHexString(this.data).toUpperCase().split("(?<=\\G.{2})"), " ");
    }

}
