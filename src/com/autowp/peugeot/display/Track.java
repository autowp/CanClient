package com.autowp.peugeot.display;

import org.apache.commons.codec.binary.Hex;

import com.autowp.peugeot.CanComfort;

public class Track {
    protected String author;
    protected String name;
    
    public Track()
    {
        this("", "");
    }
    
    public Track(String author, String name)
    {
        this.author = author;
        this.name = name;
    }
    
    public String getAuthor()
    {
        return author;
    }
    
    public void setAuthor(String author)
    {
        this.author = author;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getCompleteName(String delimiter, String defaultName)
    {
        String result;
        boolean nameExists = name.length() > 0;
        boolean authorExists = author.length() > 0;
        
        if (nameExists) {
            if (authorExists) {
                result = name + delimiter + author;
            } else {
                result = name;
            }
        } else {
            if (authorExists) {
                result = author;
            } else {
                result = defaultName;
            }
        }
        
        return result;
    }
    
    public void readFromBytes(byte[] bytes, boolean authorExists, boolean nameExists) throws DisplayException
    {
        if (authorExists) {
            int offset = 0;
            int length = CanComfort.TRACK_LIST_TRACK_AUTHOR_LENGTH;
            if (bytes.length < offset + length) {
                String str = new String(Hex.encodeHex(bytes));
                throw new DisplayException("Unexpected data `" + str + "`");
            }
            int actualLength = length;
            for (int j=0; j<length; j++) {
                if (bytes[offset+j] == 0x00) {
                    actualLength = j;
                    break;
                }
            }
            this.setAuthor(new String(bytes, offset, actualLength, CanComfort.charset));
        }
        
        if (nameExists) {
            int offset = authorExists ? CanComfort.TRACK_LIST_TRACK_AUTHOR_LENGTH : 0;
            int length = CanComfort.TRACK_LIST_TRACK_NAME_LENGTH;
            if (bytes.length < offset + length) {
                String str = new String(Hex.encodeHex(bytes));
                throw new DisplayException("Unexpected data `" + str + "`");
            }
            int actualLength = length;
            for (int j=0; j<length; j++) {
                if (bytes[offset+j] == 0x00) {
                    actualLength = j;
                    break;
                }
            }
            this.setName(new String(bytes, offset, actualLength, CanComfort.charset));
        }
    }
}
