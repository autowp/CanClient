package com.autowp.elm327.command;

import org.apache.commons.codec.binary.Hex;

public class ProgParameterSetCommand extends Command {
    
    protected byte pp;
    protected byte value;

    public ProgParameterSetCommand(byte pp, byte value)
    {
        this.name = "PP";
        this.pp = pp;
        this.value = value;
    }
    
    @Override
    public String toString() {
        String strPP = Hex.encodeHexString(new byte[] {pp}).toUpperCase();
        String strValue = Hex.encodeHexString(new byte[] {value}).toUpperCase();

        return "AT " + this.name + strPP + "SV" + strValue;
    }

}
