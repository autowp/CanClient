package com.autowp.canclient;

import java.util.EventObject;

@SuppressWarnings("serial")
public class CanFrameEvent extends EventObject {
    protected CanFrame frame;

    public CanFrameEvent(Object source, CanFrame frame) {
        super(source);
        this.frame = frame;
    }
    
    public CanFrame getFrame()
    {
        return frame;
    }
}
