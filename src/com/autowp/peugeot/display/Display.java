package com.autowp.peugeot.display;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JPanel;

import com.autowp.canclient.CanClient;
import com.autowp.canclient.CanMessage;
import com.autowp.canclient.CanMessageEvent;
import com.autowp.canclient.CanMessageEventClassListener;
import com.autowp.peugeot.CanComfort;

@SuppressWarnings("serial")
public class Display extends JPanel {
    
    private Scheme scheme = Scheme.factory("inverse"); 
    
    private TrackList trackList;
    private Volume volume;
    
    private CurrentCDTrack currentCDTrack;
    
    public Display(CanClient client)
    {
        this.setBackground(scheme.getBackground());
        this.setForeground(scheme.getForeground());
        
        GridBagLayout layout = new GridBagLayout();

        this.setLayout(layout);
        
        trackList = new TrackList(scheme);
        trackList.setVisible(false);
        
        volume = new Volume(scheme);
        volume.setVisible(false);
        
        currentCDTrack = new CurrentCDTrack(scheme);
        currentCDTrack.setVisible(false);
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(trackList, c);
        
        this.add(volume, c);
        
        this.add(currentCDTrack, c);
        
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
    
    public TrackList getTrackList()
    {
        return trackList;
    }
    
    public void processMessage(CanMessage message) throws DisplayException
    {
        switch (message.getId()) {
            case CanComfort.ID_TRACK_LIST:
                trackList.processMessage(message);
                break;
                
            case CanComfort.ID_VOLUME:
                volume.processMessage(message);
                break;    
                
            case CanComfort.ID_CURRENT_CD_TRACK:
                currentCDTrack.processMessage(message);
                break;
                
            default:
                // just skip
        }
    }
}
