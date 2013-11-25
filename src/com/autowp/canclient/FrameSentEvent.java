package com.autowp.canclient;

import java.util.EventObject;

public class FrameSentEvent extends EventObject {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    protected CanFrame frame;

    public FrameSentEvent(Object source, CanFrame frame) {
        super(source);
        this.frame = frame;
    }
    
    public CanFrame getFrame()
    {
        return frame;
    }
}
