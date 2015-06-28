package com.autowp.sender;

import java.awt.EventQueue;

import javax.swing.JDialog;

import com.autowp.can.CanClient;
import com.autowp.can.CanClientException;
import com.autowp.can.CanFrame;
import com.autowp.can.CanFrameException;

import javax.swing.JTextField;

import java.awt.FlowLayout;

import javax.swing.JButton;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SenderDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    SenderDialog dialog = new SenderDialog(null);
                    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    dialog.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private JTextField tfId;
    private JTextField tfData;

    /**
     * Create the dialog.
     */
    public SenderDialog(final CanClient client) {
        super(null, java.awt.Dialog.ModalityType.TOOLKIT_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Sender");
        setBounds(100, 100, 450, 100);
        getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        
        tfId = new JTextField();
        tfId.setToolTipText("Hex ID");
        getContentPane().add(tfId);
        tfId.setColumns(3);
        
        tfData = new JTextField();
        tfData.setToolTipText("Hex data");
        getContentPane().add(tfData);
        tfData.setColumns(16);
        
        JButton btnSend = new JButton("Send");
        btnSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    char[] idChars = ("0" + tfId.getText()).toCharArray();
                    byte[] idBytes = Hex.decodeHex(idChars);
                    int id = 0;
                    for (int i=0; i<idBytes.length; i++) {
                        int multiplier = (idBytes.length - i - 1) * 8;
                        int cValue = (int)idBytes[i] & 0xFF;
                        id += (cValue << multiplier);
                    }
                    
                    System.out.println(id);
                    
                    char[] dataChars = tfData.getText().toCharArray();
                    byte[] data = Hex.decodeHex(dataChars);
                    
                    CanFrame frame = new CanFrame(id, data);
                    
                    if (client != null) {
                        client.send(frame);
                    }
                    
                } catch (DecoderException | CanFrameException | CanClientException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                
            }
        });
        getContentPane().add(btnSend);
    }

}
