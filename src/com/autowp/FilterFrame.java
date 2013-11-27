package com.autowp;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.autowp.canclient.CanClient;
import com.autowp.canclient.CanFrame;
import com.autowp.canclient.FrameReceivedEvent;
import com.autowp.canclient.FrameReceivedEventClassListener;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


@SuppressWarnings("serial")
public class FilterFrame extends JFrame {

    private JPanel contentPane;
    private CanTable table;
    private int id;
    
    private FrameReceivedEventClassListener listener = new FrameReceivedEventClassListener() {
        public void handleFrameReceivedEvent(FrameReceivedEvent e) {
            CanFrame frame = e.getFrame();
            if (frame.getId() == id) {
                table.addCanFrame(frame);
            }
        }
    };
    private JTextField textField;
    private JButton applyFilterButton;
    private JPanel panel;

    /**
     * Create the frame.
     */
    public FilterFrame(final CanClient client) {
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        
        table = new CanTable();
        table.setEnabled(true);
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        contentPane.add(scrollPane, BorderLayout.CENTER);
        
        panel = new JPanel();
        contentPane.add(panel, BorderLayout.NORTH);
        
        textField = new JTextField();
        panel.add(textField);
        textField.setColumns(10);
        
        applyFilterButton = new JButton("Apply");
        applyFilterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                id = Integer.parseInt(textField.getText(), 16);
                DefaultTableModel dtm = (DefaultTableModel) table.getModel();
                dtm.setRowCount(0);
            }
        });
        panel.add(applyFilterButton);
        
        client.addEventListener(listener);
        
        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                client.removeEventListener(listener);
            }
        });
    }

}
