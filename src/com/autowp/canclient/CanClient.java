/**
 * 
 */
package com.autowp.canclient;

import java.util.ArrayList;
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
        adapter.connect();
        
        return this;
    }
    
    public CanClient disconnect()
    {
        adapter.disconnect();
        
        return this.stopTimers();
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
    
    public void addEventListener(FrameSentEventClassListener listener) {
        adapter.addEventListener(listener);
    }
    
    public void removeEventListener(FrameSentEventClassListener listener){
        adapter.removeEventListener(listener);
    }
    
    public void addEventListener(FrameReceivedEventClassListener listener) {
        adapter.addEventListener(listener);
    }
    
    public void removeEventListener(FrameReceivedEventClassListener listener){
        adapter.removeEventListener(listener);
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
