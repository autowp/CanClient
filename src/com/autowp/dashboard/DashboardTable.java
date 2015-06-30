package com.autowp.dashboard;

import java.awt.Color;
import java.awt.Component;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.autowp.can.CanMessage;
import com.autowp.peugeot.CanComfort;
import com.autowp.peugeot.message.AudioMenuMessage;
import com.autowp.peugeot.message.ColumnKeypadMessage;
import com.autowp.peugeot.message.CurrentCDTrackInfoMessage;
import com.autowp.peugeot.message.CurrentCDTrackMessage;
import com.autowp.peugeot.message.MessageException;
import com.autowp.peugeot.message.ParktronicMessage;
import com.autowp.peugeot.message.RDSMessage;
import com.autowp.peugeot.message.RadioKeypadMessage;
import com.autowp.peugeot.message.RadioMessage1;
import com.autowp.peugeot.message.TimeMessage;
import com.autowp.peugeot.message.Track;
import com.autowp.peugeot.message.VolumeMessage;



public class DashboardTable extends JTable {

    private static final long serialVersionUID = 1L;

    private static final int VALUE_COLUMN = 1;

    private static final int CHANGED_COLUMN = 2;

    public static final long BLINK_TIME = 3000;
    
    private String[] columnNames = {"Key", "Value"};
    

    
    private class CustomRenderer extends DefaultTableCellRenderer
    {
        private static final long serialVersionUID = 1L;

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (column == VALUE_COLUMN) {
                DefaultTableModel model = (DefaultTableModel)table.getModel();
                Long cTime = (Long)model.getValueAt(row, CHANGED_COLUMN);
                
                if (System.currentTimeMillis() - cTime <= BLINK_TIME) {
                    setForeground(Color.blue);
                } else {
                    this.setForeground(Color.black);
                }
            }
            return c;
        }
    }
    
    private class BlinkTask extends TimerTask {
        public void run() {
            DefaultTableModel model = (DefaultTableModel)getModel();
            int rowCount = model.getRowCount();
            for (int i=0; i<rowCount; i++) {
                model.fireTableCellUpdated(i, CHANGED_COLUMN);
            }
        }
    }

    public DashboardTable()
    {
        this.setRowSelectionAllowed(true);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setShowVerticalLines(true);
        this.setShowHorizontalLines(true);
        
        Timer timer = new Timer();
        timer.schedule(new BlinkTask(), 0, BLINK_TIME / 10);
        
        @SuppressWarnings("serial")
        DefaultTableModel model = new DefaultTableModel(
            new Object[][] {
            },
            new String[] {
                "Param", "Value", "Changed"
            }
        ) {
            @SuppressWarnings("rawtypes")
            Class[] columnTypes = new Class[] {
                String.class, String.class, Long.class
            };
            @SuppressWarnings({ "unchecked", "rawtypes" })
            public Class getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
        };
        this.setModel(model);
        
        this.getColumnModel().getColumn(1).setCellRenderer(new CustomRenderer());
        
        TableColumnModel tcm = this.getColumnModel();
        tcm.getColumn(0).setPreferredWidth(50);
        tcm.getColumn(1).setPreferredWidth(50);
        
        this.setAutoCreateRowSorter(true);
    }
    
    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }
    
    private void addPair(String key, boolean value)
    {
        addPair(key, value ? "true" : "false");
    }
    
    private void addPair(String key, int value)
    {
        addPair(key, new Integer(value).toString());
    }
    
    private void addPair(String key, String value)
    {
        DefaultTableModel model = (DefaultTableModel)this.getModel();
        
        boolean found = false;
        for (int i=0; i<model.getRowCount(); i++) {
            String val = model.getValueAt(i, 0).toString();
            int equals = val.compareTo(key);
            if (equals == 0) {
                String prevValue = model.getValueAt(i, 1).toString();
                model.setValueAt(value, i, VALUE_COLUMN);
                if (!prevValue.equals(value)) {
                    long cTime = System.currentTimeMillis();
                    model.setValueAt(cTime, i, CHANGED_COLUMN);
                }
                
                found = true;
                break;
            }
        }
        
        if (!found) {
            model.addRow(
                new Object[] {
                    key, 
                    value,
                    System.currentTimeMillis()
                }
            );
        }
    }
    
    private String messageToHex(CanMessage message)
    {
        String value = "";
        for (byte b : message.getData()) {
            value += String.format("%02X ", b);
        }
        return value;
    }
    
    public void addCanMessage(CanMessage message)
    {
        try {
            switch (message.getId()) {
                case CanComfort.ID_TRACK_LIST:
                    //trackList.processMessage(message);
                    break;
                    
                case CanComfort.ID_VOLUME: {
                    
                    VolumeMessage peugeotMessage = new VolumeMessage(message);
                    
                    addPair("Volume / Hex", messageToHex(message));
                    addPair("Volume", peugeotMessage.getVolume());
                    
                    break;
                }
                    
                case CanComfort.ID_CURRENT_CD_TRACK: {
                    
                    CurrentCDTrackMessage peugeotMessage = new CurrentCDTrackMessage(message);
                    Track track = peugeotMessage.getTrack();
                    
                    addPair("Current CD Track / Hex", messageToHex(message));
                    addPair("Current CD Track", track.getCompleteName("/", "-"));
                    break;
                }
                
                case CanComfort.ID_PARKTRONIC:
                    ParktronicMessage pm = new ParktronicMessage(message);
                    
                    addPair("Parktronic / Hex", messageToHex(message));
                    addPair("Parktronic / FrontLeft", pm.getFrontLeft());
                    addPair("Parktronic / FrontCenter", pm.getFrontCenter());
                    addPair("Parktronic / FrontRight", pm.getFrontRight());
                    addPair("Parktronic / RearLeft", pm.getRearLeft());
                    addPair("Parktronic / RearCenter", pm.getRearCenter());
                    addPair("Parktronic / RearRight", pm.getRearRight());
                    addPair("Parktronic / Show", pm.getShow() ? "Show" : "Don't show");
                    addPair("Parktronic / Sound", pm.getSoundEnabled() ? "Enabled" : "Disabled");
                    addPair("Parktronic / Sound Period", pm.getSoundPeriod());
                    
                    break;
                    
                case CanComfort.ID_AUDIO_MENU:
                    
                    AudioMenuMessage am = new AudioMenuMessage(message);
                    
                    addPair("AudioMenu / Hex", messageToHex(message));
                    addPair("AudioMenu / SideBalance", am.getSideBalance());
                    addPair("AudioMenu / Balance", am.getBalance());
                    addPair("AudioMenu / ShowSideBalance", am.isShowSideBalance());
                    addPair("AudioMenu / ShowTreble", am.isShowTreble());
                    addPair("AudioMenu / ShowBass", am.isShowBass());
                    addPair("AudioMenu / ShowBalance", am.isShowBalance());
                    addPair("AudioMenu / Bass", am.getBass());
                    addPair("AudioMenu / Treble", am.getTreble());
                    addPair("AudioMenu / ShowLoudnessCorrection", am.isShowLoudnessCorrection());
                    addPair("AudioMenu / LoudnessCorrection", am.isLoudnessCorrection());
                    addPair("AudioMenu / ShowAutomaticVolume", am.isShowAutomaticVolume());
                    addPair("AudioMenu / AutomaticVolume", am.isAutomaticVolume());
                    addPair("AudioMenu / ShowMusicalAmbiance", am.isShowMusicalAmbiance());
                    addPair("AudioMenu / MusicalAmbiance", am.getMusicalAmbianceName());

                    break;
                    
                case CanComfort.ID_CURRENT_CD_TRACK_INFO:
                    
                    CurrentCDTrackInfoMessage cctim = new CurrentCDTrackInfoMessage(message);
                    
                    addPair("CurrentCDTrackInfo / Hex", messageToHex(message));
                    addPair("CurrentCDTrackInfo / TrackNumber", cctim.getTrackNumber());
                    addPair("CurrentCDTrackInfo / Time", String.format(
                        "%s of %s", 
                        cctim.getCurrentTime(), cctim.getTotalTime()
                    ));
                    addPair("CurrentCDTrackInfo / SomeValue", cctim.getSomeValue());
                    
                    break;
                    
                case CanComfort.ID_TIME:
                    
                    TimeMessage tm = new TimeMessage(message);

                    addPair("Time / Hex", messageToHex(message));
                    addPair("Time", tm.getTimeString());
                    addPair("Time / Format", tm.isTimeFormat24() ? "24h" : "12h");
                    
                    break;
                    
                case CanComfort.ID_COLUMN_KEYPAD:
                    ColumnKeypadMessage ckm = new ColumnKeypadMessage(message);
                    
                    addPair("ColumnKeypad / Hex", messageToHex(message));
                    addPair("ColumnKeypad / Forward press", ckm.isForward());
                    addPair("ColumnKeypad / Backward press", ckm.isBackward());
                    addPair("ColumnKeypad / UnknownValue press", ckm.getUnknownValue());
                    addPair("ColumnKeypad / VolumeUp press", ckm.isVolumeUp());
                    addPair("ColumnKeypad / VolumeDown press", ckm.isVolumeDown());
                    addPair("ColumnKeypad / Source press", ckm.isSource());
                    
                    break;
                    
                case CanComfort.ID_RDS:
                    
                    addPair("RDS / Hex", messageToHex(message));
                    
                    RDSMessage rum = new RDSMessage(message);
                    addPair("RDS / REG mode activated", rum.isREGModeActivated());
                    addPair("RDS / RDS search activated", rum.isRDSSearchActivated());
                    addPair("RDS / TA", rum.isTA());
                    addPair("RDS / Show PTY Menu", rum.isShowPTYMenu());
                    addPair("RDS / PTY", rum.isPTY());
                    addPair("RDS / PTY Value", rum.getPTYValueString());
                    
                    break;
                    
                case CanComfort.ID_RADIO_KEYPAD:
                    RadioKeypadMessage rkm = new RadioKeypadMessage(message);
                    
                    addPair("RadioKeypad / Hex", messageToHex(message));
                    addPair("RadioKeypad / Audio press", rkm.isAudio());
                    addPair("RadioKeypad / Clim  press", rkm.isClim());
                    addPair("RadioKeypad / Dark press", rkm.isDark());
                    addPair("RadioKeypad / Down press", rkm.isDown());
                    addPair("RadioKeypad / ESC press", rkm.isESC());
                    addPair("RadioKeypad / Left press", rkm.isLeft());
                    addPair("RadioKeypad / Menu press", rkm.isMenu());
                    addPair("RadioKeypad / Ok press", rkm.isOk());
                    addPair("RadioKeypad / Right press", rkm.isRight());
                    addPair("RadioKeypad / Trip press", rkm.isTrip());
                    addPair("RadioKeypad / Up press", rkm.isUp());
                   
                    break;
                    
                case CanComfort.ID_RADIO_1:
                    addPair("1E0 Radio1 / Hex", messageToHex(message));
                    RadioMessage1 r1m = new RadioMessage1(message);
                    addPair("1E0 Radio1 / TrackIntro", r1m.isTrackIntro());
                    addPair("1E0 Radio1 / RandomPlay", r1m.isRandomPlay());
                    addPair("1E0 Radio1 / AltFreqencies (RDS)", r1m.isAltFreqencies());
                    addPair("1E0 Radio1 / RadioText", r1m.isRadioText());
                    addPair("1E0 Radio1 / REG mode", r1m.isRegMode());
                    addPair("1E0 Radio1 / Unknown1", r1m.getUnknown1());
                    addPair("1E0 Radio1 / Unknown2", r1m.isUnknown2());
                    addPair("1E0 Radio1 / Unknown3", r1m.getUnknown3());
                    break;
                    
                default:
                    String key = String.format("%03X", message.getId()).toString();
                    addPair(key, messageToHex(message));
            }
            
        } catch (MessageException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
            
        
    }

}
