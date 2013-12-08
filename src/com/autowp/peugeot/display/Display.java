package com.autowp.peugeot.display;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.codec.binary.Hex;

import com.autowp.canclient.CanClient;
import com.autowp.canclient.CanMessage;
import com.autowp.canclient.CanMessageEvent;
import com.autowp.canclient.CanMessageEventClassListener;
import com.autowp.peugeot.CanComfort;

public class Display {
    
    private Scheme scheme = Scheme.factory("default"); 
    
    /*private TrackList trackList;
    private Volume volume;*/
    
    protected Track currentCDTrack = new Track();
    
    private static final int MIN_VOLUME = 0;
    private static final int MAX_VOLUME = 30;
    
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
            client.addEventListener(new CanMessageEventClassListener() {
                @Override
                public void handleCanMessageReceivedEvent(CanMessageEvent e) {
                    try {
                        processMessage(e.getMessage());
                    } catch (Exception ex) {
                        ex.printStackTrace(System.err);
                    }
                }

                @Override
                public void handleCanMessageSentEvent(CanMessageEvent e) {
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
        switch (message.getId()) {
            case CanComfort.ID_TRACK_LIST:
                //trackList.processMessage(message);
                break;
                
            case CanComfort.ID_VOLUME: {
                byte[] data = message.getData();
                if (data.length != 1) {
                    String str = new String(Hex.encodeHex(data));
                    throw new DisplayException("Unexpected length of volume message `" + str + "`");
                }
                int volume = (int)data[0] & 0x1F;
                boolean show = ((int)data[0] & 0xE0) == 0x00;
                
                if (volume < MIN_VOLUME) {
                    String str = new String(Hex.encodeHex(data));
                    throw new DisplayException("Volume cannot be < " + MIN_VOLUME + " `" + str + "`");
                }
                
                if (volume > MAX_VOLUME) {
                    String str = new String(Hex.encodeHex(data));
                    throw new DisplayException("Volume cannot be > " + MAX_VOLUME + " `" + str + "`");
                }
                
                final VolumePanel volumePanel = displayPanel.getVolumePanel();
                
                boolean oldVisible = volumePanel.isVisible();
                
                if (show) {
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
                
                volumePanel.getVolumeGridPanel().setVolume(volume);
                break;
            }
                
            case CanComfort.ID_CURRENT_CD_TRACK: {
                byte[] data = message.getData();
                if (data.length == 5) {
                    final byte[] emptyData = new byte[] {0x20, 0x00, 0x00, 0x00, 0x00};
                    if (!Arrays.equals(data, emptyData)) {
                        String str = new String(Hex.encodeHex(data));
                        throw new DisplayException("Unexpected data `" + str + "`");
                    }
                    
                    currentCDTrack.setAuthor("");
                    currentCDTrack.setName("");
                    
                } else {
                    
                    if (!(data[0] == 0x20 && data[1] == 0x00)) {
                        String str = new String(Hex.encodeHex(data));
                        throw new DisplayException("Unexpected data `" + str + "`");
                    }
                    
                    if ((data[2] & 0xEF) != 0x48) {
                        String str = new String(Hex.encodeHex(data));
                        throw new DisplayException("Unexpected data `" + str + "`");
                    }
                    
                    boolean trackAuthorExists = (data[2] & 0x10) == 0x10;
                    int textDataOffset = 4;
                    byte[] textData = new byte[data.length - textDataOffset];
                    System.arraycopy(data, textDataOffset, textData, 0, textData.length);
                    currentCDTrack.readFromBytes(textData, trackAuthorExists, true);
                }
                
                displayPanel.getMainPanel().getAudioPanel().getCDPanel()
                    .setTrackName(currentCDTrack.getCompleteName("/", ""));
                
                System.out.println(currentCDTrack.getCompleteName("/", ""));
                
                break;
            }
                
            default:
                // just skip
        }
    }
}
