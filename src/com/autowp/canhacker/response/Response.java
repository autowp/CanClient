package com.autowp.canhacker.response;

import org.apache.commons.codec.binary.Hex;

abstract public class Response {
    
    abstract public String toString();
    
    public static Response fromBytes(byte[] bytes) throws ResponseException
    {
        if (bytes.length <= 0) {
            throw new ResponseException("Invalid response: empty bytes");
        }
        switch ((char)bytes[0]) {
            case BellResponse.CODE: // bell
                return new BellResponse(bytes);
            case VersionResponse.CODE:
                return new VersionResponse(bytes);
            case CanErrorResponse.CODE:
                return new CanErrorResponse(bytes);
            case FirmwareVersionResponse.CODE:
                return new FirmwareVersionResponse(bytes);
            case FrameResponse.CODE:
                return new FrameResponse(bytes);
        }
        
        String hex = new String(Hex.encodeHex(bytes));
        
        throw new ResponseException("Invalid response: response type not found. `" + hex + "`");
    }
}
