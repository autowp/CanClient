package com.autowp;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import com.autowp.can.CanFrame;
import com.autowp.can.CanMessage;



@SuppressWarnings("serial")
public class CanFrameTable extends JTable {
    public CanFrameTable()
    {
        this.setRowSelectionAllowed(true);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setShowVerticalLines(true);
        this.setShowHorizontalLines(true);
        DefaultTableModel model = new DefaultTableModel(
            new Object[][] {
            },
            new String[] {
                "ID", "Length", "Data", "Comment"
            }
        ) {
            @SuppressWarnings("rawtypes")
            Class[] columnTypes = new Class[] {
                String.class, Integer.class, String.class, Object.class
            };
            @SuppressWarnings({ "unchecked", "rawtypes" })
            public Class getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
        };
        this.setModel(model);
        
        TableColumnModel tcm = this.getColumnModel();
        tcm.getColumn(0).setPreferredWidth(40);
        tcm.getColumn(1).setPreferredWidth(20);
        tcm.getColumn(2).setPreferredWidth(200);
        tcm.getColumn(3).setPreferredWidth(200);
    }
    
    public void addCanFrame(CanFrame frame)
    {
        DefaultTableModel model = (DefaultTableModel)this.getModel();
        byte[] data = frame.getData();
        String idStr = Integer.toHexString(frame.getId()).toUpperCase();
        idStr = StringUtils.leftPad(idStr, 3, '0');
        
        String dataStr = Hex.encodeHexString(data).toUpperCase();
        
        dataStr = StringUtils.join(splitStringEvery(dataStr, 2), ' ');
        
        model.addRow(
            new Object[] {
                idStr, 
                data.length, 
                dataStr
            }
        );
    }
    
    public void addCanMessage(CanMessage message)
    {
        DefaultTableModel model = (DefaultTableModel)this.getModel();
        byte[] data = message.getData();
        String idStr = Integer.toHexString(message.getId()).toUpperCase();
        idStr = StringUtils.leftPad(idStr, 3, '0');
        
        String dataStr = Hex.encodeHexString(data).toUpperCase();
        
        dataStr = StringUtils.join(splitStringEvery(dataStr, 2), ' ');
        
        model.addRow(
            new Object[] {
                idStr, 
                data.length, 
                dataStr
            }
        );
    }
    
    public String[] splitStringEvery(String s, int interval) {
        int arrayLength = (int) Math.ceil(((s.length() / (double)interval)));
        String[] result = new String[arrayLength];

        int j = 0;
        int lastIndex = result.length - 1;
        for (int i = 0; i < lastIndex; i++) {
            result[i] = s.substring(j, j + interval);
            j += interval;
        } //Add the last bit
        if (lastIndex >= 0) {
            result[lastIndex] = s.substring(j);
        }

        return result;
    }
}
