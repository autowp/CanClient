package com.autowp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.autowp.canclient.CanFrame;

public class CanFilter {
    
    public enum Mode { MATCH, NOT_MATCH };
    
    protected List<Integer> list = new ArrayList<>();
    
    protected Mode mode;
    
    public CanFilter()
    {
        
    }
    
    public CanFilter setMode(Mode mode)
    {
        this.mode = mode;
        
        return this;
    }
    
    public CanFilter add(int id)
    {
        if (list.indexOf(id) == -1) {
            list.add(id);
        }
        
        return this;
    }
    
    public CanFilter add(CanFrame frame)
    {
        return this.add(frame.getId());
    }
    
    public CanFilter remove(CanFrame frame)
    {
        int id = frame.getId();
        int index;
        while ((index = list.indexOf(id)) != -1) {
            list.remove(index);
        }
        
        return this;
    }
    
    public CanFilter clear()
    {
        list.clear();
        
        return this;
    }
    
    public boolean match(CanFrame frame)
    {
        boolean result = false;
        
        if (mode != null) {
            boolean found = list.indexOf(frame.getId()) != -1; 
            
            switch (mode) {
                case MATCH:
                    result = found;
                    break;
                    
                case NOT_MATCH:
                    result = !found;
                    break;
            }
        }
        
        return result;
    }
    
    public void readFromStream(FileInputStream in) throws Exception
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        
        String line = br.readLine();
        if (line == null) {
            throw new Exception("Match line not found");
        }
        
        switch (line) {
            case "match":
                this.setMode(Mode.MATCH);
                break;
                
            case "notmatch":
                this.setMode(Mode.NOT_MATCH);
                break;
                
            default:
                throw new Exception("Match line not valid");
        }
        
        this.clear();
        while ((line = br.readLine()) != null) {
            int id = Integer.parseInt(line, 16);
            this.add(id);
        }
    }

    public void writeToStream(FileOutputStream out) throws IOException
    {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
        
        switch (mode) {
            case MATCH:
                bw.write("match\n");
                break;
                
            case NOT_MATCH:
                bw.write("notmatch\n");
                break;
        }
        
        for (Integer item: list) {
            Object line = StringUtils.leftPad(Integer.toHexString(item), 3, '0');
            bw.write(line + "\n");
        }
    }
}
