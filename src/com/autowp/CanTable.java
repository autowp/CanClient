package com.autowp;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import com.autowp.canclient.CanFrame;


@SuppressWarnings("serial")
public class CanTable extends JTable {
    public CanTable()
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
            @SuppressWarnings("unchecked")
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
        
        model.addRow(
            new Object[] {
                idStr, 
                data.length, 
                Hex.encodeHexString(data).toUpperCase()
            }
        );
    }
}
