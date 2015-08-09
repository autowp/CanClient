package com.autowp.dashboard;

import com.autowp.can.CanClient;
import com.autowp.can.CanMessage;

import javax.swing.JTable;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class DashboardDialog extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTable table;

    /**
     * Create the dialog.
     */
    public DashboardDialog(CanClient client) {
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setTitle("Dashboard");
        setBounds(100, 100, 450, 691);
        
        JScrollPane scrollPane = new JScrollPane();
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        
        table = new DashboardTable();
        scrollPane.setViewportView(table);
        
        JButton btnClear = new JButton("Clear");
        getContentPane().add(btnClear, BorderLayout.NORTH);
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                DefaultTableModel dtm = (DefaultTableModel) table.getModel();
                dtm.setRowCount(0);
            }
        });
        
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
