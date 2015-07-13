package com.autowp.psa.radiokeypad;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import com.autowp.can.CanClient;
import com.autowp.can.CanClientException;
import com.autowp.can.CanFrame;
import com.autowp.can.CanFrameException;
import com.autowp.psa.message.RadioKeypadMessage;

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RadioKeypadDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    
    private CanClient mClient;
    
    private RadioKeypadMessage mMessage = new RadioKeypadMessage();

    private JToggleButton tglbtnReceive;

    /**
     * Create the dialog.
     */
    public RadioKeypadDialog(CanClient client) {
        mClient = client;
        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(new BorderLayout());
        {
            JPanel panel = new JPanel();
            getContentPane().add(panel, BorderLayout.NORTH);
            {
                tglbtnReceive = new JToggleButton("Send & receive");
                panel.add(tglbtnReceive);
            }
        }
        {
            JPanel panel = new JPanel();
            getContentPane().add(panel, BorderLayout.CENTER);
            panel.setLayout(new GridLayout(0, 3, 0, 0));
            
            JButton btnEsc = new JButton("Esc");
            panel.add(btnEsc);
            btnEsc.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent arg0) {
                    mMessage.setESC(true);
                    sendState();
                }
                @Override
                public void mouseReleased(MouseEvent arg0) {
                    mMessage.setESC(false);
                    sendState();
                }
            });
            
            JButton btnUp = new JButton("Up");
            panel.add(btnUp);
            btnUp.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent arg0) {
                    mMessage.setUp(true);
                    sendState();
                }
                @Override
                public void mouseReleased(MouseEvent arg0) {
                    mMessage.setUp(false);
                    sendState();
                }
            });
            
            JButton btnMenu = new JButton("Menu");
            panel.add(btnMenu);
            btnMenu.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent arg0) {
                    mMessage.setMenu(true);
                    sendState();
                }
                @Override
                public void mouseReleased(MouseEvent arg0) {
                    mMessage.setMenu(false);
                    sendState();
                }
            });

            JButton btnLeft = new JButton("Left");
            panel.add(btnLeft);
            btnLeft.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent arg0) {
                    mMessage.setLeft(true);
                    sendState();
                }
                @Override
                public void mouseReleased(MouseEvent arg0) {
                    mMessage.setLeft(false);
                    sendState();
                }
            });

            JButton btnOk = new JButton("Ok");
            panel.add(btnOk);
            btnOk.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent arg0) {
                    mMessage.setOk(true);
                    sendState();
                }
                @Override
                public void mouseReleased(MouseEvent arg0) {
                    mMessage.setOk(false);
                    sendState();
                }
            });

            JButton btnRight = new JButton("Right");
            panel.add(btnRight);
            btnRight.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent arg0) {
                    mMessage.setRight(true);
                    sendState();
                }
                @Override
                public void mouseReleased(MouseEvent arg0) {
                    mMessage.setRight(false);
                    sendState();
                }
            });

            JButton btnReserved = new JButton("Reserved");
            btnReserved.setEnabled(false);
            panel.add(btnReserved);

            JButton btnDown = new JButton("Down");
            panel.add(btnDown);
            btnDown.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent arg0) {
                    mMessage.setDown(true);
                    sendState();
                }
                @Override
                public void mouseReleased(MouseEvent arg0) {
                    mMessage.setDown(false);
                    sendState();
                }
            });
            
            JButton btnReserved_1 = new JButton("Reserved");
            btnReserved_1.setEnabled(false);
            panel.add(btnReserved_1);
            
            JButton btnClim = new JButton("Clim");
            panel.add(btnClim);
            btnClim.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent arg0) {
                    mMessage.setClim(true);
                    sendState();
                }
                @Override
                public void mouseReleased(MouseEvent arg0) {
                    mMessage.setClim(false);
                    sendState();
                }
            });
            
            JButton btnAudio = new JButton("Audio");
            panel.add(btnAudio);
            btnAudio.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent arg0) {
                    mMessage.setAudio(true);
                    sendState();
                }
                @Override
                public void mouseReleased(MouseEvent arg0) {
                    mMessage.setAudio(false);
                    sendState();
                }
            });
            
            JButton btnTrip = new JButton("Trip");
            panel.add(btnTrip);
            btnTrip.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent arg0) {
                    mMessage.setTrip(true);
                    sendState();
                }
                @Override
                public void mouseReleased(MouseEvent arg0) {
                    mMessage.setTrip(false);
                    sendState();
                }
            });
            
            JButton btnDark = new JButton("Dark");
            panel.add(btnDark);
            btnDark.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent arg0) {
                    mMessage.setDark(true);
                    sendState();
                }
                @Override
                public void mouseReleased(MouseEvent arg0) {
                    mMessage.setDark(false);
                    sendState();
                }
            });
            

        }
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
