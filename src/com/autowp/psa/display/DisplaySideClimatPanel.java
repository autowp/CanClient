package com.autowp.psa.display;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;

@SuppressWarnings("serial")
public class DisplaySideClimatPanel extends JPanel {

    private JPanel panel;
    private JLabel leftTemperatureLabel;
    private JLabel rightTemperatureLabel;
    
    /**
     * Create the panel.
     */
    public DisplaySideClimatPanel() {
        Color background = Color.BLACK;
        Color foreground = Color.ORANGE;
        Font font = new Font("Arial", Font.PLAIN, 14);
        
        this.setForeground(foreground);
        this.setBackground(background);
        this.setFont(font);
        
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0, 0, 0};
        gridBagLayout.rowHeights = new int[]{0, 0, 0};
        gridBagLayout.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{3.0, 1.0, Double.MIN_VALUE};
        setLayout(gridBagLayout);
        
        panel = new JPanel();
        panel.setForeground(foreground);
        panel.setBackground(background);
        panel.setFont(font);
        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.gridwidth = 2;
        gbc_panel.insets = new Insets(0, 0, 5, 0);
        gbc_panel.fill = GridBagConstraints.BOTH;
        gbc_panel.gridx = 0;
        gbc_panel.gridy = 0;
        add(panel, gbc_panel);
        
        leftTemperatureLabel = new JLabel("0.0");
        leftTemperatureLabel.setForeground(foreground);
        leftTemperatureLabel.setBackground(background);
        leftTemperatureLabel.setFont(font);
        GridBagConstraints gbc_leftTemperatureLabel = new GridBagConstraints();
        gbc_leftTemperatureLabel.insets = new Insets(0, 0, 0, 5);
        gbc_leftTemperatureLabel.gridx = 0;
        gbc_leftTemperatureLabel.gridy = 1;
        add(leftTemperatureLabel, gbc_leftTemperatureLabel);
        
        rightTemperatureLabel = new JLabel("0.0");
        rightTemperatureLabel.setForeground(foreground);
        rightTemperatureLabel.setBackground(background);
        rightTemperatureLabel.setFont(font);
        GridBagConstraints gbc_rightTemperatureLabel = new GridBagConstraints();
        gbc_rightTemperatureLabel.gridx = 1;
        gbc_rightTemperatureLabel.gridy = 1;
        add(rightTemperatureLabel, gbc_rightTemperatureLabel);

    }

    public void setScheme(Scheme scheme) {
        Color foreground = scheme.getForeground();
        Color background = scheme.getBackground();
        
        panel.setForeground(foreground);
        panel.setBackground(background);
        
        leftTemperatureLabel.setForeground(foreground);
        leftTemperatureLabel.setBackground(background);
        
        rightTemperatureLabel.setForeground(foreground);
        rightTemperatureLabel.setBackground(background);
    }

}
