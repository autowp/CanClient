/**
 * 
 */
package com.autowp.canclient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * @author Dmitry
 *
 */
public class CanClient {
    protected CanAdapter adapter;
    
    private List<Timer> timers = new ArrayList<Timer>();
    
    private List<FrameSentEventClassListener> frameSentListeners = 
            new ArrayList<FrameSentEventClassListener>();
    
    private List<FrameReceivedEventClassListener> frameReceivedListeners = 
            new ArrayList<FrameReceivedEventClassListener>();
    
    private FrameReceivedEventClassListener frameReceivedEventClassListener = 
            new FrameReceivedEventClassListener() {
                public void handleFrameReceivedEvent(FrameReceivedEvent e) {
                    fireFrameReceivedEvent(e.getFrame());
                }
            };
    
    private FrameSentEventClassListener frameSentEventClassListener = 
            new FrameSentEventClassListener() {
                public void handleFrameSentEvent(FrameSentEvent e) {
                    fireFrameSentEvent(e.getFrame());
                }
            };
    
    public CanClient()
    {
        
    }
    
    public CanClient connect() throws Exception
    {
        if (this.isConnected()) {
            return this;
        }
        
        if (adapter == null) {
            throw new Exception("Adapter not specified");
        }
        adapter.addEventListener(frameReceivedEventClassListener);
        adapter.addEventListener(frameSentEventClassListener);
        adapter.connect();
        
        return this;
    }
    
    public CanClient disconnect()
    {
        this.stopTimers();
        
        adapter.disconnect();
        
        adapter.removeEventListener(frameReceivedEventClassListener);
        adapter.removeEventListener(frameSentEventClassListener);
        
        return this;
    }
    
    public CanClient stopTimers()
    {
        for (Timer t : this.timers) {
            t.cancel();
        }
        this.timers.clear();
        
        return this;
    }
    
    public boolean isConnected()
    {
        return adapter != null && adapter.isConnected();
    }
    
    public CanClient send(CanFrame message) throws Exception
    {
        if (!this.isConnected()) {
            throw new Exception("CanClient is not connected");
        }
        
        adapter.send(message);
        
        return this;
    }
    
    public void setAdapter(CanAdapter adapter) {
        this.adapter = adapter;
    }
    
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
    
    public void addTimerTaskFrame(CanFrame frame, long delay, long period)
    {
        Timer timer = new Timer();
        timer.schedule(new FrameTimerTask(this, frame), delay, period);
        
        this.timers.add(timer);
    }
    
    public class FrameTimerTask extends TimerTask {
        private CanClient client;
        private CanFrame frame;
        
        public FrameTimerTask(CanClient client, CanFrame frame)
        {
            this.client = client;
            this.frame = frame;
        }
        
        public void run() {
            try {
                this.client.send(this.frame);
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }
    }

    public void sendDelayedFrame(final CanFrame frame, int delay) {
        final CanClient client = this;
        new Timer().schedule(new TimerTask() {          
            @Override
            public void run() {
                try {
                    client.send(frame);
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            }
        }, delay);
    }
}
