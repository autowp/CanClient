package com.autowp.peugeot.display;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.BorderLayout;

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
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {0, 0, 0};
        gridBagLayout.rowHeights = new int[] {0, 0};
        gridBagLayout.columnWeights = new double[]{2.0, 1.0, 8.0};
        gridBagLayout.rowWeights = new double[]{1.0, 5.0};
        setLayout(gridBagLayout);
        
        upperPanel = new JPanel();
        upperPanel.setForeground(foreground);
        upperPanel.setBackground(background);
        GridBagConstraints gbc_upperPanel = new GridBagConstraints();
        gbc_upperPanel.gridwidth = 2;
        gbc_upperPanel.insets = new Insets(0, 0, 5, 0);
        gbc_upperPanel.fill = GridBagConstraints.BOTH;
        gbc_upperPanel.gridx = 1;
        gbc_upperPanel.gridy = 0;
        add(upperPanel, gbc_upperPanel);
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
        GridBagConstraints gbc_sidePanel = new GridBagConstraints();
        gbc_sidePanel.anchor = GridBagConstraints.WEST;
        gbc_sidePanel.gridwidth = 2;
        gbc_sidePanel.insets = new Insets(0, 0, 0, 5);
        gbc_sidePanel.fill = GridBagConstraints.VERTICAL;
        gbc_sidePanel.gridx = 0;
        gbc_sidePanel.gridy = 1;
        add(sidePanel, gbc_sidePanel);
        
        mainPanel = new DisplayAudioPanel();
        mainPanel.setForeground(foreground);
        mainPanel.setBackground(background);
        GridBagConstraints gbc_mainPanel = new GridBagConstraints();
        gbc_mainPanel.fill = GridBagConstraints.BOTH;
        gbc_mainPanel.gridx = 2;
        gbc_mainPanel.gridy = 1;
        add(mainPanel, gbc_mainPanel);
        
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
