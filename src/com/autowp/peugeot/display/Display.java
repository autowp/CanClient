package com.autowp.peugeot.display;

import java.awt.Color;
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
    
    private static final Color BACKGROUND = Color.BLACK;
    private static final Color FOREGROUND = Color.ORANGE;
    
    private TrackList trackList;
    
    public Display(CanClient client)
    {
        this.setBackground(BACKGROUND);
        this.setForeground(FOREGROUND);
        
        GridBagLayout layout = new GridBagLayout();

        this.setLayout(layout);
        
        trackList = new TrackList();
        trackList.setVisible(false);
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(trackList, c);
        
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
                
            default:
                // just skip
        }
    }
}
