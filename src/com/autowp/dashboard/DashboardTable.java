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
import com.autowp.psa.CanComfort;
import com.autowp.psa.message.AudioMenuMessage;
import com.autowp.psa.message.BSIInfoMessage;
import com.autowp.psa.message.BSIInfoWindowMessage;
import com.autowp.psa.message.BSIStatusMessage;
import com.autowp.psa.message.BSIVINMessage;
import com.autowp.psa.message.CDChangerStatusMessage;
import com.autowp.psa.message.ColumnKeypadMessage;
import com.autowp.psa.message.CurrentCDTrackInfoMessage;
import com.autowp.psa.message.CurrentCDTrackMessage;
import com.autowp.psa.message.DisplayConditioningMessage;
import com.autowp.psa.message.DisplayPingMessage;
import com.autowp.psa.message.DisplayStatusMessage;
import com.autowp.psa.message.DisplayUnknown1Message;
import com.autowp.psa.message.DisplayWelcomeMessage;
import com.autowp.psa.message.MessageException;
import com.autowp.psa.message.ParktronicMessage;
import com.autowp.psa.message.RDSMessage;
import com.autowp.psa.message.RadioCDChangerCommandMessage;
import com.autowp.psa.message.RadioKeypadMessage;
import com.autowp.psa.message.Radio1Message;
import com.autowp.psa.message.RadioPingMessage;
import com.autowp.psa.message.RadioStatusMessage;
import com.autowp.psa.message.RadioWelcomeMessage;
import com.autowp.psa.message.TimeMessage;
import com.autowp.psa.message.Track;
import com.autowp.psa.message.VolumeMessage;



public class DashboardTable extends JTable {

    private static final long serialVersionUID = 1L;

