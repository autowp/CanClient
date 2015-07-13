package com.autowp.psa.display;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Font;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class DisplayAudioCDPanel extends JPanel {

    private JLabel trackNameLabel;
    private JLabel timeLabel;
    
    /**
     * Create the panel.
     */
    public DisplayAudioCDPanel() {
        Color background = Color.BLACK;
        Color foreground = Color.ORANGE;
        Font font = new Font("Arial", Font.PLAIN, 14);
        
        this.setForeground(foreground);
        this.setBackground(background);
        this.setFont(font);
        
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0, 0};
        gridBagLayout.rowHeights = new int[]{0, 0, 0};
        gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
        setLayout(gridBagLayout);
        
        timeLabel = new JLabel("> 0:00");
        timeLabel.setForeground(foreground);
        timeLabel.setBackground(background);
        timeLabel.setFont(font);
        timeLabel.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gbc_timeLabel = new GridBagConstraints();
        gbc_timeLabel.fill = GridBagConstraints.HORIZONTAL;
        gbc_timeLabel.insets = new Insets(0, 0, 5, 0);
        gbc_timeLabel.gridx = 0;
        gbc_timeLabel.gridy = 0;
        add(timeLabel, gbc_timeLabel);
        
        trackNameLabel = new JLabel("Track name");
        trackNameLabel.setForeground(foreground);
        trackNameLabel.setBackground(background);
        trackNameLabel.setFont(font);
        trackNameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gbc_trackNameLabel = new GridBagConstraints();
        gbc_trackNameLabel.fill = GridBagConstraints.HORIZONTAL;
        gbc_trackNameLabel.gridx = 0;
        gbc_trackNameLabel.gridy = 1;
        add(trackNameLabel, gbc_trackNameLabel);

    }

    public void setTrackName(String trackName)
    {
        trackNameLabel.setText(trackName);
    }

    public void setScheme(Scheme scheme) {
        Color foreground = scheme.getForeground();
        Color background = scheme.getBackground();
        
        this.setForeground(foreground);
        this.setBackground(background);
        
        timeLabel.setForeground(foreground);
        timeLabel.setBackground(background);
        
        trackNameLabel.setForeground(foreground);
        trackNameLabel.setBackground(background);
    }
}
