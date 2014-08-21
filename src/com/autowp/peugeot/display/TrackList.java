package com.autowp.peugeot.display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.apache.commons.codec.binary.Hex;

import com.autowp.can.CanMessage;
import com.autowp.peugeot.CanComfort;
import com.autowp.peugeot.message.Track;

@SuppressWarnings("serial")
public class TrackList extends JPanel {
    private Scheme scheme = null;
    
    public static final int LINES = 4;
    private static final double LINE_STRING_PADDING = .15;
    
    private static final String DEFAULT_TRACK_NAME = "Not entered";
    
    private ArrayList<Track> tracks = new ArrayList<Track>();
    private int listOffset = 0;
    private int currentOffset = 0;
    
    public TrackList(Scheme scheme)
    {
        this.scheme = scheme;
        
        this.setBackground(scheme.getBackground());
        this.setForeground(scheme.getForeground());
        
        this.setPreferredSize(new Dimension(400, 100));
        this.setMinimumSize(new Dimension(200, 50));
        this.setMaximumSize(new Dimension(800, 200));
    }
    
    public void next() throws DisplayException
    {
        if (currentOffset < LINES - 1) {
            currentOffset = currentOffset + 1;
        } else {
            int newListOffset = listOffset + 1;
            if (newListOffset + currentOffset < tracks.size()) {
                listOffset = newListOffset;
            } else {
                throw new DisplayException("Out of bounds");
            }
        }
        this.repaint();
    }
    
    public void prev() throws DisplayException
    {
        if (currentOffset > 0) {
            currentOffset = currentOffset - 1;
        } else {
            if (listOffset > 0) {
                listOffset = listOffset - 1;
            } else {
                throw new DisplayException("Out of bounds");
            }
        }
        this.repaint();
    }
    
    public void setTracksCount(int count) throws DisplayException
    {
        if (count < 0) {
            throw new DisplayException("Unexpected tracks count " + count);
        }
        if (count != tracks.size()) {
            if (count < tracks.size()) {
                for (int i=tracks.size()-1; i>=count; i--) {
                    tracks.remove(i);
                }
            } else {
                int toAdd = count - tracks.size();
                for (int i=0; i<toAdd; i++) {
                    tracks.add(new Track());
                }
            }
            
            this.repaint();
        }
    }
    
    public void setOffsets(int listOffset, int currentOffset) throws DisplayException
    {
        if (currentOffset < 0 || currentOffset > LINES-1) {
            throw new DisplayException("currentOffset is out of bounds");
        }
        
        if (listOffset < 0 || listOffset > tracks.size() - LINES) {
            throw new DisplayException("listOffset is out of bounds");
        }
        
        if (currentOffset + listOffset >= tracks.size()) {
            throw new DisplayException("currentOffset + listOffset is out of bounds");
        }
        
        this.listOffset = listOffset;
        this.currentOffset = currentOffset;
        
        this.repaint();
    }
    
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        Dimension size = getSize();
        
        // background
        int width = size.width;
        int height = size.height;
        
        g.setColor(scheme.getBackground());
        
        // list
        int lineHeight = height / LINES;

        Font font = new Font("Arial", Font.TRUETYPE_FONT, (int) (lineHeight * ( 1 - LINE_STRING_PADDING)));
        g.setFont(font);
        
