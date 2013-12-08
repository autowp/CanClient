package com.autowp.peugeot.display;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
public class VolumePanel extends JPanel {

    private JLabel lblTitle;
    private JLabel lblVolume;
    private VolumeGridPanel pnlVolumeGrid;
    
    /**
     * Create the panel.
     */
    public VolumePanel() {
        setBorder(new LineBorder(new Color(0, 0, 0)));
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWeights = new double[]{19.0, 1.0};
        gridBagLayout.rowWeights = new double[]{1.0, 10.0};
        setLayout(gridBagLayout);
        
        lblTitle = new JLabel("Volume");
        GridBagConstraints gbc_lblTitle = new GridBagConstraints();
        gbc_lblTitle.gridx = 0;
        gbc_lblTitle.gridy = 0;
        add(lblTitle, gbc_lblTitle);
        
        lblVolume = new JLabel("0");
        GridBagConstraints gbc_lblVolume = new GridBagConstraints();
        gbc_lblVolume.anchor = GridBagConstraints.WEST;
        gbc_lblVolume.gridx = 1;
        gbc_lblVolume.gridy = 0;
        add(lblVolume, gbc_lblVolume);
        
        pnlVolumeGrid = new VolumeGridPanel();
        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.gridwidth = 2;
        gbc_panel.fill = GridBagConstraints.BOTH;
        gbc_panel.gridx = 0;
        gbc_panel.gridy = 1;
        add(pnlVolumeGrid, gbc_panel);

    }
    
    public VolumeGridPanel getVolumeGridPanel()
    {
        return pnlVolumeGrid;
    }

    public void setScheme(Scheme scheme) {
        Color foreground = scheme.getForeground();
        Color background = scheme.getBackground();
        
        this.setForeground(foreground);
        this.setBackground(background);
        
        lblTitle.setForeground(foreground);
        lblTitle.setBackground(background);
        
        lblVolume.setForeground(foreground);
        lblVolume.setBackground(background);
        
        pnlVolumeGrid.setScheme(scheme);
    }
}
