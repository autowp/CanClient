package com.autowp.dashboard;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.autowp.can.CanMessage;
import com.autowp.peugeot.CanComfort;
import com.autowp.peugeot.message.CurrentCDTrackMessage;
import com.autowp.peugeot.message.MessageException;
import com.autowp.peugeot.message.ParktronicMessage;
import com.autowp.peugeot.message.Track;
import com.autowp.peugeot.message.VolumeMessage;



public class DashboardTable extends JTable {

    private static final long serialVersionUID = 1L;

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
    }
    
    public void addCanMessage(CanMessage message)
    {
        String key = "";
        String value = "";
        
        try {
            switch (message.getId()) {
                case CanComfort.ID_TRACK_LIST:
                    //trackList.processMessage(message);
                    break;
                    
                case CanComfort.ID_VOLUME: {
                    
                    VolumeMessage peugeotMessage = new VolumeMessage(message);
                    
                    key = "Volume";
                    value = Integer.toString(peugeotMessage.getVolume());
                    
                    break;
                }
                    
                case CanComfort.ID_CURRENT_CD_TRACK: {
                    
                    CurrentCDTrackMessage peugeotMessage = new CurrentCDTrackMessage(message);
                    Track track = peugeotMessage.getTrack();
                    
                    key = "Current CD Track";
                    value = track.getCompleteName("/", "-");
                    break;
                }
                
                case CanComfort.ID_PARKTRONIC:
                    ParktronicMessage pm = new ParktronicMessage(message);
                    
                    key = "Parktronic";
                    value = pm.getFrontLeft() + " - " + pm.getFrontCenter() + " - " + pm.getFrontRight() + " / " +
                            pm.getRearLeft() + " - " + pm.getRearCenter() + " - " + pm.getRearRight() + " / " +
                            (pm.getShow() ? "Show" : "Don't show") + " / " +
                            (pm.getSoundEnabled() ? "Sound enabled" : "Sound disabled") + " / " + 
                            "Period: " + pm.getSoundPeriod();
                    
                    break;
                    
                default:
                    // just skip
            }
            
        } catch (MessageException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
            
        DefaultTableModel model = (DefaultTableModel)this.getModel();
        
        boolean found = false;
        for (int i=0; i<model.getRowCount(); i++) {
            if (model.getValueAt(i, 0) == key) {
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
}
