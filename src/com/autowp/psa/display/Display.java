package com.autowp.psa.display;

import java.util.Timer;
import java.util.TimerTask;




import com.autowp.can.CanClient;
import com.autowp.can.CanMessage;
import com.autowp.psa.CanComfort;
import com.autowp.psa.message.CurrentCDTrackMessage;
import com.autowp.psa.message.MessageException;
import com.autowp.psa.message.Track;
import com.autowp.psa.message.VolumeMessage;

public class Display {
    
    private Scheme scheme = Scheme.factory("default"); 
    
    /*private TrackList trackList;
    private Volume volume;*/
    
    private static final int VOLUME_HIDE_DELAY = 2500; // ms
    
    private TimerTask hideVolumeTimerTask = null;
    
    private Timer volumeTimer = new Timer();
    
    protected DisplayPanel displayPanel;
    
    public Display(CanClient client, DisplayPanel displayPanel)
    {
        this.displayPanel = displayPanel;
        displayPanel.setScheme(scheme);
        
        /*trackList = new TrackList(scheme);
        trackList.setVisible(false);
        
        volume = new Volume(scheme);
        volume.setVisible(false);*/
        
        /*this.add(trackList, c);
        
        this.add(volume, c);*/
        
        if (client != null) {
            client.addEventListener(new CanClient.OnCanMessageTransferListener() {
                @Override
                public void handleCanMessageReceivedEvent(CanMessage message) {
                    try {
                        processMessage(message);
                    } catch (Exception ex) {
                        ex.printStackTrace(System.err);
                    }
                }

                @Override
                public void handleCanMessageSentEvent(CanMessage message) {
                    // TODO Auto-generated method stub
                    
                }
            });
        }
    }
    
    /*public TrackList getTrackList()
    {
        return trackList;
    }*/
    
    public void processMessage(CanMessage message) throws DisplayException
    {
        try {
            switch (message.getId()) {
                case CanComfort.ID_TRACK_LIST:
                    //trackList.processMessage(message);
                    break;
                    
                case CanComfort.ID_VOLUME: {
                    
                    VolumeMessage peugeotMessage = new VolumeMessage(message);
                    
                    final VolumePanel volumePanel = displayPanel.getVolumePanel();
                    
                    //boolean oldVisible = volumePanel.isVisible();
                    
                    if (peugeotMessage.getShow()) {
                        volumePanel.setVisible(true);
                        
                        if (hideVolumeTimerTask != null) {
                            hideVolumeTimerTask.cancel();
                            hideVolumeTimerTask = null;
                        }
                        hideVolumeTimerTask = new TimerTask() {
                            @Override
                            public void run() {
                                try {
                                    volumePanel.setVisible(false);
                                } catch (Exception e) {
                                    e.printStackTrace(System.out);
                                }
                            }
                        };
                        volumeTimer.schedule(hideVolumeTimerTask, VOLUME_HIDE_DELAY);
                    }
                    
                    /*if (oldVisible != show) {
                        volumePanel.invalidate();
                    }*/
                    
                    volumePanel.getVolumeGridPanel().setVolume(peugeotMessage.getVolume());
                    break;
                }
                    
                case CanComfort.ID_CURRENT_CD_TRACK: {
                    
                    CurrentCDTrackMessage peugeotMessage = new CurrentCDTrackMessage(message);
                    Track track = peugeotMessage.getTrack();
                    
                    displayPanel.getMainPanel().getAudioPanel().getCDPanel()
                        .setTrackName(track.getCompleteName("/", ""));
                    
                    break;
                }
                
                case CanComfort.ID_PARKTRONIC:
                    
                    break;
                    
                default:
                    // just skip
            }
        
        } catch (MessageException e1) {
            throw new DisplayException(e1.getMessage());
        }
    }
}
