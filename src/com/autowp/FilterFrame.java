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

import com.autowp.canclient.CanClient;
import com.autowp.canclient.CanFrame;
import com.autowp.canclient.FrameReceivedEvent;
import com.autowp.canclient.FrameReceivedEventClassListener;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.ListSelectionModel;


@SuppressWarnings("serial")
public class FilterFrame extends JFrame {

    private JPanel contentPane;
    private CanTable table;
    private CanFilter filter = new CanFilter();
    
    private FrameReceivedEventClassListener listener = new FrameReceivedEventClassListener() {
        public void handleFrameReceivedEvent(FrameReceivedEvent e) {
            if (filter != null) {
                CanFrame frame = e.getFrame();
                if (filter.match(frame)) {
                    table.addCanFrame(frame);
                }
            }
        }
    };
    private JTextField textField;
    private JButton applyFilterButton;
    private JPanel panel;
    private JButton loadButton;
    private JButton saveButton;
    private JButton clearButton;

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
                int id = Integer.parseInt(textField.getText(), 16);
                filter.clear().setMode(CanFilter.Mode.MATCH).add(id);
                clearRows();
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
        
        client.addEventListener(listener);
        
        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                client.removeEventListener(listener);
            }
        });
    }

    protected void clearRows()
    {
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        dtm.setRowCount(0);
    }
}
