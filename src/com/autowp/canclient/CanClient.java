/**
 * 
 */
package com.autowp.canclient;


/**
 * @author Dmitry
 *
 */
public class CanClient {
    protected CanAdapter adapter;
    
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
}
