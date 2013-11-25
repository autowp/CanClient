package com.autowp.canhacker.response;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

public class FrameResponse extends Response {
    final public static char CODE = 't';
    
    final public static char ID_LENGTH_CHARS = 3;
    
    final public static char ID_MIN = 0;
    final public static char ID_MAX = 0x07FF; // 11bits
    
    final public static char DATA_LENGTH_MIN = 0;
    final public static char DATA_LENGTH_MAX = 8;
    
    protected int id;
    
    protected byte data[];
    
    public FrameResponse(byte[] bytes) throws ResponseException, DecoderException
    {
        if (bytes.length < 5) {
            throw new ResponseException("Frame response must be >= 5 bytes long");
        }
        
        if (bytes.length > 21) {
            throw new ResponseException("Frame response must be <= 21 bytes long");
        }
        
        if (bytes[0] != CODE) {
            throw new ResponseException("Frame response must start with `" + CODE + "` character");
        }
        
        String str = (new String(bytes)).substring(1);
        
        String idStr = str.substring(0, 3);
        String dataLengthStr = str.substring(3, 4);
        String dataStr = str.substring(4);
        
        // extract id
        this.id = Integer.parseInt('0' + idStr, 16);
        
        if (this.id < ID_MIN) {
            throw new ResponseException("Frame response id cannot be < 0");
        }
        if (this.id > ID_MAX) {
            throw new ResponseException("Frame response id cannot be greater then 11bits");
        }
        
        // extract data length
        int dataLength = Integer.parseInt('0' + dataLengthStr, 16);
        if (dataLength < DATA_LENGTH_MIN) {
            throw new ResponseException("Frame response data length cannot be < " + DATA_LENGTH_MIN);
        }
        if (dataLength > DATA_LENGTH_MAX) {
            throw new ResponseException("Frame response data length cannot be > " + DATA_LENGTH_MAX);
        }
        
        // extract data
        this.data = Hex.decodeHex(dataStr.toCharArray());
        int actualDataLength = this.data.length;
        if (actualDataLength < DATA_LENGTH_MIN) {
            throw new ResponseException("Frame response data cannot be < " + DATA_LENGTH_MIN + " bytes long");
        }
        if (actualDataLength > DATA_LENGTH_MAX) {
            throw new ResponseException("Frame response data cannot be > " + DATA_LENGTH_MAX + " bytes long");
        }
        
        if (dataLength != actualDataLength) {
            throw new ResponseException("Frame response dataLength and actual data length not the same");
        }
    }

    @Override
    public String toString() {
        String idHex = Integer.toHexString(this.id);
        idHex = StringUtils.leftPad(idHex, ID_LENGTH_CHARS, '0');
        
        String dataLengthHex = Integer.toHexString(this.data.length);
        String dataLength = "" + dataLengthHex.charAt(dataLengthHex.length() - 1);
        
        return CODE + (idHex + dataLength + new String(Hex.encodeHex(this.data))).toUpperCase();
    }

    public int getId()
    {
        return id;
    }
    
    public byte[] getData()
    {
        return data;
    }
}
