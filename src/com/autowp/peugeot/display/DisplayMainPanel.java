package com.autowp.peugeot.display;

import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.BorderLayout;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class DisplayMainPanel extends JPanel {
    
    protected JPanel upperPanel;
    protected JLabel temperatureLabel;
    protected JLabel timeLabel;
    protected DisplaySideClimatPanel sidePanel;
    protected DisplayAudioPanel mainPanel; 

    /**
     * Create the panel.
     */
    public DisplayMainPanel() {
        
        Color background = Color.BLACK;
        Color foreground = Color.ORANGE;
        Font font = new Font("Arial", Font.PLAIN, 14);
        
        setBackground(background);
        setLayout(new MigLayout("", "[40px,grow 40][40,grow 40][300px,grow 300]", "[31px][163px]"));
        
        upperPanel = new JPanel();
        upperPanel.setForeground(foreground);
        upperPanel.setBackground(background);
        add(upperPanel, "cell 1 0 2 1,grow");
        upperPanel.setLayout(new BorderLayout(0, 0));
        
        temperatureLabel = new JLabel("--\u00B0");
        temperatureLabel.setFont(font);
        temperatureLabel.setHorizontalAlignment(SwingConstants.LEFT);
        temperatureLabel.setBackground(new Color(0, 0, 0));
        temperatureLabel.setForeground(foreground);
        upperPanel.add(temperatureLabel, BorderLayout.WEST);
        
        timeLabel = new JLabel("0:00");
        timeLabel.setFont(font);
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        timeLabel.setForeground(foreground);
        timeLabel.setBackground(background);
        upperPanel.add(timeLabel, BorderLayout.EAST);
        
        sidePanel = new DisplaySideClimatPanel();
        sidePanel.setForeground(foreground);
        sidePanel.setBackground(background);
        add(sidePanel, "cell 0 1 2 1,alignx left,growy");
        
        mainPanel = new DisplayAudioPanel();
        mainPanel.setForeground(foreground);
        mainPanel.setBackground(background);
        add(mainPanel, "cell 2 1,grow");
        
    }

    public void setScheme(Scheme scheme) {
        
        Color foreground = scheme.getForeground();
        Color background = scheme.getBackground();
        
        this.setForeground(foreground);
        this.setBackground(background);
        
        upperPanel.setForeground(foreground);
        upperPanel.setBackground(background);
        
        temperatureLabel.setForeground(foreground);
        temperatureLabel.setBackground(background);
        
        timeLabel.setForeground(foreground);
        timeLabel.setBackground(background);
        
        sidePanel.setScheme(scheme);
        
        mainPanel.setScheme(scheme);
    }

    public DisplayAudioPanel getAudioPanel() {
        return mainPanel;
    }

}
