package com.autowp.psa.bsi;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JToggleButton;

import com.autowp.can.CanFrameException;
import com.autowp.psa.bsi.BSI;
import com.autowp.psa.message.BSIInfoWindowMessage;
import com.autowp.psa.message.MessageException;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JComboBox;

import java.awt.FlowLayout;

import javax.swing.JCheckBox;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.SwingConstants;

import java.awt.Font;

public class BSIDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private BSI mBSI;
    private JTextField textField;
    private JToggleButton tglbtnStatus;
    private JToggleButton tglbtnInfo;
    private JToggleButton tglbtnReceive;
    private JPanel panel_2;
    private JToggleButton tglbtnVIN;
    private JPanel pnlInfoWindow;
    private JButton btnShowInfoWindow;
    private JButton btnHideInfoWindow;
    private JToggleButton tglbtnInfoWindow;
    private JComboBox cmbxCode;
    private JPanel panel_3;
    private JCheckBox chckbxDashboardLightning;
    private JSlider sliderBrightness;
    private JLabel lblVin_1;
    private JLabel lblxStatus;
    private JLabel lblxfInfo;
    private JLabel lblxaInfowindow;
    private JPanel panel_4;
    private JSlider sliderTemperature;

    /**
     * Create the dialog.
     */
    public BSIDialog(BSI client) {
        mBSI = client;
        
        setBounds(100, 100, 490, 346);
        getContentPane().setLayout(new BorderLayout(0, 0));
        
        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(new GridLayout(0, 2, 10, 5));
        
        lblVin_1 = new JLabel("0x2B6 VIN");
        lblVin_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
        lblVin_1.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lblVin_1);
        
        panel_2 = new JPanel();
        panel.add(panel_2);
        
        tglbtnVIN = new JToggleButton("Start VIN");
        panel_2.add(tglbtnVIN);
        
        tglbtnVIN.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (tglbtnVIN.isSelected()) {
                    mBSI.startVIN();
                } else {
                    mBSI.stopVIN();
                }
            }
        });
        
        textField = new JTextField();
        panel_2.add(textField);
        textField.setToolTipText("VIN");
        textField.setText(mBSI.getVIN());
        textField.setColumns(8);
        
        lblxStatus = new JLabel("0x036 Status");
        lblxStatus.setHorizontalAlignment(SwingConstants.RIGHT);
        lblxStatus.setFont(new Font("Tahoma", Font.PLAIN, 20));
        panel.add(lblxStatus);
        
        panel_3 = new JPanel();
        panel.add(panel_3);
        panel_3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        
        tglbtnStatus = new JToggleButton("Start status");
        panel_3.add(tglbtnStatus);
        
        chckbxDashboardLightning = new JCheckBox("Dashboard lighting");
        panel_3.add(chckbxDashboardLightning);
        
        sliderBrightness = new JSlider();
        sliderBrightness.setMinimum(0);
        sliderBrightness.setMaximum(15);
        sliderBrightness.setValue(8);
        panel_3.add(sliderBrightness);
        
        tglbtnStatus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    if (tglbtnStatus.isSelected()) {
                        mBSI.startStatus();
                    } else {
                        mBSI.stopStatus();
                    }
                } catch (CanFrameException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        
        lblxfInfo = new JLabel("0x0F6 Info");
        lblxfInfo.setHorizontalAlignment(SwingConstants.RIGHT);
        lblxfInfo.setFont(new Font("Tahoma", Font.PLAIN, 20));
        panel.add(lblxfInfo);
        
        panel_4 = new JPanel();
        panel.add(panel_4);
        
        tglbtnInfo = new JToggleButton("Start info");
        panel_4.add(tglbtnInfo);
        
        sliderTemperature = new JSlider();
        sliderTemperature.setValue(250);
        sliderTemperature.setMaximum(850);
        sliderTemperature.setMinimum(-400);
        panel_4.add(sliderTemperature);
        
        tglbtnInfo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (tglbtnInfo.isSelected()) {
                    try {
                        mBSI.startInfo();
                    } catch (CanFrameException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                } else {
                    mBSI.stopInfo();
                }
            }
        });
        
        lblxaInfowindow = new JLabel("0x1A1 InfoWindow");
        lblxaInfowindow.setHorizontalAlignment(SwingConstants.RIGHT);
        lblxaInfowindow.setFont(new Font("Tahoma", Font.PLAIN, 20));
        panel.add(lblxaInfowindow);
        
        pnlInfoWindow = new JPanel();
        panel.add(pnlInfoWindow);
        
        tglbtnInfoWindow = new JToggleButton("Start info window");
        pnlInfoWindow.add(tglbtnInfoWindow);
        
        btnShowInfoWindow = new JButton("Show");
        pnlInfoWindow.add(btnShowInfoWindow);
        
        btnHideInfoWindow = new JButton("Hide");
        pnlInfoWindow.add(btnHideInfoWindow);
        
        String[] codes = new String[256];
        for (int i=0; i<=255; i++) {
            codes[i] = String.format("%02X", i);
        }
        
        cmbxCode = new JComboBox(codes);
        pnlInfoWindow.add(cmbxCode);
        
        JPanel panel_1 = new JPanel();
        getContentPane().add(panel_1, BorderLayout.NORTH);
        
        tglbtnReceive = new JToggleButton("Send & Receive");
        panel_1.add(tglbtnReceive);
        
        refreshControls();
        
        textField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }
            public void removeUpdate(DocumentEvent e) {
                warn();
            }
            public void insertUpdate(DocumentEvent e) {
                warn();
            }
            public void warn() {
                try {
                    mBSI.setVIN(textField.getText());
                } catch (MessageException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        
        tglbtnReceive.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mBSI.setReceive(tglbtnReceive.isSelected());
            }
        });
        
        tglbtnInfoWindow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (tglbtnInfoWindow.isSelected()) {
                    try {
                        mBSI.startInfoWindow();
                    } catch (CanFrameException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                } else {
                    mBSI.stopInfoWindow();
                }
            }
        });
        
        btnShowInfoWindow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mBSI.setInfoWindowAction(BSIInfoWindowMessage.ACTION_SHOW);
            }
        });
        
        btnHideInfoWindow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mBSI.setInfoWindowAction(BSIInfoWindowMessage.ACTION_HIDE);
            }
        });
        
        cmbxCode.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent event) {
                String item = (String) event.getItem();
                
                int code = Integer.parseInt(item, 16);
                mBSI.setInfoWindowCode((byte) code);
            }
            
        });
        
        chckbxDashboardLightning.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
                mBSI.setDahsboardLightingEnabled(chckbxDashboardLightning.isSelected());
            }
        });
        sliderBrightness.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                mBSI.setDashboardLightingBrightness((byte) sliderBrightness.getValue());
            }
        });
        
        sliderTemperature.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                double temperature = sliderTemperature.getValue() / 10.0;
                mBSI.setTemperature(temperature);
            }
        });
    }

    public void refreshControls() {
        tglbtnStatus.setSelected(mBSI.isStatusStarted());
        tglbtnInfo.setSelected(mBSI.isInfoStarted());
        tglbtnReceive.setSelected(mBSI.isReceive());
        sliderBrightness.setValue(mBSI.getDashboardLightingBrightness());
        tglbtnVIN.setSelected(mBSI.isVINStarted());
        chckbxDashboardLightning.setSelected(mBSI.isDashboardLightingEnabled());
        
        boolean infoWindowStarted = mBSI.isInfoWindowStarted();
        tglbtnInfoWindow.setSelected(infoWindowStarted);
        btnShowInfoWindow.setEnabled(infoWindowStarted);
        btnHideInfoWindow.setEnabled(infoWindowStarted);
    }
}
