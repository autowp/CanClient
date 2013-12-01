/**
 * 
 */
package com.autowp.canclient;

import java.util.ArrayList;
import java.util.HashMap;
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
    
    private static final int PCITYPE_SINGLE_FRAME = 0;
    private static final int PCITYPE_FIRST_FRAME = 1;
    private static final int PCITYPE_CONSECUTIVE_FRAME = 2;
    private static final int PCITYPE_FLOW_CONTROL_FRAME = 3;
    
    private List<Timer> timers = new ArrayList<Timer>();
    
    private List<CanFrameEventClassListener> canFrameEventListeners = 
            new ArrayList<CanFrameEventClassListener>();
    
    private CanFrameEventClassListener canFrameEventClassListener = 
            new CanClientFrameEventClassListener();
            
    private List<CanMessageEventClassListener> canMessageEventListeners = 
            new ArrayList<CanMessageEventClassListener>();

    
    public CanClient()
    {
        
    }
    
    public CanClient connect() throws CanClientException
    {
        if (this.isConnected()) {
            return this;
        }
        
        if (adapter == null) {
            throw new CanClientException("Adapter not specified");
        }
        adapter.addEventListener(canFrameEventClassListener);
        try {
            adapter.connect();
        } catch (CanAdapterException e) {
            throw new CanClientException("Adapter error: " + e.getMessage());
        }
        
        return this;
    }
    
    public CanClient disconnect()
    {
        this.stopTimers();
        
        adapter.disconnect();
        
        adapter.removeEventListener(canFrameEventClassListener);
        
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
    
    public CanClient send(CanFrame message) throws CanClientException
    {
        if (!this.isConnected()) {
            throw new CanClientException("CanClient is not connected");
        }
        
        try {
            adapter.send(message);
        } catch (CanAdapterException e) {
            throw new CanClientException("Adapter error: " + e.getMessage());
        }
        
        return this;
    }
    
    public void setAdapter(CanAdapter adapter) {
        this.adapter = adapter;
    }
    
    public synchronized void addEventListener(CanFrameEventClassListener listener) {
        canFrameEventListeners.add(listener);
    }
    
    public synchronized void removeEventListener(CanFrameEventClassListener listener){
        canFrameEventListeners.remove(listener);
    }
    
    protected synchronized void fireCanFrameSentEvent(CanFrame frame)
    {
        CanFrameEvent event = new CanFrameEvent(this, frame);
        Iterator<CanFrameEventClassListener> i = canFrameEventListeners.iterator();
        while(i.hasNext())  {
            ((CanFrameEventClassListener) i.next()).handleCanFrameSentEvent(event);
        }
    }
    
    protected synchronized void fireCanFrameReceivedEvent(CanFrame frame)
    {
        CanFrameEvent event = new CanFrameEvent(this, frame);
        Iterator<CanFrameEventClassListener> i = canFrameEventListeners.iterator();
        while(i.hasNext())  {
            ((CanFrameEventClassListener) i.next()).handleCanFrameReceivedEvent(event);
        }
    }
    
    public synchronized void addEventListener(CanMessageEventClassListener listener) {
        canMessageEventListeners.add(listener);
    }
    
    public synchronized void removeEventListener(CanMessageEventClassListener listener){
        canMessageEventListeners.remove(listener);
    }
    
    protected synchronized void fireCanMessageSentEvent(CanMessage message)
    {
        CanMessageEvent event = new CanMessageEvent(this, message);
        Iterator<CanMessageEventClassListener> i = canMessageEventListeners.iterator();
        while(i.hasNext())  {
            ((CanMessageEventClassListener) i.next()).handleCanMessageSentEvent(event);
        }
    }
    
    protected synchronized void fireCanMessageReceivedEvent(CanMessage message)
    {
        CanMessageEvent event = new CanMessageEvent(this, message);
        Iterator<CanMessageEventClassListener> i = canMessageEventListeners.iterator();
        while(i.hasNext())  {
            ((CanMessageEventClassListener) i.next()).handleCanMessageReceivedEvent(event);
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
    
    private class CanClientFrameEventClassListener implements CanFrameEventClassListener {
        HashMap<Integer, MultiFrameBuffer> multiframeBuffers = new HashMap<Integer, MultiFrameBuffer>();
        
        public void handleCanFrameSentEvent(CanFrameEvent e) {
            CanFrame frame = e.getFrame();
            fireCanFrameSentEvent(frame);
            
            
            
        }
        public void handleCanFrameReceivedEvent(CanFrameEvent e) {
            CanFrame frame = e.getFrame();
            fireCanFrameReceivedEvent(frame);
            
            try {
                int arbID = frame.getId();
                // check is multiFrame
                if (arbID == 0x125) { // TODO: proper check for multipart
                    
                    byte[] data = frame.getData();
                    if (data.length <= 0) {
                        throw new CanClientException("Unexpected zero size can message");
                    }
                    
                    int pciType = (data[0] & 0xF0) >>> 4;
                    
                    switch (pciType) {
                        case PCITYPE_SINGLE_FRAME: {
                            int dataLength = data[0] & 0x0F;
                            byte[] messageData = new byte[dataLength];
                            System.arraycopy(data, 1, messageData, 0, dataLength);
                            fireCanMessageReceivedEvent(
                                new CanMessage(arbID, messageData)
                            );
                            break;
                        }
                            
                        case PCITYPE_FIRST_FRAME: {
                            int dataLengthHigh = data[0] & 0x0F;
                            int dataLengthLow = (int) data[1] & 0xFF;
                            
                            int dataLength = (dataLengthHigh << 8) + dataLengthLow;
                            
                            byte[] messageData = new byte[data.length - 2];
                            System.arraycopy(data, 2, messageData, 0, data.length - 2);
                            
                            MultiFrameBuffer buffer = new MultiFrameBuffer(dataLength);
                            buffer.append(messageData, 0);
                            
                            multiframeBuffers.put(arbID, buffer);
                            break;
                        }
                            
                        case PCITYPE_CONSECUTIVE_FRAME: {
                           
                            int index = data[0] & 0x0F;
                            byte[] messageData = new byte[data.length - 1];
                            System.arraycopy(data, 1, messageData, 0, data.length - 1);
                            
                            MultiFrameBuffer buffer = multiframeBuffers.get(arbID);
                            if (buffer == null) {
                                throw new CanClientException("Buffer for " + arbID + " not found");
                            }
                            
                            buffer.append(messageData, index);
                            
                            if (buffer.isComplete()) {
                                fireCanMessageReceivedEvent(
                                    new CanMessage(arbID, buffer.getData())
                                );
                                multiframeBuffers.remove(arbID);
                            }
                            
                            break;
                        }
                            
                        case PCITYPE_FLOW_CONTROL_FRAME:
                            // TODO: 
                            break;
                            
                        default:
                            throw new CanClientException("Unexpected PCITYPE " + pciType);
                    }
                            
                } else {
                    fireCanMessageReceivedEvent(
                        new CanMessage(arbID, frame.getData())
                    );
                }
            } catch (CanClientException | CanMessageException ex) {
                ex.printStackTrace();
            }
        }
    };
    
    protected class MultiFrameBuffer {
        int currentLength;
        int lastCounter;
        byte[] buffer;
        
        public MultiFrameBuffer(int expectedLength)
        {
            this.currentLength = 0;
            this.buffer = new byte[expectedLength];
            this.lastCounter = -1; // initial value to match first 0
        }
        
        public void append(byte[] data, int cycleCounter) throws CanClientException
        {
            if (currentLength + data.length > buffer.length) {
                throw new CanClientException("Buffer overflow detected");
            }
            
            if (cycleCounter != (lastCounter + 1) % 16) {
                throw new CanClientException("Cycle counter breaks from " + lastCounter + " to " + cycleCounter);
            }
            
            System.arraycopy(data, 0, buffer, currentLength, data.length);
            
            currentLength += data.length;
            
            lastCounter = cycleCounter;
        }
        
        public boolean isComplete()
        {
            return buffer.length == currentLength;
        }
        
        public byte[] getData()
        {
            return buffer;
        }
    }
}
