package com.autowp.dashboard;

import java.awt.EventQueue;

import javax.swing.JDialog;

import com.autowp.can.CanClient;
import com.autowp.can.CanMessage;

import javax.swing.JTable;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;

public class DashboardDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private JTable table;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    DashboardDialog dialog = new DashboardDialog(null);
                    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    dialog.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the dialog.
     */
    public DashboardDialog(CanClient client) {
        setTitle("Dashboard");
        setBounds(100, 100, 450, 300);
        
        JScrollPane scrollPane = new JScrollPane();
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        
        table = new DashboardTable();
        scrollPane.setViewportView(table);
        
        if (client != null) {
            client.addEventListener(new CanClient.OnCanMessageTransferListener() {
                @Override
                public void handleCanMessageReceivedEvent(CanMessage message) {
                    ((DashboardTable) table).addCanMessage(message);
                }

                @Override
                public void handleCanMessageSentEvent(CanMessage message) {
                    // TODO Auto-generated method stub
                    
                }
            });
        }

    }

}
