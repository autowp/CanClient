package com.autowp.canclient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class CanAdapter {
    public abstract void send(CanFrame message) throws Exception;
    
    public abstract void connect() throws Exception;
    
    public abstract void disconnect();
    
    public abstract boolean isConnected();
    
    private List<CanFrameEventClassListener> canFrameEventListeners = new ArrayList<CanFrameEventClassListener>();
    
    public synchronized void addEventListener(CanFrameEventClassListener listener) {
        canFrameEventListeners.add(listener);
    }
    
    public synchronized void removeEventListener(CanFrameEventClassListener listener){
        canFrameEventListeners.remove(listener);
    }
    
    protected synchronized void fireFrameSentEvent(CanFrame frame)
    {
        CanFrameEvent event = new CanFrameEvent(this, frame);
        Iterator<CanFrameEventClassListener> i = canFrameEventListeners.iterator();
        while(i.hasNext())  {
            ((CanFrameEventClassListener) i.next()).handleCanFrameSentEvent(event);
        }
    }
    
    protected synchronized void fireFrameReceivedEvent(CanFrame frame)
    {
        CanFrameEvent event = new CanFrameEvent(this, frame);
        Iterator<CanFrameEventClassListener> i = canFrameEventListeners.iterator();
        while(i.hasNext())  {
            ((CanFrameEventClassListener) i.next()).handleCanFrameReceivedEvent(event);
        }
    }
}
