package com.autowp.canhacker.response;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

public class FrameResponse extends Response {
    final public static char CODE = 't';
    
    final public static char ID_LENGTH_CHARS = 3;
    final public static char TIMESTAMP_LENGTH_CHARS = 4;
    
    final public static char ID_MIN = 0;
    final public static char ID_MAX = 0x07FF; // 11bits
    
    final public static char DATA_LENGTH_MIN = 0;
    final public static char DATA_LENGTH_MAX = 8;
    
    protected int id;
    
    protected byte data[];
    
    protected int timestamp;
    
    public FrameResponse(byte[] bytes) throws ResponseException, DecoderException
    {
        if (bytes.length < 5) {
            throw new ResponseException("Frame response must be >= 5 bytes long");
        }
        
        if (bytes[0] != CODE) {
            throw new ResponseException("Frame response must start with `" + CODE + "` character");
        }
        
        if (bytes.length > 25) {
            //String hex = new String(Hex.encodeHex(bytes));
            String hex = new String(bytes);
            throw new ResponseException("Frame response must be <= 21 bytes long. `" + hex + "`");
        }
        
        String str = (new String(bytes)).substring(1);
        
        String idStr = str.substring(0, 3);
        String dataLengthStr = str.substring(3, 4);
        
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
        String dataStr = str.substring(4, 4 + dataLength * 2);
        this.data = Hex.decodeHex(dataStr.toCharArray());
        
        // extract timestamp, if need
        int lengthWithoutTimestamp = ID_LENGTH_CHARS + 1 + dataLength * 2;
        int lengthWithTimestamp = lengthWithoutTimestamp + TIMESTAMP_LENGTH_CHARS;
        if (str.length() == lengthWithTimestamp) {
            String timestampStr = str.substring(lengthWithoutTimestamp);
            this.timestamp = Integer.parseInt("0" + timestampStr, 16);
        } else if (str.length() != lengthWithoutTimestamp) {
            throw new ResponseException("Unexpected response length");
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
