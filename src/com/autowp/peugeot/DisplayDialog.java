package com.autowp.peugeot;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.autowp.canclient.CanClient;
import com.autowp.peugeot.display.Display;
import com.autowp.peugeot.display.TrackList;

@SuppressWarnings("serial")
public class DisplayDialog extends JDialog {

    private final JPanel contentPanel = new JPanel();
    
    private Display display;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            DisplayDialog dialog = new DisplayDialog(null);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
            
            // loading example data
            TrackList trackList = dialog.getDisplay().getTrackList();
            trackList.setVisible(true);
            
            trackList.setTracksCount(12);
            trackList.setTrack(0, "t.A.T.u.", "All the things she said (edited)");
            trackList.setTrack(1, "Rammstein", "Alter mann (special version)");
            trackList.setTrack(2, "Eminem", "Bonnie & Clyde");
            trackList.setTrack(3, "Zaz", "Ces petits riens");
            trackList.setTrack(4, "t.A.T.u.", "All the things she said (edited)");
            trackList.setTrack(5, "Rammstein", "Alter mann (special version)");
            trackList.setTrack(6, "Eminem", "Bonnie & Clyde");
            trackList.setTrack(7, "Zaz", "Ces petits riens");
            trackList.setTrack(8, "t.A.T.u.", "All the things she said (edited)");
            trackList.setTrack(9, "Rammstein", "Alter mann (special version)");
            trackList.setTrack(10, "Eminem", "Bonnie & Clyde");
            trackList.setTrack(11, "Zaz", "Ces petits riens");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     * @param client 
     */
    public DisplayDialog(CanClient client) {
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
        
        display = new Display(client);
        
        upButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    display.getTrackList().prev();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        JButton downButton = new JButton("Down");
        buttonPane.add(downButton);
        
        downButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    display.getTrackList().next();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        
        contentPanel.add(display, BorderLayout.CENTER);
        display.setVisible(true);
    }

    public Display getDisplay()
    {
        return display;
    }
}
