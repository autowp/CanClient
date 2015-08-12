package com.autowp.psa.cdchanger;

import javax.swing.JDialog;
import javax.swing.JToggleButton;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import java.awt.GridLayout;

import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.SwingConstants;

import com.autowp.can.CanFrameException;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class CDChangerDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private CDChanger mCDChanger;
    private JToggleButton tglbtnDisk;
    private JToggleButton tglbtnStatus;
    private JToggleButton tglbtnTracksCount;
    private JToggleButton tglbtnDisk2;
    private JToggleButton tglbtnCurrentTrack;
    private JToggleButton tglbtnPing;
    private JSpinner spinnerDisk;
    private JSpinner spinnerCurrentTrack;
    private JSpinner spinnerTracks;
    private JToggleButton tglbtnLoopback;

    /**
     * Create the dialog.
     */
    public CDChangerDialog(final CDChanger cdChanger) {
        
        setBounds(100, 100, 450, 300);
        
        tglbtnLoopback = new JToggleButton("Loopback");
        getContentPane().add(tglbtnLoopback, BorderLayout.NORTH);
        
        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(new GridLayout(6, 2, 10, 10));
        
        JLabel lblxDisk_1 = new JLabel("0x0E2 Disk");
        lblxDisk_1.setHorizontalAlignment(SwingConstants.RIGHT);
        lblxDisk_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
        panel.add(lblxDisk_1);
        
        tglbtnDisk = new JToggleButton("Disk");
        tglbtnDisk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (tglbtnDisk.isSelected()) {
                    try {
                        mCDChanger.startDisk();
                    } catch (CanFrameException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                } else {
                    mCDChanger.stopDisk();
                }
            }
        });
        panel.add(tglbtnDisk);
        
        JLabel lblxaStatus = new JLabel("0x1A0 Status");
        lblxaStatus.setHorizontalAlignment(SwingConstants.RIGHT);
        lblxaStatus.setFont(new Font("Tahoma", Font.PLAIN, 20));
        panel.add(lblxaStatus);
        
        tglbtnStatus = new JToggleButton("Status");
        tglbtnStatus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (tglbtnStatus.isSelected()) {
                    try {
                        mCDChanger.startStatus();
                    } catch (CanFrameException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                } else {
                    mCDChanger.stopStatus();
                }
            }
        });
        panel.add(tglbtnStatus);
        
        JLabel lblxaTrackscount = new JLabel("0x1A2 TracksCount");
        lblxaTrackscount.setHorizontalAlignment(SwingConstants.RIGHT);
        lblxaTrackscount.setFont(new Font("Tahoma", Font.PLAIN, 20));
        panel.add(lblxaTrackscount);
        
        tglbtnTracksCount = new JToggleButton("TracksCount");
        tglbtnTracksCount.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (tglbtnTracksCount.isSelected()) {
                    try {
                        mCDChanger.startTracksCount();
                    } catch (CanFrameException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                } else {
                    mCDChanger.stopTracksCount();
                }
            }
        });
        panel.add(tglbtnTracksCount);
        
        JLabel lblxDisk = new JLabel("0x162 Disk2");
        lblxDisk.setHorizontalAlignment(SwingConstants.RIGHT);
        lblxDisk.setFont(new Font("Tahoma", Font.PLAIN, 20));
        panel.add(lblxDisk);
        
        tglbtnDisk2 = new JToggleButton("Disk2");
        tglbtnDisk2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (tglbtnDisk2.isSelected()) {
                    try {
                        mCDChanger.startDisk2();
                    } catch (CanFrameException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                } else {
                    mCDChanger.stopDisk2();
                }
            }
        });
        panel.add(tglbtnDisk2);
        
        JLabel lblxbPing = new JLabel("0x1E2 CurrentTrack");
        lblxbPing.setHorizontalAlignment(SwingConstants.RIGHT);
        lblxbPing.setFont(new Font("Tahoma", Font.PLAIN, 20));
        panel.add(lblxbPing);
        
        tglbtnCurrentTrack = new JToggleButton("CurrentTrack");
        tglbtnCurrentTrack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (tglbtnCurrentTrack.isSelected()) {
                    try {
                        mCDChanger.startCurrentTrack();
                    } catch (CanFrameException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                } else {
                    mCDChanger.stopCurrentTrack();
                }
            }
        });
        panel.add(tglbtnCurrentTrack);
        
        JLabel label = new JLabel("0x531 Ping");
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setFont(new Font("Tahoma", Font.PLAIN, 20));
        panel.add(label);
        
        tglbtnPing = new JToggleButton("Ping");
        tglbtnPing.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (tglbtnPing.isSelected()) {
                    try {
                        mCDChanger.startPing();
                    } catch (CanFrameException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    mCDChanger.stopPing();
                }
            }
        });
        panel.add(tglbtnPing);
        
        JPanel panel_1 = new JPanel();
        getContentPane().add(panel_1, BorderLayout.SOUTH);
        
        JLabel lblDisk = new JLabel("Disk");
        panel_1.add(lblDisk);
        
        spinnerDisk = new JSpinner();
        spinnerDisk.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                Byte value = (Byte)spinnerDisk.getValue();
                mCDChanger.setDisk(value.byteValue());
            }
        });
        spinnerDisk.setModel(new SpinnerNumberModel(new Byte((byte) 1), new Byte((byte) 1), null, new Byte((byte) 1)));
        panel_1.add(spinnerDisk);
        
        JLabel lblTrack = new JLabel("Track");
        panel_1.add(lblTrack);
        
        spinnerCurrentTrack = new JSpinner();
        spinnerCurrentTrack.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Byte value = (Byte)spinnerCurrentTrack.getValue();
                mCDChanger.setCurrentTrack(value.byteValue());
            }
        });
        spinnerCurrentTrack.setModel(new SpinnerNumberModel(new Byte((byte) 1), new Byte((byte) 1), new Byte((byte) 99), new Byte((byte) 1)));
        panel_1.add(spinnerCurrentTrack);
        
        JLabel lblTracks = new JLabel("Tracks");
        panel_1.add(lblTracks);
        
        spinnerTracks = new JSpinner();
        spinnerTracks.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Byte value = (Byte)spinnerTracks.getValue();
                mCDChanger.setTotalTracks(value.byteValue());
            }
        });
        spinnerTracks.setModel(new SpinnerNumberModel(new Byte((byte) 1), new Byte((byte) 1), new Byte((byte) 99), new Byte((byte) 1)));
        panel_1.add(spinnerTracks);

        tglbtnLoopback.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mCDChanger.setLoopback(tglbtnLoopback.isSelected());
            }
        });
        
        setCDChanger(cdChanger);
    }

    public void setCDChanger(CDChanger cdChanger) {
        mCDChanger = cdChanger;
        
        boolean cdChangerSet = cdChanger != null;
        
        tglbtnLoopback.setSelected(cdChangerSet ? cdChanger.isLoopback() : false);
        tglbtnLoopback.setEnabled(cdChangerSet);
        tglbtnDisk.setSelected(cdChangerSet ? cdChanger.isDiskStarted() : false);
        tglbtnDisk.setEnabled(cdChangerSet);
        tglbtnStatus.setSelected(cdChangerSet ? cdChanger.isStatusStarted() : false);
        tglbtnStatus.setEnabled(cdChangerSet);
        tglbtnTracksCount.setSelected(cdChangerSet ? cdChanger.isTracksCountStarted() : false);
        tglbtnTracksCount.setEnabled(cdChangerSet);
        tglbtnDisk2.setSelected(cdChangerSet ? cdChanger.isDisk2Started() : false);
        tglbtnDisk2.setEnabled(cdChangerSet);
        tglbtnCurrentTrack.setSelected(cdChangerSet ? cdChanger.isCurrentTrackStarted() : false);
        tglbtnCurrentTrack.setEnabled(cdChangerSet);
        tglbtnPing.setSelected(cdChangerSet ? cdChanger.isPingStarted() : false);
        tglbtnPing.setEnabled(cdChangerSet);
        
        spinnerDisk.setEnabled(cdChangerSet);
        spinnerTracks.setEnabled(cdChangerSet);
        spinnerCurrentTrack.setEnabled(cdChangerSet);
        
        if (cdChangerSet) {
            spinnerDisk.setValue(new Byte(cdChanger.getDisk()));
            spinnerTracks.setValue(new Byte(cdChanger.getTotalTracks()));
            spinnerCurrentTrack.setValue(new Byte(cdChanger.getCurrentTrack()));
        }
        
    }
}