    private static final int VALUE_COLUMN = 1;
    private static final int LAST_COLUMN = 2;
    private static final int PERIOD_COLUMN = 3;
    private static final int CHANGED_COLUMN = 4;

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
                "Param", "Value", "Last", "Period", "Changed"
            }
        ) {
            @SuppressWarnings("rawtypes")
            Class[] columnTypes = new Class[] {
                String.class, String.class, Long.class, Long.class, Long.class
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
    
    private void addPair(String key, double value)
    {
        addPair(key, new Double(value).toString());
    }
    
    private void addPair(String key, float value)
    {
        addPair(key, new Float(value).toString());
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
                Long prev = (Long)model.getValueAt(i, LAST_COLUMN);
                
                String prevValue = model.getValueAt(i, 1).toString();
                model.setValueAt(value, i, VALUE_COLUMN);
                long cTime = System.currentTimeMillis();
                if (!prevValue.equals(value)) {
                    model.setValueAt(cTime, i, CHANGED_COLUMN);
                }
                model.setValueAt(cTime, i, LAST_COLUMN);
                
                model.setValueAt(cTime - prev, i, PERIOD_COLUMN);
                
                found = true;
                break;
            }
        }
        
        if (!found) {
            long cTime = System.currentTimeMillis();
            model.addRow(
                new Object[] {
                    key, 
                    value,
                    cTime,
                    0,
                    cTime
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
            switch (0/*message.getId()*/) {
                case CanComfort.ID_TRACK_LIST:
                    //trackList.processMessage(message);
                    break;
                    
                case CanComfort.ID_VOLUME: {
                    
                    VolumeMessage peugeotMessage = new VolumeMessage(message);
                    
                    addPair("1A5 Volume / Hex", messageToHex(message));
                    addPair("1A5 Volume", peugeotMessage.getVolume());
                    
                    break;
                }
                    
                case CanComfort.ID_CURRENT_CD_TRACK: {
                    
                    CurrentCDTrackMessage peugeotMessage = new CurrentCDTrackMessage(message);
                    Track track = peugeotMessage.getTrack();
                    
                    addPair("0A4 Current CD Track / Hex", messageToHex(message));
                    addPair("0A4 Current CD Track", track.getCompleteName("/", "-"));
                    break;
                }
                
                case CanComfort.ID_PARKTRONIC:
                    ParktronicMessage pm = new ParktronicMessage(message);
                    
                    addPair("0E1 Parktronic / Hex", messageToHex(message));
                    addPair("0E1 Parktronic / FrontLeft", pm.getFrontLeft());
                    addPair("0E1 Parktronic / FrontCenter", pm.getFrontCenter());
                    addPair("0E1 Parktronic / FrontRight", pm.getFrontRight());
                    addPair("0E1 Parktronic / RearLeft", pm.getRearLeft());
                    addPair("0E1 Parktronic / RearCenter", pm.getRearCenter());
                    addPair("0E1 Parktronic / RearRight", pm.getRearRight());
                    addPair("0E1 Parktronic / Show", pm.getShow() ? "Show" : "Don't show");
                    addPair("0E1 Parktronic / Sound", pm.getSoundEnabled() ? "Enabled" : "Disabled");
                    addPair("0E1 Parktronic / Sound Period", pm.getSoundPeriod());
                    
                    break;
                    
                case CanComfort.ID_AUDIO_MENU:
                    
                    AudioMenuMessage am = new AudioMenuMessage(message);
                    
                    addPair("1E5 AudioMenu / Hex", messageToHex(message));
                    addPair("1E5 AudioMenu / SideBalance", am.getSideBalance());
                    addPair("1E5 AudioMenu / Balance", am.getBalance());
                    addPair("1E5 AudioMenu / ShowSideBalance", am.isShowSideBalance());
                    addPair("1E5 AudioMenu / ShowTreble", am.isShowTreble());
                    addPair("1E5 AudioMenu / ShowBass", am.isShowBass());
                    addPair("1E5 AudioMenu / ShowBalance", am.isShowBalance());
                    addPair("1E5 AudioMenu / Bass", am.getBass());
                    addPair("1E5 AudioMenu / Treble", am.getTreble());
                    addPair("1E5 AudioMenu / ShowLoudnessCorrection", am.isShowLoudnessCorrection());
                    addPair("1E5 AudioMenu / LoudnessCorrection", am.isLoudnessCorrection());
                    addPair("1E5 AudioMenu / ShowAutomaticVolume", am.isShowAutomaticVolume());
                    addPair("1E5 AudioMenu / AutomaticVolume", am.isAutomaticVolume());
                    addPair("1E5 AudioMenu / ShowMusicalAmbiance", am.isShowMusicalAmbiance());
                    addPair("1E5 AudioMenu / MusicalAmbiance", am.getMusicalAmbianceName());

                    break;
                    
                case CanComfort.ID_CURRENT_CD_TRACK_INFO:
                    
                    CurrentCDTrackInfoMessage cctim = new CurrentCDTrackInfoMessage(message);
                    
                    addPair("3A5 CurrentCDTrackInfo / Hex", messageToHex(message));
                    addPair("3A5 CurrentCDTrackInfo / TrackNumber", cctim.getTrackNumber());
                    addPair("3A5 CurrentCDTrackInfo / Time", String.format(
                        "%s of %s", 
                        cctim.getCurrentTime(), cctim.getTotalTime()
                    ));
                    addPair("3A5 CurrentCDTrackInfo / SomeValue", cctim.getSomeValue());
                    
                    break;
                    
                case CanComfort.ID_TIME:
                    
                    TimeMessage tm = new TimeMessage(message);

                    addPair("3F6 Time / Hex", messageToHex(message));
                    addPair("3F6 Time", tm.getTimeString());
                    addPair("3F6 Time / Format", tm.isTimeFormat24() ? "24h" : "12h");
                    
                    break;
                    
                case CanComfort.ID_COLUMN_KEYPAD:
                    ColumnKeypadMessage ckm = new ColumnKeypadMessage(message);
                    
                    addPair("21F ColumnKeypad / Hex", messageToHex(message));
                    addPair("21F ColumnKeypad / Forward press", ckm.isForward());
                    addPair("21F ColumnKeypad / Backward press", ckm.isBackward());
                    addPair("21F ColumnKeypad / UnknownValue press", ckm.getUnknownValue());
                    addPair("21F ColumnKeypad / VolumeUp press", ckm.isVolumeUp());
                    addPair("21F ColumnKeypad / VolumeDown press", ckm.isVolumeDown());
                    addPair("21F ColumnKeypad / Source press", ckm.isSource());
                    
                    break;
                    
                case CanComfort.ID_RDS:
                    
                    addPair("265 RDS / Hex", messageToHex(message));
                    
                    RDSMessage rum = new RDSMessage(message);
                    addPair("265 RDS / REG mode activated", rum.isREGModeActivated());
                    addPair("265 RDS / RDS search activated", rum.isRDSSearchActivated());
                    addPair("265 RDS / TA", rum.isTA());
                    addPair("265 RDS / Show PTY Menu", rum.isShowPTYMenu());
                    addPair("265 RDS / PTY", rum.isPTY());
                    addPair("265 RDS / PTY Value", rum.getPTYValueString());
                    
                    break;
                    
                case CanComfort.ID_RADIO_KEYPAD:
                    RadioKeypadMessage rkm = new RadioKeypadMessage(message);
                    
                    addPair("3E5 RadioKeypad / Hex", messageToHex(message));
                    addPair("3E5 RadioKeypad / Audio press", rkm.isAudio());
                    addPair("3E5 RadioKeypad / Clim  press", rkm.isClim());
                    addPair("3E5 RadioKeypad / Dark press", rkm.isDark());
                    addPair("3E5 RadioKeypad / Down press", rkm.isDown());
                    addPair("3E5 RadioKeypad / ESC press", rkm.isESC());
                    addPair("3E5 RadioKeypad / Left press", rkm.isLeft());
                    addPair("3E5 RadioKeypad / Menu press", rkm.isMenu());
                    addPair("3E5 RadioKeypad / Ok press", rkm.isOk());
                    addPair("3E5 RadioKeypad / Right press", rkm.isRight());
                    addPair("3E5 RadioKeypad / Trip press", rkm.isTrip());
                    addPair("3E5 RadioKeypad / Up press", rkm.isUp());
                    addPair("3E5 RadioKeypad / Tel press", rkm.isTel());
                   
                    break;
                    
                case CanComfort.ID_RADIO_1: {
                    addPair("1E0 Radio1 / Hex", messageToHex(message));
                    Radio1Message r1m = new Radio1Message(message);
                    addPair("1E0 Radio1 / TrackIntro", r1m.isTrackIntro());
                    addPair("1E0 Radio1 / RandomPlay", r1m.isRandomPlay());
                    addPair("1E0 Radio1 / AltFreqencies (RDS)", r1m.isAltFreqencies());
                    addPair("1E0 Radio1 / RadioText", r1m.isRadioText());
                    addPair("1E0 Radio1 / REG mode", r1m.isRegMode());
                    addPair("1E0 Radio1 / CD Repeat", r1m.getCDRepeat());
                    addPair("1E0 Radio1 / Unknown1", r1m.getUnknown1());
                    addPair("1E0 Radio1 / Unknown2", r1m.isUnknown2());
                    addPair("1E0 Radio1 / Unknown3", r1m.getUnknown3());
                    addPair("1E0 Radio1 / Unknown4", r1m.getUnknown4());
                    addPair("1E0 Radio1 / Unknown5", r1m.getUnknown5());
                    addPair("1E0 Radio1 / Unknown6", r1m.getUnknown6());
                    break;
                }
                    
                case CanComfort.ID_DISPLAY_CONDITIONING:
                    addPair("1E6 DisplayConditioning / Hex", messageToHex(message));
                    DisplayConditioningMessage dcm = new DisplayConditioningMessage(message);
                    addPair("1E6 DisplayConditioning / isDefault", dcm.isDefault());
                    addPair("1E6 DisplayConditioning / isLHRHControl", dcm.isLHRHControl());
                    addPair("1E6 DisplayConditioning / isAcOff", dcm.isAcOff());
                    break;
                    
                case CanComfort.ID_DISPLAY_STATUS:
                    addPair("167 DisplayStatus / Hex", messageToHex(message));
                    DisplayStatusMessage dsm = new DisplayStatusMessage(message);
                    addPair("1E6 DisplayStatus / isOff", dsm.isOff());
                    addPair("1E6 DisplayStatus / Unknown1", dsm.getUnknown1());
                    addPair("1E6 DisplayStatus / Unknown2", dsm.getUnknown2());
                    addPair("1E6 DisplayStatus / Unknown3", dsm.getUnknown3());
                    addPair("1E6 DisplayStatus / Unknown4", dsm.getUnknown4());
                    break;
                    
                case CanComfort.ID_DISPLAY_UNKNOWN1:
                    addPair("0DF DisplayUnknown1 / Hex", messageToHex(message));
                    DisplayUnknown1Message du1m = new DisplayUnknown1Message(message);
                    addPair("0DF DisplayUnknown1 / Unknown1", du1m.isUnknown1());
                    addPair("0DF DisplayUnknown1 / Unknown2", du1m.isUnknown2());
                    addPair("0DF DisplayUnknown1 / Unknown3", du1m.getUnknown3());
                    addPair("0DF DisplayUnknown1 / Unknown4", du1m.getUnknown4());
                    addPair("0DF DisplayUnknown1 / Unknown5", du1m.getUnknown5());
                    break;
                    
                case CanComfort.ID_BSI_INFO:
                    addPair("0F6 BsiInfo / Hex", messageToHex(message));
                    BSIInfoMessage bi1m = new BSIInfoMessage(message);
                    addPair("0F6 BsiInfo / isReverse", bi1m.isReverse());
                    addPair("0F6 BsiInfo / Gear", bi1m.getGear());
                    addPair("0F6 BsiInfo / Temperature", bi1m.getTemperature());
                    addPair("0F6 BsiInfo / Odometer", bi1m.getOdometer());
                    break;
                    
                case CanComfort.ID_BSI_INFO_WINDOW:
                    addPair("1A1 BsiInfoWindow / Hex", messageToHex(message));
                    BSIInfoWindowMessage biwm = new BSIInfoWindowMessage(message);
                    addPair("1A1 BsiInfoWindow / Action", biwm.getAction());
                    addPair("1A1 BsiInfoWindow / Code", biwm.getCode());
                    
                    break;
                    
                case CanComfort.ID_DISPLAY_PING: {
                    addPair("525 DisplayPing / Hex", messageToHex(message));
                    DisplayPingMessage dpm = new DisplayPingMessage(message);
                    break;
                }
                
                case CanComfort.ID_RADIO_PING: {
                    addPair("520 RadioPing / Hex", messageToHex(message));
                    RadioPingMessage rpm = new RadioPingMessage(message);
                    break;
                }
                    
                case CanComfort.ID_DISPLAY_WELCOME: {
                    addPair("5E5 DisplayWelcome / Hex", messageToHex(message));
                    DisplayWelcomeMessage dwm = new DisplayWelcomeMessage(message);
                    break;
                }
                
                case CanComfort.ID_RADIO_WELCOME: {
                    addPair("5E0 RadioWelcome / Hex", messageToHex(message));
                    RadioWelcomeMessage rwm = new RadioWelcomeMessage(message);
                    break;
                }
                
                case CanComfort.ID_RADIO_STATUS: {
                    addPair("165 RadioStatus / Hex", messageToHex(message));
                    RadioStatusMessage rsm = new RadioStatusMessage(message);
                    addPair("165 RadioStatus / Enabled", rsm.isEnabled());
                    addPair("165 RadioStatus / Source", rsm.getSource());
                    addPair("165 RadioStatus / CD Changer Available", rsm.isCDChangerAvailable());
                    addPair("165 RadioStatus / CD Disk Available", rsm.isCDDiskAvailable());
                    addPair("165 RadioStatus / Unknown0", rsm.getUnknown0());
                    addPair("165 RadioStatus / Unknown1", rsm.getUnknown1());
                    break;
                }
                
                case CanComfort.ID_CD_CHANGER_STATUS: {
                    addPair("1A0 CDChangerStatus / Hex", messageToHex(message));
                    CDChangerStatusMessage ccsm = new CDChangerStatusMessage(message);
                    addPair("1A0 CDChangerStatus / Random", ccsm.isRandom());
                    addPair("1A0 CDChangerStatus / Repeat", ccsm.isRepeat());
                    addPair("1A0 CDChangerStatus / Intro", ccsm.isIntro());
                    break;
                }
                
                case CanComfort.ID_RADIO_CD_CHANGER_COMMAND: {
                    addPair("131 RadioCDChangerCommand / Hex", messageToHex(message));
                    RadioCDChangerCommandMessage cccm = new RadioCDChangerCommandMessage(message);
                    addPair("131 RadioCDChangerCommand / Random", cccm.isRandom());
                    addPair("131 RadioCDChangerCommand / Repeat", cccm.isRepeat());
                    addPair("131 RadioCDChangerCommand / Unknown0", cccm.getUnknown0());
                    addPair("131 RadioCDChangerCommand / Enable", cccm.isEnable());
                    addPair("131 RadioCDChangerCommand / EnablePlaying", cccm.isEnablePlaying());
                    addPair("131 RadioCDChangerCommand / TrackBack", cccm.isTrackBack());
                    addPair("131 RadioCDChangerCommand / TrackForward", cccm.isTrackForward());
                    addPair("131 RadioCDChangerCommand / RewindTrack", cccm.isRewindTrack());
                    addPair("131 RadioCDChangerCommand / Intro", cccm.isIntro());
                    addPair("131 RadioCDChangerCommand / FastForward", cccm.isFastForward());
                    addPair("131 RadioCDChangerCommand / FastBackward", cccm.isFastBackward());
                    byte track = cccm.getGotoTrack();
                    if (track != 0x00) {
                        addPair("131 RadioCDChangerCommand / Goto Track", track);
                    }
                    byte disk = cccm.getGotoDisk();
                    if (disk != 0x00) {
                        addPair("131 RadioCDChangerCommand / Goto Disk", disk);
                    }
                    break;
                }
                
                case CanComfort.ID_BSI_STATUS: {
                    addPair("036 BSIStatus / Hex", messageToHex(message));
                    BSIStatusMessage cccm = new BSIStatusMessage(message);
                    addPair("036 BSIStatus / DashboardLightningEnabled", cccm.isDashboardLightningEnabled());
                    addPair("036 BSIStatus / DashboardLightningBrightness", cccm.getDashboardLightningBrightness());
                    break;
                }
                
                case CanComfort.ID_VIN: {
                    addPair("2B6 BSIStatus / Hex", messageToHex(message));
                    BSIVINMessage cccm = new BSIVINMessage(message);
                    addPair("2B6 BSIStatus / VIN", cccm.getVIN());
                    break;
                }
                    
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
