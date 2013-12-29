package com.autowp.elm327.command;

import org.apache.commons.codec.binary.Hex;

public class ProgParameterOnCommand extends Command {

    protected byte pp;

    public ProgParameterOnCommand(byte pp)
    {
        this.name = "PP";
        this.pp = pp;
    }
    
    @Override
    public String toString() {
        String strPP = Hex.encodeHexString(new byte[] {pp}).toUpperCase();

        return "AT " + this.name + strPP + "SVON";
    }

}
