package com.autowp.elm327.command;

import org.apache.commons.codec.binary.Hex;

import com.autowp.elm327.Elm327Exception;

public class SetHeaderCommand extends Command {
    
    public static final int HEADER_LENGTH = 3;
    
    protected byte[] bytes;

    public SetHeaderCommand(byte[] header) throws Elm327Exception
    {
        this.name = "SH";
        
        if (header.length != HEADER_LENGTH) {
            throw new Elm327Exception("Header length must be " + HEADER_LENGTH + " bytes long");
        }
        
        this.bytes = new byte[HEADER_LENGTH];

        System.arraycopy(header, 0, this.bytes, 0, HEADER_LENGTH);
    }
    
    @Override
    public String toString() {
        return "AT " + name + " " + Hex.encodeHexString(bytes).toUpperCase();
    }

}
