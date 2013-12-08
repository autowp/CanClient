package com.autowp.peugeot.display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JPanel;


@SuppressWarnings("serial")
public class VolumeGridPanel extends JPanel {
    private static final int COLUMNS = 16;
    private static final double COLUMNS_DUTY = 0.8;
    
    private static final double PADDING = 0;
    
    protected int volume = 0;
    
    private Color foregroundSecondary = Color.RED;
    
    protected double[] volumeGrid = new double[] {
        0, 1, 2, 3, 4, 5, 6, 6.5, 7, 7.5, 8, 8.5, 9, 9.5, // 13
        10, 10.5, 11, 11.5, 12, 12.5, 13, 13.5, 14, // 22
        14.25, 14.5, 14.75, 15, 15.25, 15.5, 15.75, 16 // 30
    };
    
    public VolumeGridPanel()
    {
        this.setPreferredSize(new Dimension(400, 100));
        this.setMinimumSize(new Dimension(200, 50));
        this.setMaximumSize(new Dimension(800, 200));
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
        
        g.setColor(this.getBackground());
        g.fillRect(0, 0, width, height);
        
        Rectangle rect = new Rectangle(
            (int)paddingPixelsH, (int)paddingPixelsV, 
            (int)(width-paddingPixelsH*2), (int)(height-paddingPixelsV*2)
        );
        
        g.setColor(this.getForeground());
        
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
            
            g.setColor(this.getForegroundSecondary());
            g.fillRect(
                columnLeft, columnTop, 
                columnWidthRounded, columnHeight
            );
            
            double gridValue = volumeGrid[volume];
            
            if (i <= gridValue) {
                double fill = gridValue - i;
                g.setColor(this.getForeground());
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
    
    public Color getForegroundSecondary()
    {
        return foregroundSecondary;
    }
    
    public void setForegroundSecondary(Color color)
    {
        foregroundSecondary = color;
    }
    
    public void setScheme(Scheme scheme) {
        Color foreground = scheme.getForeground();
        Color background = scheme.getBackground();
        
        this.setForeground(foreground);
        this.setBackground(background);
        this.setForegroundSecondary(scheme.getForegroundSecondary());
    }
}
