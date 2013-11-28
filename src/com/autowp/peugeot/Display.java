package com.autowp.peugeot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import java.awt.Component;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Display extends Component {
    
    private static final Color BACKGROUND = Color.BLACK;
    private static final Color FOREGROUND = Color.ORANGE;
    
    private TrackList trackList = new TrackList();
    
    
    
    public Display()
    {
        
    }
    
    public void paint(Graphics g) {
        Dimension size = getSize();
        
        // draw background
        g.setColor(BACKGROUND);
        g.fillRect(0, 0, size.width, size.height);
        
        trackList.draw(g);
        
        super.paint(g);
    }
    
    public TrackList getTrackList()
    {
        return trackList;
    }
    
    public class TrackList {
        
        private static final double WIDTH = 0.8;
        private static final double HEIGHT = 0.8;
        
        private static final int LINES = 4;
        private static final double LINE_STRING_PADDING = .15;
        
        private ArrayList<String> tracks = new ArrayList<String>();
        private int currentTrack = 4;
        
        public TrackList()
        {
            tracks.add("t.A.T.u. - All the things she said (edited)");
            tracks.add("Rammstein - Alter mann (special version)");
            tracks.add("Eminem - Bonnie & Clyde");
            tracks.add("Zaz - Ces petits riens");
            tracks.add("t.A.T.u. - All the things she said (edited)");
            tracks.add("Rammstein - Alter mann (special version)");
            tracks.add("Eminem - Bonnie & Clyde");
            tracks.add("Zaz - Ces petits riens");
            tracks.add("t.A.T.u. - All the things she said (edited)");
            tracks.add("Rammstein - Alter mann (special version)");
            tracks.add("Eminem - Bonnie & Clyde");
            tracks.add("Zaz - Ces petits riens");
        }
        
        public void next()
        {
            if (currentTrack < tracks.size() - 1) {
                currentTrack ++;
            }
        }
        
        public void prev()
        {
            if (currentTrack > 0) {
                currentTrack --;
            }
        }
        
        public void setCurrentTrack(int trackNumber)
        {
            currentTrack = trackNumber;
        }
        
        public void draw(Graphics g)
        {
            Dimension size = getSize();
            
            // background
            int width = (int) Math.round(size.width * WIDTH);
            int height = (int) Math.round(size.height * HEIGHT);
            
            int left = (size.width - width) / 2;
            int top = (size.height - height) / 2;
            
            g.setColor(FOREGROUND);
            g.fillRect(left, top, width, height);
            
            // list
            int lineHeight = height / LINES;
            int startFrom = (currentTrack+1) - LINES / 2;
            if (startFrom < 0) {
                startFrom = 0;
            }
            if (startFrom > tracks.size() - LINES) {
                startFrom = tracks.size() - LINES;
            }
            System.out.println(startFrom);
            System.out.println(currentTrack);
            Font font = new Font("Arial", Font.TRUETYPE_FONT, (int) (lineHeight * ( 1 - LINE_STRING_PADDING)));
            g.setFont(font);
            
            for (int line=0; line < LINES; line++) {
                int trackNumber = startFrom + line;
                g.setColor(BACKGROUND);
                
                Color fontColor;
                Color bgColor;
                if (currentTrack == trackNumber) {
                    fontColor = FOREGROUND;
                    bgColor = BACKGROUND;
                } else {
                    fontColor = BACKGROUND;
                    bgColor = FOREGROUND;
                }
                
                Rectangle rect = new Rectangle(left, top + line * lineHeight, width, lineHeight);
                g.setClip(rect);
                
                // bg
                g.setColor(bgColor);
                g.fillRect((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
                
                // text
                String text = trackNumber + ". " + tracks.get(trackNumber);
                g.setColor(fontColor);
                g.drawString(text, (int) rect.getX(), (int) (rect.getY() + lineHeight * (1 - LINE_STRING_PADDING)));
                
            }
        }
    }
}
