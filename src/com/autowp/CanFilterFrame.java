package com.autowp;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;


import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.ListSelectionModel;

import com.autowp.can.CanClient;
import com.autowp.can.CanFilter;
import com.autowp.can.CanFrame;
import com.autowp.can.CanFrameEvent;
import com.autowp.can.CanFrameEventClassListener;
import com.autowp.can.CanMessage;
import com.autowp.can.CanMessageEvent;
import com.autowp.can.CanMessageEventClassListener;


@SuppressWarnings("serial")
public class CanFilterFrame extends JFrame {

    private JPanel contentPane;
    private CanFrameTable table;
    private CanFilter filter = new CanFilter();
    
    private CanFrameEventClassListener frameListener;
    
    private CanMessageEventClassListener messageListener;
    
    private JTextField textField;
    private JButton applyFilterButton;
    private JPanel panel;
    private JButton loadButton;
    private JButton saveButton;
    private JButton clearButton;

    /**
     * Create the frame.
     */
    public CanFilterFrame(final CanClient client, boolean listenMessageInstedOfFrames) {
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        
        table = new CanFrameTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        table.setEnabled(true);
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        contentPane.add(scrollPane, BorderLayout.CENTER);
        
        panel = new JPanel();
        contentPane.add(panel, BorderLayout.NORTH);
        
        loadButton = new JButton("Load ...");
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser openFile = new JFileChooser();
                FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Filter config", "txt");
                openFile.setFileFilter(fileFilter);
                int ret = openFile.showOpenDialog(null);
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = openFile.getSelectedFile();
                    try {
                        FileInputStream in = new FileInputStream(file.getAbsolutePath());
                        filter.readFromStream(in);
                        in.close();
                        
                        clearRows();
                        
                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    
                }
            }
        });
        panel.add(loadButton);
        
        saveButton = new JButton("Save ...");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser openFile = new JFileChooser();
                FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Filter config", "txt");
                openFile.setFileFilter(fileFilter);
                int ret = openFile.showSaveDialog(null);
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = openFile.getSelectedFile();
                    try {
                        FileOutputStream out = new FileOutputStream(file.getAbsolutePath());
                        filter.writeToStream(out);
                        out.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    
                }
            }
        });
        panel.add(saveButton);
        
        textField = new JTextField();
        panel.add(textField);
        textField.setColumns(10);
        
        applyFilterButton = new JButton("Apply");
        applyFilterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    int id = Integer.parseInt(textField.getText(), 16);
                    filter.clear().setMode(CanFilter.Mode.MATCH).add(id);
                    clearRows();
                } catch (NumberFormatException e) {
                    e.printStackTrace(System.err);
                }
            }
        });
        panel.add(applyFilterButton);
        
        clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearRows();
            }
        });
        panel.add(clearButton);
        
        if (listenMessageInstedOfFrames) {
            client.addEventListener(getMessageListener());
            
            this.addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent e){
                    client.removeEventListener(getMessageListener());
                }
            });
        } else {
            client.addEventListener(getFrameListener());
            
            this.addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent e){
                    client.removeEventListener(getFrameListener());
                }
            });
        }
    }

    protected void clearRows()
    {
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        dtm.setRowCount(0);
    }
    
    protected CanFrameEventClassListener getFrameListener()
    {
        if (frameListener == null) {
            frameListener = new CanFrameEventClassListener() {
                @Override
                public void handleCanFrameReceivedEvent(CanFrameEvent e) {
                    if (filter != null) {
                        CanFrame frame = e.getFrame();
                        if (filter.match(frame)) {
                            table.addCanFrame(frame);
                        }
                    }
                }
    
                @Override
                public void handleCanFrameSentEvent(CanFrameEvent e) {
                    // TODO Auto-generated method stub
                    
                }
            };
        }
        
        return frameListener;
    }
    
    protected CanMessageEventClassListener getMessageListener()
    {
        if (messageListener == null) {
            messageListener = new CanMessageEventClassListener() {
    
                @Override
                public void handleCanMessageReceivedEvent(CanMessageEvent e) {
                    if (filter != null) {
                        CanMessage message = e.getMessage();
                        if (filter.match(message)) {
                            table.addCanMessage(message);
                        }
                    }
                }
    
                @Override
                public void handleCanMessageSentEvent(CanMessageEvent e) {
                    // TODO Auto-generated method stub
                    
                }
            };
        }
        return messageListener;
    }
}
