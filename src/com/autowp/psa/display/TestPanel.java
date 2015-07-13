package com.autowp.psa.display;

import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.border.BevelBorder;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import java.awt.Color;

@SuppressWarnings("serial")
public class TestPanel extends JPanel {

    /**
     * Create the panel.
     */
    public TestPanel() {
        setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        setLayout(new MigLayout("", "[100px,grow][200px,grow 200]", "[92px,grow][92px,grow][92px,grow]"));
        
        JPanel panel_3 = new JPanel();
        add(panel_3, "cell 0 0 2 1,grow");
        
        JLabel lblNewLabel_4 = new JLabel("New label");
        panel_3.add(lblNewLabel_4);
        
        JPanel panel_2 = new JPanel();
        panel_2.setBackground(Color.BLACK);
        add(panel_2, "cell 0 1,grow");
        
        JLabel lblNewLabel_2 = new JLabel("New label");
        panel_2.add(lblNewLabel_2);
        
        JPanel panel = new JPanel();
        panel.setBackground(Color.YELLOW);
        FlowLayout flowLayout = (FlowLayout) panel.getLayout();
        add(panel, "cell 1 1,grow");
        
        JLabel lblNewLabel_3 = new JLabel("New label");
        panel.add(lblNewLabel_3);
        
        JPanel panel_5 = new JPanel();
        add(panel_5, "cell 0 2,grow");
        
        JLabel lblNewLabel = new JLabel("New label");
        panel_5.add(lblNewLabel);
        
        JPanel panel_1 = new JPanel();
        panel_1.setBackground(Color.RED);
        add(panel_1, "cell 1 2,grow");
        
        JLabel lblNewLabel_1 = new JLabel("New label");
        panel_1.add(lblNewLabel_1);

    }

}
