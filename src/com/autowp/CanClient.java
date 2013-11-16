/**
 * 
 */
package com.autowp;

import com.autowp.canhacker.CanHacker;




/**
 * @author Dmitry
 *
 */
public class CanClient {
    protected CanHacker canHacker;
    
    public CanClient()
    {
        canHacker = new CanHacker();
        canHacker.setSpeed(57600);
    }
    
    public CanClient connect() throws Exception
    {
        canHacker.connect();
        
        return this;
    }
    
    public CanClient disconnect()
    {
        canHacker.disconnect();
        
        return this;
    }
    
    public CanClient setPortName(String portName)
    {
        canHacker.setPortName(portName);
        
        return this;
    }
    
    public String getPortName()
    {
        return canHacker.getPortName();
    }
    
    public boolean isConnected()
    {
        return canHacker.isConnected();
    }
    
    public CanClient send(CanMessage message)
    {
        //canHacker.send(message);
        
        return this;
    }
    
    public CanHacker getCanHacker()
    {
        return canHacker;
    }
}
