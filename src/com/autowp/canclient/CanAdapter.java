package com.autowp.canclient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class CanAdapter {
    public abstract void send(CanFrame message) throws Exception;
    
    public abstract void connect() throws Exception;
    
    public abstract void disconnect();
    
    public abstract boolean isConnected();
    
    private List<FrameSentEventClassListener> frameSentListeners = new ArrayList<FrameSentEventClassListener>();
    
    private List<FrameReceivedEventClassListener> frameReceivedListeners = new ArrayList<FrameReceivedEventClassListener>();
    
    public synchronized void addEventListener(FrameSentEventClassListener listener) {
        frameSentListeners.add(listener);
    }
    
    public synchronized void removeEventListener(FrameSentEventClassListener listener){
        frameSentListeners.remove(listener);
    }
    
    protected synchronized void fireFrameSentEvent(CanFrame frame)
    {
        FrameSentEvent event = new FrameSentEvent(this, frame);
        Iterator<FrameSentEventClassListener> i = frameSentListeners.iterator();
        while(i.hasNext())  {
            ((FrameSentEventClassListener) i.next()).handleFrameSentEvent(event);
        }
    }
    
    public synchronized void addEventListener(FrameReceivedEventClassListener listener) {
        frameReceivedListeners.add(listener);
    }
    
    public synchronized void removeEventListener(FrameReceivedEventClassListener listener){
        frameReceivedListeners.remove(listener);
    }
    
    protected synchronized void fireFrameReceivedEvent(CanFrame frame)
    {
        FrameReceivedEvent event = new FrameReceivedEvent(this, frame);
        Iterator<FrameReceivedEventClassListener> i = frameReceivedListeners.iterator();
        while(i.hasNext())  {
            ((FrameReceivedEventClassListener) i.next()).handleFrameReceivedEvent(event);
        }
    }
}
