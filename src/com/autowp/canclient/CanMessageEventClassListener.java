package com.autowp.canclient;

public interface CanMessageEventClassListener {
    public void handleCanMessageReceivedEvent(CanMessageEvent e);
    
    public void handleCanMessageSentEvent(CanMessageEvent e);
}