        for (int line=0; line < LINES; line++) {
            int trackNumber = listOffset + line;
            g.setColor(scheme.getBackground());
            
            Color fontColor;
            Color bgColor;
            if (currentOffset == line) {
                fontColor = scheme.getBackground();
                bgColor = scheme.getForeground();
            } else {
                fontColor = scheme.getForeground();
                bgColor = scheme.getBackground();
            }
            
            Rectangle rect = new Rectangle(0, line * lineHeight, width, lineHeight);
            g.setClip(rect);
            
            // bg
            g.setColor(bgColor);
            g.fillRect((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
            
            String trackName;
            // text
            if (trackNumber < tracks.size()) {
                Track track = tracks.get(trackNumber);
                trackName = track.getCompleteName(" / ", DEFAULT_TRACK_NAME);
            } else {
                trackName = DEFAULT_TRACK_NAME;
            }
            
            g.setColor(fontColor);
            g.drawString((trackNumber+1) + ": " + trackName, (int) rect.getX(), (int) (rect.getY() + lineHeight * (1 - LINE_STRING_PADDING)));
            
        }
    }
    
    public void setTrack(int number, String author, String name)
    {
        tracks.set(number, new Track(author, name));
    }
    
    public void processMessage(CanMessage message) throws DisplayException
    {
        switch (message.getId()) {
            case CanComfort.ID_TRACK_LIST:
                byte[] data = message.getData();
                
                switch (data[0]) {
                    case 0x00:
                        if (data.length == 1) {
                            this.setVisible(false);
                        } else {
                            // close track list
                            String str = new String(Hex.encodeHex(data));
                            throw new DisplayException("Unexpected data `" + str + "`");
                        }
                        break;
                        
                    case 0x70:
                        // track list state data and possible track names
                        int tracksCount = (int)data[1] & 0xFF; 
                        boolean enableTrackList = (data[3] & 0xE0) == 0x40;
                        boolean trackListOffsetChanged = (data[3] & 0x10) == 0x10;
                        
                        int listOffset = (int)data[2] & 0xFF;
                        int currentOffset = data[3] & 0x0F;
                        
                        setTracksCount(tracksCount);
                        setOffsets(listOffset, currentOffset);
                        
                        // track names
                        int currentNamesOffset = 6;
                        for (int i=0; i<4; i++) {
                            int trackNamesFlag = (data[4] >>> ((LINES - i - 1) * 2)) & 0x03;
                            
                            if (trackNamesFlag != 0x00) {
                            
                                boolean authorExists = (trackNamesFlag & 0x02) == 0x02;
                                boolean nameExists = (trackNamesFlag & 0x01) == 0x01;
                            
                            
                                String trackAuthor = "";
                                if (authorExists) {
                                    // looking for first zero byte
                                    int lengthOfName = CanComfort.TRACK_LIST_TRACK_AUTHOR_LENGTH;
                                    for (int j=0; j<CanComfort.TRACK_LIST_TRACK_AUTHOR_LENGTH; j++) {
                                        if (data[currentNamesOffset + j] == 0x00) {
                                            lengthOfName = j;
                                            break;
                                        }
                                    }
                                    trackAuthor = new String(data, currentNamesOffset, lengthOfName, CanComfort.charset);
                                    currentNamesOffset += CanComfort.TRACK_LIST_TRACK_AUTHOR_LENGTH;
                                }
                                
                                String trackName = "";
                                if (nameExists) {
                                    // looking for first zero byte
                                    int lengthOfName = CanComfort.TRACK_LIST_TRACK_NAME_LENGTH;
                                    for (int j=0; j<CanComfort.TRACK_LIST_TRACK_NAME_LENGTH; j++) {
                                        if (data[currentNamesOffset + j] == 0x00) {
                                            lengthOfName = j;
                                            break;
                                        }
                                    }
                                    trackName = new String(data, currentNamesOffset, lengthOfName, CanComfort.charset);
                                    currentNamesOffset += CanComfort.TRACK_LIST_TRACK_NAME_LENGTH;
                                }
                                
                                int trackNumber;
                                if (trackListOffsetChanged) {
                                    if (currentOffset == 0) {
                                        trackNumber = listOffset + i;
                                    } else if (currentOffset == LINES-1) {
                                        trackNumber = listOffset + currentOffset - i;
                                    } else {
                                        throw new DisplayException("Unexpected state of list");
                                    }
                                } else {
                                    trackNumber = listOffset + i;
                                }
                                
                                this.setTrack(trackNumber, trackAuthor, trackName);
                            }
                        }
                        
                        if (enableTrackList) {
                            this.setVisible(true);
                            this.revalidate();
                        }
                        
                        break;
                        
                    default:
                        String str = new String(Hex.encodeHex(data));
                        throw new DisplayException("Unexpected data `" + str + "`");
                }
                
                break;
                
            default:
                throw new DisplayException("Unexpected message id `" + message.getId() + "`");
        }
    }
}
