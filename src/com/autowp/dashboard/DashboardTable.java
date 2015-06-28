package com.autowp.dashboard;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
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
import com.autowp.peugeot.message.TimeMessage;
import com.autowp.peugeot.message.Track;
import com.autowp.peugeot.message.VolumeMessage;



public class DashboardTable extends JTable {

    private static final long serialVersionUID = 1L;
    
    private String[] columnNames = {"Key", "Value"};

    public DashboardTable()
    {
        this.setRowSelectionAllowed(true);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setShowVerticalLines(true);
        this.setShowHorizontalLines(true);
        @SuppressWarnings("serial")
        DefaultTableModel model = new DefaultTableModel(
            new Object[][] {
            },
            new String[] {
                "Param", "Value"
            }
        ) {
            @SuppressWarnings("rawtypes")
            Class[] columnTypes = new Class[] {
                String.class, String.class
            };
            @SuppressWarnings({ "unchecked", "rawtypes" })
            public Class getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
        };
        this.setModel(model);
        
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
                model.setValueAt(value, i, 1);
                found = true;
                break;
            }
        }
        
        if (!found) {
            model.addRow(
                new Object[] {
                    key, 
                    value
                }
            );
        }
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
                    
                    addPair("Volume", peugeotMessage.getVolume());
                    
                    break;
                }
                    
                case CanComfort.ID_CURRENT_CD_TRACK: {
                    
                    CurrentCDTrackMessage peugeotMessage = new CurrentCDTrackMessage(message);
                    Track track = peugeotMessage.getTrack();
                    
                    addPair("Current CD Track", track.getCompleteName("/", "-"));
                    break;
                }
                
                case CanComfort.ID_PARKTRONIC:
                    ParktronicMessage pm = new ParktronicMessage(message);
                    
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
                    
                    addPair("CurrentCDTrackInfo / TrackNumber", cctim.getTrackNumber());
                    addPair("CurrentCDTrackInfo / Time", String.format(
                        "%s of %s", 
                        cctim.getCurrentTime(), cctim.getTotalTime()
                    ));
                    addPair("CurrentCDTrackInfo / SomeValue", cctim.getSomeValue());
                    
                    break;
                    
                case CanComfort.ID_TIME:
                    
                    TimeMessage tm = new TimeMessage(message);
                    String value = "";
                    for (byte b : message.getData()) {
                        value += String.format("%02X ", b);
                    }
                    addPair("Time / Hex", value);
                    addPair("Time", tm.getTimeString());
                    addPair("Time / Format", tm.isTimeFormat24() ? "24h" : "12h");
                    
                    break;
                    
                case CanComfort.ID_COLUMN_KEYPAD:
                    ColumnKeypadMessage ckm = new ColumnKeypadMessage(message);
                    
                    
                    String ckmValue = "";
                    for (byte b : message.getData()) {
                        ckmValue += String.format("%02X ", b);
                    }
                    
                    addPair("ColumnKeypad / Hex", ckmValue);
                    addPair("ColumnKeypad / Forward press", ckm.isForward());
                    addPair("ColumnKeypad / Backward press", ckm.isBackward());
                    addPair("ColumnKeypad / UnknownValue press", ckm.getUnknownValue());
                    addPair("ColumnKeypad / VolumeUp press", ckm.isVolumeUp());
                    addPair("ColumnKeypad / VolumeDown press", ckm.isVolumeDown());
                    addPair("ColumnKeypad / Source press", ckm.isSource());
                    
                    break;
                    
                case CanComfort.ID_RDS:
                    RDSMessage rum = new RDSMessage(message);
                    
                    String rumValue = "";
                    for (byte b : message.getData()) {
                        rumValue += String.format("%02X ", b);
                    }
                    
                    addPair("RDS / Hex", rumValue);
                    addPair("RDS / TA", rum.isTA());
                    addPair("RDS / Show PTY Menu", rum.isShowPTYMenu());
                    addPair("RDS / PTY", rum.isPTY());
                    addPair("RDS / PTY Value", rum.getPTYValueString());
                    
                    break;
                    
                case CanComfort.ID_RADIO_KEYPAD:
                    RadioKeypadMessage rkm = new RadioKeypadMessage(message);
                    
                    String rkmValue = "";
                    for (byte b : message.getData()) {
                        rkmValue += String.format("%02X ", b);
                    }
                    
                    addPair("RadioKeypad / Hex", rkmValue);
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
                    
                default:
                    String key = String.format("%03X", message.getId()).toString();
                    String val = "";
                    for (byte b : message.getData()) {
                        val += String.format("%02X ", b);
                    }
                    addPair(key, val);
            }
            
        } catch (MessageException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
            
        
    }

}
