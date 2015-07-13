package com.autowp.psa.display;

import java.awt.Color;

public abstract class Scheme {
    protected Color background;
    protected Color foreground;
    protected Color foregroundSecondary;
    
    public static class Default extends Scheme
    {
        public Default() {
            background = Color.ORANGE;
            foreground = Color.BLACK;
            foregroundSecondary = new Color(0x804000);
        }
    }
    
    public static class Inverse extends Scheme
    {
        public Inverse() {
            background = Color.BLACK;
            foreground = Color.ORANGE;
            foregroundSecondary = new Color(0x804000);
        }
    }
    
    public Color getBackground()
    {
        return background;
    }
    
    public Color getForeground()
    {
        return foreground;
    }
    
    public Color getForegroundSecondary()
    {
        return foregroundSecondary;
    }
    
    public static Scheme factory(String scheme)
    {
        Scheme result = null;
        switch (scheme) {
            case "inverse":
                result = new Inverse();
                break;
                
            case "default":
                result = new Default();
                break;
        }
        return result;
    }
}
