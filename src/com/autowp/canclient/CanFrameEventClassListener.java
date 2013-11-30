package com.autowp.canclient;

public interface CanFrameEventClassListener {
    public void handleCanFrameReceivedEvent(CanFrameEvent e);
    
    public void handleCanFrameSentEvent(CanFrameEvent e);
}
