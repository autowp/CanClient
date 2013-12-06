package com.autowp.peugeot.display;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

import org.apache.commons.codec.binary.Hex;

import com.autowp.canclient.CanMessage;
import com.autowp.peugeot.CanComfort;


@SuppressWarnings("serial")
public class Volume extends JPanel {
    private Scheme scheme = null;
    
    private static final int MIN_VOLUME = 0;
    private static final int MAX_VOLUME = 30;
    
    private static final int HIDE_DELAY = 2500; // ms
    
    private static final int COLUMNS = 16;
    private static final double COLUMNS_DUTY = 0.8;
    
    private static final double PADDING = 0.2;
    
    private TimerTask hideTimerTask = null;
    
    private Timer timer = new Timer();
    
    protected int volume = 0;
    
    protected double[] volumeGrid = new double[] {
        0, 1, 2, 3, 4, 5, 6, 6.5, 7, 7.5, 8, 8.5, 9, 9.5, // 13
        10, 10.5, 11, 11.5, 12, 12.5, 13, 13.5, 14, // 22
        14.25, 14.5, 14.75, 15, 15.25, 15.5, 15.75, 16 // 30
    };
    
    public Volume(Scheme scheme)
    {
        this.setPreferredSize(new Dimension(400, 100));
        this.setMinimumSize(new Dimension(200, 50));
        this.setMaximumSize(new Dimension(800, 200));
        this.scheme = scheme;
        
        this.setBackground(scheme.getBackground());
        this.setForeground(scheme.getForeground());
    }
    
    public void processMessage(CanMessage message) throws DisplayException
    {
        switch (message.getId()) {
            case CanComfort.ID_VOLUME:
                byte[] data = message.getData();
                if (data.length != 1) {
                    String str = new String(Hex.encodeHex(data));
                    throw new DisplayException("Unexpected length of volume message `" + str + "`");
                }
                int volume = (int)data[0] & 0x1F;
                boolean show = ((int)data[0] & 0xE0) == 0x00;
                
                if (volume < MIN_VOLUME) {
                    String str = new String(Hex.encodeHex(data));
                    throw new DisplayException("Volume cannot be < " + MIN_VOLUME + " `" + str + "`");
                }
                
                if (volume > MAX_VOLUME) {
                    String str = new String(Hex.encodeHex(data));
                    throw new DisplayException("Volume cannot be > " + MAX_VOLUME + " `" + str + "`");
                }
                
                boolean oldVisible = this.isVisible();
                
                if (show) {
                    this.setVisible(true);
                    
                    if (hideTimerTask != null) {
                        hideTimerTask.cancel();
                        hideTimerTask = null;
                    }
                    hideTimerTask = new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                setVisible(false);
                            } catch (Exception e) {
                                e.printStackTrace(System.out);
                            }
                        }
                    };
                    timer.schedule(hideTimerTask, HIDE_DELAY);
                }
                
                if (oldVisible != show) {
                    this.invalidate();
                }
                
                this.setVolume(volume);
                break;
        }
    }
    
    public void setVolume(int volume)
    {
        if (this.volume != volume) {
            this.volume = volume;
            this.invalidate();
            this.repaint();
        }
    }
    
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        Dimension size = getSize();
        
        // background
        int width = size.width;
        int height = size.height;
        
        double paddingPixelsH = width * PADDING;
        double paddingPixelsV = height * PADDING;
        
        g.setColor(scheme.getBackground());
        g.fillRect(0, 0, width, height);
        
        Rectangle rect = new Rectangle(
            (int)paddingPixelsH, (int)paddingPixelsV, 
            (int)(width-paddingPixelsH*2), (int)(height-paddingPixelsV*2)
        );
        
        g.setColor(scheme.getForeground());
        g.drawRect(rect.x, rect.y, rect.width-1, rect.height);
        
        //double perColumn = rect.getWidth() / COLUMNS;
        //int columnWidth = (int)Math.round(perColumn * COLUMNS_DUTY);
        double columnWidth = COLUMNS_DUTY * rect.getWidth() / ( COLUMNS + COLUMNS_DUTY - 1);
        double gutterWidth = columnWidth * (1-COLUMNS_DUTY)/COLUMNS_DUTY;
        int columnWidthRounded = (int) Math.round(columnWidth);
        
        int columnBottom = rect.y + rect.height;
        double perColumnHeight = rect.getHeight() / COLUMNS;
        
        for (int i=0; i<COLUMNS; i++) {
            int columnHeight = (int)Math.round(perColumnHeight * (i+1));
            int columnLeft = (int)Math.round(rect.x + columnWidth * i + gutterWidth * (i));
            int columnTop = columnBottom - columnHeight;
            
            g.setColor(scheme.getForegroundSecondary());
            g.fillRect(
                columnLeft, columnTop, 
                columnWidthRounded, columnHeight
            );
            
            double gridValue = volumeGrid[volume];
            
            if (i <= gridValue) {
                double fill = gridValue - i;
                g.setColor(scheme.getForeground());
                if (fill <= 1) {
                    g.fillRect(
                        columnLeft, columnTop, 
                        (int) Math.round(columnWidth * fill), columnHeight
                    );
                } else {
                    g.fillRect(
                        columnLeft, columnTop, 
                        columnWidthRounded, columnHeight
                    );
                }
            }
        }
    }
}
