package com.autowp.peugeot.display;

import java.util.Arrays;

import javax.swing.JLabel;
import org.apache.commons.codec.binary.Hex;

import com.autowp.can.CanMessage;
import com.autowp.peugeot.CanComfort;
import com.autowp.peugeot.message.MessageException;
import com.autowp.peugeot.message.Track;



@SuppressWarnings("serial")
public class CurrentCDTrack extends JLabel {
    private Scheme scheme = null;
    
    private static final byte[] emptyData = new byte[] {0x20, 0x00, 0x00, 0x00, 0x00};
    
    protected Track track = new Track();
    
    public CurrentCDTrack(Scheme scheme)
    {
        this.scheme = scheme;
        
        this.setBackground(this.scheme.getBackground());
        this.setForeground(this.scheme.getForeground());
    }
    
    public void processMessage(CanMessage message) throws DisplayException
    {
        switch (message.getId()) {
            case CanComfort.ID_CURRENT_CD_TRACK:
                byte[] data = message.getData();
                if (data.length == 5) {
                    if (!Arrays.equals(data, emptyData)) {
                        String str = new String(Hex.encodeHex(data));
                        throw new DisplayException("Unexpected data `" + str + "`");
                    }
                    
                    track.setAuthor("");
                    track.setName("");
                    
                } else {
                    
                    if (!(data[0] == 0x20 && data[1] == 0x00)) {
                        String str = new String(Hex.encodeHex(data));
                        throw new DisplayException("Unexpected data `" + str + "`");
                    }
                    
                    if ((data[2] & 0xEF) != 0x48) {
                        String str = new String(Hex.encodeHex(data));
                        throw new DisplayException("Unexpected data `" + str + "`");
                    }
                    
                    boolean trackAuthorExists = (data[2] & 0x10) == 0x10;
                    int textDataOffset = 4;
                    byte[] textData = new byte[data.length - textDataOffset];
                    System.arraycopy(data, textDataOffset, textData, 0, textData.length);
                    try {
                        track.readFromBytes(textData, trackAuthorExists, true);
                    } catch (MessageException e) {
                        // TODO Auto-generated catch block
                        throw new DisplayException(e.getMessage());
                    }
                }
                
                this.setVisible(true);
                this.invalidate();
                
                this.setText(track.getCompleteName("/", ""));
                
                break;
        }
    }
}
