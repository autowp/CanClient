package com.autowp.canclient;

import java.util.EventObject;

@SuppressWarnings("serial")
public class CanMessageEvent extends EventObject {

    protected CanMessage message;

    public CanMessageEvent(Object source, CanMessage message) {
        super(source);
        this.message = message;
    }
    
    public CanMessage getMessage()
    {
        return message;
    }

}
