package com.autowp.peugeot.display;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import net.miginfocom.swing.MigLayout;
import java.awt.GridLayout;

@SuppressWarnings("serial")
public class DisplayAudioPanel extends JPanel {
    
    protected JLabel iconTitleLabel;
    protected JPanel iconPanel;
    protected JLabel titleLabel;
    protected DisplayAudioCDPanel contentPanel;
    protected JPanel bottomPanel;
    protected JLabel rdsLabel;
    protected JLabel taLabel;
    protected JLabel ptyLabel;
    protected JLabel regLabel;
    protected JLabel loudLabel;

    /**
     * Create the panel.
     */
    public DisplayAudioPanel() {
        Color background = Color.BLACK;
        Color foreground = Color.ORANGE;
        Font font = new Font("Arial", Font.PLAIN, 14);
        
        this.setForeground(foreground);
        this.setBackground(background);
        this.setFont(font);
        setLayout(new MigLayout("", "[70px,grow 70][300px,grow 300]", "[40px,grow 40][100px,grow][40px,grow 40]"));
        
        iconTitleLabel = new JLabel("CD");
        iconTitleLabel.setForeground(foreground);
        iconTitleLabel.setBackground(background);
        iconTitleLabel.setFont(font);
        add(iconTitleLabel, "cell 0 0,alignx center,aligny center");
        
        iconPanel = new JPanel();
        iconPanel.setForeground(foreground);
        iconPanel.setBackground(background);
        iconPanel.setFont(font);
        add(iconPanel, "cell 0 1 1 2,grow");
        
        titleLabel = new JLabel("Title");
        titleLabel.setForeground(foreground);
        titleLabel.setBackground(background);
        titleLabel.setFont(font);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        add(titleLabel, "cell 1 0,grow");
        
        contentPanel = new DisplayAudioCDPanel();
        contentPanel.setForeground(foreground);
        contentPanel.setBackground(background);
        contentPanel.setFont(font);
        add(contentPanel, "cell 1 1,grow");
        
        bottomPanel = new JPanel();
        bottomPanel.setForeground(foreground);
        bottomPanel.setBackground(background);
        bottomPanel.setFont(font);
        add(bottomPanel, "cell 1 2,grow");
        bottomPanel.setLayout(new GridLayout(0, 5, 0, 0));
        
        rdsLabel = new JLabel("RDS");
        rdsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rdsLabel.setForeground(foreground);
        rdsLabel.setBackground(background);
        rdsLabel.setFont(font);
        bottomPanel.add(rdsLabel);
        
        taLabel = new JLabel("TA");
        taLabel.setHorizontalAlignment(SwingConstants.CENTER);
        taLabel.setForeground(foreground);
        taLabel.setBackground(background);
        taLabel.setFont(font);
        bottomPanel.add(taLabel);
        
        ptyLabel = new JLabel("PTY");
        ptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ptyLabel.setForeground(foreground);
        ptyLabel.setBackground(background);
        ptyLabel.setFont(font);
        bottomPanel.add(ptyLabel);
        
        regLabel = new JLabel("REG");
        regLabel.setHorizontalAlignment(SwingConstants.CENTER);
        regLabel.setForeground(foreground);
        regLabel.setBackground(background);
        regLabel.setFont(font);
        bottomPanel.add(regLabel);
        
        loudLabel = new JLabel("LOUD");
        loudLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loudLabel.setForeground(foreground);
        loudLabel.setBackground(background);
        loudLabel.setFont(font);
        bottomPanel.add(loudLabel);

    }

    public void setScheme(Scheme scheme) {
        Color foreground = scheme.getForeground();
        Color background = scheme.getBackground();
        
        this.setForeground(foreground);
        this.setBackground(background);
        
        iconTitleLabel.setForeground(foreground);
        iconTitleLabel.setBackground(background);
        
        iconPanel.setForeground(foreground);
        iconPanel.setBackground(background);
        
        titleLabel.setForeground(foreground);
        titleLabel.setBackground(background);
        
        contentPanel.setScheme(scheme);
        
        bottomPanel.setForeground(foreground);
        bottomPanel.setBackground(background);
        
        rdsLabel.setForeground(foreground);
        rdsLabel.setBackground(background);
        
        taLabel.setForeground(foreground);
        taLabel.setBackground(background);
        
        ptyLabel.setForeground(foreground);
        ptyLabel.setBackground(background);
        
        regLabel.setForeground(foreground);
        regLabel.setBackground(background);
        
        loudLabel.setForeground(foreground);
        loudLabel.setBackground(background);
    }

    public DisplayAudioCDPanel getCDPanel() {
        return contentPanel;
    }

}
