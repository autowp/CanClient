package com.autowp.peugeot.display;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

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
        
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {0, 0};
        gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
        gridBagLayout.columnWeights = new double[]{1.0, 4.0};
        gridBagLayout.rowWeights = new double[]{2.0, 5.0, 1.0, Double.MIN_VALUE};
        setLayout(gridBagLayout);
        
        iconTitleLabel = new JLabel("CD");
        iconTitleLabel.setForeground(foreground);
        iconTitleLabel.setBackground(background);
        iconTitleLabel.setFont(font);
        GridBagConstraints gbc_iconTitleLabel = new GridBagConstraints();
        gbc_iconTitleLabel.insets = new Insets(0, 0, 5, 5);
        gbc_iconTitleLabel.gridx = 0;
        gbc_iconTitleLabel.gridy = 0;
        add(iconTitleLabel, gbc_iconTitleLabel);
        
        iconPanel = new JPanel();
        iconPanel.setForeground(foreground);
        iconPanel.setBackground(background);
        iconPanel.setFont(font);
        GridBagConstraints gbc_iconPanel = new GridBagConstraints();
        gbc_iconPanel.gridheight = 2;
        gbc_iconPanel.insets = new Insets(0, 0, 0, 5);
        gbc_iconPanel.fill = GridBagConstraints.BOTH;
        gbc_iconPanel.gridx = 0;
        gbc_iconPanel.gridy = 1;
        add(iconPanel, gbc_iconPanel);
        
        titleLabel = new JLabel("Title");
        titleLabel.setForeground(foreground);
        titleLabel.setBackground(background);
        titleLabel.setFont(font);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gbc_titleLabel = new GridBagConstraints();
        gbc_titleLabel.fill = GridBagConstraints.BOTH;
        gbc_titleLabel.insets = new Insets(0, 0, 5, 0);
        gbc_titleLabel.gridx = 1;
        gbc_titleLabel.gridy = 0;
        add(titleLabel, gbc_titleLabel);
        
        contentPanel = new DisplayAudioCDPanel();
        contentPanel.setForeground(foreground);
        contentPanel.setBackground(background);
        contentPanel.setFont(font);
        GridBagConstraints gbc_contentPanel = new GridBagConstraints();
        gbc_contentPanel.insets = new Insets(0, 0, 5, 0);
        gbc_contentPanel.fill = GridBagConstraints.BOTH;
        gbc_contentPanel.gridx = 1;
        gbc_contentPanel.gridy = 1;
        add(contentPanel, gbc_contentPanel);
        
        bottomPanel = new JPanel();
        bottomPanel.setForeground(foreground);
        bottomPanel.setBackground(background);
        bottomPanel.setFont(font);
        GridBagConstraints gbc_bottomPanel = new GridBagConstraints();
        gbc_bottomPanel.fill = GridBagConstraints.BOTH;
        gbc_bottomPanel.gridx = 1;
        gbc_bottomPanel.gridy = 2;
        add(bottomPanel, gbc_bottomPanel);
        
        rdsLabel = new JLabel("RDS");
        rdsLabel.setForeground(foreground);
        rdsLabel.setBackground(background);
        rdsLabel.setFont(font);
        bottomPanel.add(rdsLabel);
        
        taLabel = new JLabel("TA");
        taLabel.setForeground(foreground);
        taLabel.setBackground(background);
        taLabel.setFont(font);
        bottomPanel.add(taLabel);
        
        ptyLabel = new JLabel("PTY");
        ptyLabel.setForeground(foreground);
        ptyLabel.setBackground(background);
        ptyLabel.setFont(font);
        bottomPanel.add(ptyLabel);
        
        regLabel = new JLabel("REG");
        regLabel.setForeground(foreground);
        regLabel.setBackground(background);
        regLabel.setFont(font);
        bottomPanel.add(regLabel);
        
        loudLabel = new JLabel("LOUD");
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
