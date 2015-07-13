package com.autowp.peugeot.bsi;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.autowp.can.CanClient;
import com.autowp.can.CanClientException;
import com.autowp.can.CanFrame;
import com.autowp.can.CanFrameException;
import com.autowp.peugeot.CanComfort;
import com.autowp.peugeot.message.BSIInfoMessage;
import com.autowp.peugeot.message.BSIInfoWindowMessage;
import com.autowp.peugeot.message.BSIStatusMessage;

public class BSI {
    private CanClient mCanClient;
    private String mVIN = null;
    private Timer mStatusTimer;
    private Timer mInfoTimer;
    private boolean mReceive = true;
    private BSIInfoWindowMessage mInfowWindowMessage;
    private Timer mInfoWindowTimer;
    public BSIStatusMessage mStatusMessage;
    
    private class BSITimerTask extends TimerTask {
        private CanFrame frame;
        
        public BSITimerTask(CanFrame frame)
        {
            this.frame = frame;
        }
        
        public void run() {
            try {
                mCanClient.send(this.frame);
                if (mReceive) {
                    mCanClient.receive(this.frame);
                }
            } catch (CanClientException e) {
                //fireErrorEvent(e);
            }
        }
    }
    
    private class StatusTimerTask extends TimerTask {
        public void run() {
            try {
                CanFrame frame = mStatusMessage.assembleFrame();
                mCanClient.send(frame);
                if (mReceive) {
                    mCanClient.receive(frame);
                }
            } catch (CanFrameException | CanClientException e) {
                //fireErrorEvent(e);
            }
        }
    }
    
    private class InfowWindowTimerTask extends TimerTask {
        public void run() {
            
            try {
                CanFrame frame = mInfowWindowMessage.assembleFrame();
                mCanClient.send(frame);
                if (mReceive) {
                    mCanClient.receive(frame);
                }
                if (mInfowWindowMessage.getAction() == BSIInfoWindowMessage.ACTION_HIDE) {
                    mInfowWindowMessage.setAction(BSIInfoWindowMessage.ACTION_CLEAR);
                }
            } catch (CanClientException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (CanFrameException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
    }

    public BSI(CanClient canClient) {
        mCanClient = canClient;
        
        mInfowWindowMessage = new BSIInfoWindowMessage();
        mStatusMessage = new BSIStatusMessage();
    }

    public String getVIN() {
        return mVIN;
    }

    public void setVIN(String VIN) {
        mVIN = VIN;
    }
    
    public void sendVIN() throws BSIException, CanFrameException {
        mCanClient.sendDelayedFrame(vinFrame(mVIN), CanComfort.VIN_DELAY, true);
    }
    
    private static CanFrame vinFrame(String vin) throws BSIException, CanFrameException 
    {
        int length = vin.length();
        if (length < CanComfort.VIN_LENGTH) {
            throw new BSIException("Vin require at least " + CanComfort.VIN_LENGTH + " last digits");
        }
        
        String lastDigits = vin.substring(length - CanComfort.VIN_LENGTH, length);
        return new CanFrame(CanComfort.ID_VIN, lastDigits.getBytes(CanComfort.charset));
    }
    
    public void startStatus() throws CanFrameException
    {
        stopStatus();
        
        StatusTimerTask task = new StatusTimerTask();
        
        Timer timer = new Timer();
        timer.schedule(task, 0, CanComfort.BSI_STATUS_PERIOD);
        
        mStatusTimer = timer;
    }
    
    public void stopStatus()
    {
        if (mStatusTimer != null) {
            mStatusTimer.cancel();
            mStatusTimer = null;
        }
    }
    
    private static CanFrame infoFrame() throws CanFrameException
    {
        final Random random = new Random();
        BSIInfoMessage message = new BSIInfoMessage();
        message.setGear((byte) random.nextInt(7));
        message.setIsReverse(random.nextBoolean());
        message.setTemperature(random.nextFloat() * 125 - 40);
        message.setOdometer(random.nextInt(0x00FFFFFF));
        
        return message.assembleFrame();
    }
    
    public void startInfo() throws CanFrameException
    {
        stopInfo();
        
        BSITimerTask task = new BSITimerTask(infoFrame());
        
        Timer timer = new Timer();
        timer.schedule(task, 0, CanComfort.BSI_INFO_PERIOD);
        
        mInfoTimer = timer;
    }
    
    public void stopInfo()
    {
        if (mInfoTimer != null) {
            mInfoTimer.cancel();
            mInfoTimer = null;
        }
    }

    public boolean isStatusStarted() {
        return mStatusTimer != null;
    }
    
    public boolean isInfoStarted() {
        return mInfoTimer != null;
    }

    public boolean isReceive() {
        return mReceive;
    }

    public void setReceive(boolean receive) {
        mReceive = receive;
    }
    
    public void startInfoWindow() throws CanFrameException
    {
        stopInfoWindow();
        
        InfowWindowTimerTask task = new InfowWindowTimerTask();
        
        Timer timer = new Timer();
        timer.schedule(task, 0, CanComfort.BSI_INFO_WINDOW_PERIOD);
        
        mInfoWindowTimer = timer;
    }
    
    public void stopInfoWindow()
    {
        if (mInfoWindowTimer != null) {
            mInfoWindowTimer.cancel();
            mInfoWindowTimer = null;
        }
    }
    
    public boolean isInfoWindowStarted() {
        return mInfoWindowTimer != null;
    }
    
    public void setInfoWindowAction(byte action) {
        mInfowWindowMessage.setAction(action);
    }

    public void setInfoWindowCode(byte code) {
        mInfowWindowMessage.setCode(code);
    }

    public void setDahsboardLightingEnabled(boolean enabled) {
        mStatusMessage.setDashboardLightningEnabled(enabled);
    }

    public void setDashboardLightingBrightness(byte value) {
        mStatusMessage.setDashboardLightningBrightness(value);
    }

    public byte getDashboardLightingBrightness() {
        return mStatusMessage.getDashboardLightningBrightness();
    }

    public boolean isDashboardLightingEnabled() {
        return mStatusMessage.isDashboardLightningEnabled();
    }
}
