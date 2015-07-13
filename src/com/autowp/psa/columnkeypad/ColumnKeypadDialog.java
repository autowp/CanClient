package com.autowp.psa.columnkeypad;

import java.awt.EventQueue;

import javax.swing.JDialog;

import java.awt.GridLayout;

import javax.swing.JButton;

import com.autowp.can.CanClient;
import com.autowp.can.CanClientException;
import com.autowp.can.CanFrame;
import com.autowp.can.CanFrameException;
import com.autowp.psa.message.ColumnKeypadMessage;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JToggleButton;

public class ColumnKeypadDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    
    private CanClient mClient;

    private ColumnKeypadMessage mMessage = new ColumnKeypadMessage();

    private JToggleButton tglbtnReceive;

    /**
     * Create the dialog.
     */
    public ColumnKeypadDialog(CanClient client) {
        mClient = client;
        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(new BorderLayout(0, 0));
        
        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(new GridLayout(0, 4, 0, 0));
        
        JButton btnForward = new JButton("Forward");
        panel.add(btnForward);
        
        JButton btnBackward = new JButton("Backward");
        panel.add(btnBackward);
        
        JButton btnVolumeup = new JButton("VolumeUp");
        panel.add(btnVolumeup);
        
        JButton btnVolumedown = new JButton("VolumeDown");
        panel.add(btnVolumedown);
        
        JButton btnSource = new JButton("Source");
        panel.add(btnSource);
        
        JButton btnScrollup = new JButton("ScrollUp");
        panel.add(btnScrollup);
        
        JButton btnScrolldown = new JButton("ScrollDown");
        panel.add(btnScrolldown);
        
        JPanel panel_1 = new JPanel();
        getContentPane().add(panel_1, BorderLayout.NORTH);
        
        tglbtnReceive = new JToggleButton("Send & Receive");
        panel_1.add(tglbtnReceive);
        btnScrolldown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mMessage.setScroll((byte) (mMessage.getScroll() - 1));
                sendState();
            }
        });
        btnScrollup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mMessage.setScroll((byte) (mMessage.getScroll() + 1));
                sendState();
            }
        });
        btnSource.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent arg0) {
                mMessage.setSource(true);
                sendState();
            }
            @Override
            public void mouseReleased(MouseEvent arg0) {
                mMessage.setSource(false);
                sendState();
            }
        });
        btnVolumedown.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent arg0) {
                mMessage.setVolumeDown(true);
                sendState();
            }
            @Override
            public void mouseReleased(MouseEvent arg0) {
                mMessage.setVolumeDown(false);
                sendState();
            }
        });
        btnVolumeup.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent arg0) {
                mMessage.setVolumeUp(true);
                sendState();
            }
            @Override
            public void mouseReleased(MouseEvent arg0) {
                mMessage.setVolumeUp(false);
                sendState();
            }
        });
        btnBackward.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent arg0) {
                mMessage.setBackward(true);
                sendState();
            }
            @Override
            public void mouseReleased(MouseEvent arg0) {
                mMessage.setBackward(false);
                sendState();
            }
        });
        btnForward.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent arg0) {
                mMessage.setForward(true);
                sendState();
            }
            @Override
            public void mouseReleased(MouseEvent arg0) {
                mMessage.setForward(false);
                sendState();
            }
        });

    }

    private void sendState()
    {
        try {
            
            CanFrame frame = mMessage.assembleFrame();
            
            mClient.send(frame);
            
            if (tglbtnReceive.isSelected()) {
                mClient.receive(frame);
            }
           
        } catch (CanClientException | CanFrameException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
