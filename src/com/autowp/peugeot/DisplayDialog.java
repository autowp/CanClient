package com.autowp.peugeot;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class DisplayDialog extends JDialog {

    private final JPanel contentPanel = new JPanel();
    
    private Display display = new Display();

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            DisplayDialog dialog = new DisplayDialog();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     */
    public DisplayDialog() {
        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        JButton upButton = new JButton("Up");
        buttonPane.add(upButton);
        
        upButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                display.getTrackList().prev();
                display.repaint();
            }
        });

        JButton downButton = new JButton("Down");
        buttonPane.add(downButton);
        
        downButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                display.getTrackList().next();
                display.repaint();
            }
        });
        
        contentPanel.add(display, BorderLayout.CENTER);
        display.setVisible(true);
    }


}
