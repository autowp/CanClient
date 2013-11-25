package com.autowp.canhacker.response;

import org.apache.commons.codec.binary.Hex;

public class BellResponse extends Response {
    final public static char CODE = 0x07;
    
    public BellResponse(byte[] bytes) throws ResponseException
    {
        if (bytes.length != 1) {
            String hex = new String(Hex.encodeHex(bytes));
            throw new ResponseException("Bell response must be 1 bytes long. `" + hex + "` received");
        }
        
        if (bytes[0] != CODE) {
            throw new ResponseException("Bell response must be 0x07 character");
        }
    }
    
    public String toString()
    {
        return new String(new char[] { CODE });
    }
}
