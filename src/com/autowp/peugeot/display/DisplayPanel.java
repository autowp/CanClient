package com.autowp.peugeot.display;

import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.JLayeredPane;

@SuppressWarnings("serial")
public class DisplayPanel extends JPanel {
    protected DisplayMainPanel mainPanel;
    protected VolumePanel volumePanel;
    
    private final static double VOLUME_OFFSET_V = 0.2;
    private final static double VOLUME_OFFSET_H = 0.2;
    
    /**
     * Create the panel.
     */
    public DisplayPanel() {
        setLayout(new CardLayout(0, 0));
        
        JLayeredPane layeredPane = new JLayeredPane();
        add(layeredPane, "name_122511086541626");
        
        mainPanel = new DisplayMainPanel();
        mainPanel.setBounds(0, 0, 450, 300);
        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);
        
        volumePanel = new VolumePanel();
        volumePanel.setBounds(100, 50, 250, 200);
        volumePanel.setVisible(false);
        layeredPane.add(volumePanel, JLayeredPane.POPUP_LAYER);
        
        this.addComponentListener(new ComponentListener() 
        {  
            @Override
            public void componentResized(ComponentEvent evt) {
                Rectangle bounds = evt.getComponent().getBounds();
                mainPanel.setBounds(0, 0, bounds.width, bounds.height);
                
                double vOffset = bounds.height * VOLUME_OFFSET_V;
                double hOffset = bounds.width * VOLUME_OFFSET_H;
                double height = bounds.height - 2 * vOffset;
                double width = bounds.width - 2 * hOffset;
                
                volumePanel.setBounds((int) hOffset, (int) vOffset, (int) width, (int) height);
            }

            @Override
            public void componentHidden(ComponentEvent arg0) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void componentMoved(ComponentEvent arg0) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void componentShown(ComponentEvent arg0) {
                // TODO Auto-generated method stub
                
            }
        });
    }

    public void setScheme(Scheme scheme) {
        this.setForeground(scheme.getForeground());
        this.setBackground(scheme.getBackground());
        
        mainPanel.setScheme(scheme);
        volumePanel.setScheme(scheme);
    }

    public DisplayMainPanel getMainPanel() {
        return mainPanel;
    }
    
    public VolumePanel getVolumePanel() {
        return volumePanel;
    }
}
