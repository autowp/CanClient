package com.autowp.canhacker.command;

import static org.junit.Assert.*;

import org.junit.Test;

import com.autowp.canhacker.CanHackerException;

public class CommandsTest {
    
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    @Test
    public void test() {
        
        ArbitrationCaptureRegisterCommand c1 = new ArbitrationCaptureRegisterCommand();
        assertEquals("A", c1.toString());
        
        CanErrorCommand c6 = new CanErrorCommand();
        assertEquals("F", c6.toString());
        
        ErrorCaptureRegisterCommand c7 = new ErrorCaptureRegisterCommand();
        assertEquals("E", c7.toString());
        
        FirmwareVersionCommand c8 = new FirmwareVersionCommand();
        assertEquals("v", c8.toString());
        
        ListenOnlyModeCommand c9 = new ListenOnlyModeCommand();
        assertEquals("L", c9.toString());
        
        OperationalModeCommand c10 = new OperationalModeCommand();
        assertEquals("O", c10.toString());
        
        ResetModeCommand c11 = new ResetModeCommand();
        assertEquals("C", c11.toString());
        
        SerialNumberCommand c12 = new SerialNumberCommand();
        assertEquals("N", c12.toString());
        
        ToggleTimestampCommand c13 = new ToggleTimestampCommand();
        assertEquals("Z", c13.toString());
        
        VersionCommand c14 = new VersionCommand();
        assertEquals("V", c14.toString());
    }
    
    @Test
    public void bitRateTest() throws CanHackerException 
    {
        BitRateCommand c2 = new BitRateCommand(BitRateCommand.BitRate.S0);
        assertEquals("S0", c2.toString());
        
        BitRateCommand c3 = new BitRateCommand(BitRateCommand.BitRate.S1);
        assertEquals("S1", c3.toString());
        
        BitRateCommand c4 = new BitRateCommand(BitRateCommand.BitRate.S7);
        assertEquals("S7", c4.toString());
        
        BitRateCommand c5 = new BitRateCommand(BitRateCommand.BitRate.S8);
        assertEquals("S8", c5.toString()); 
    }
    
    @Test
    public void bitRateNullTest()
    {
        boolean thrown = false;
        try {
            new BitRateCommand(null);
        } catch (CanHackerException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }
    
    @Test
    public void transmitTest() throws CanHackerException 
    {
        TransmitCommand c1 = new TransmitCommand(0x123, hexStringToByteArray("1234567812345678"));
        assertEquals("t12381234567812345678", c1.toString());
        
        TransmitCommand c2 = new TransmitCommand(0x123, hexStringToByteArray("12345678123456"));
        assertEquals("t123712345678123456", c2.toString());
        
        TransmitCommand c4 = new TransmitCommand(0x123, hexStringToByteArray("123456781234"));
        assertEquals("t1236123456781234", c4.toString());
        
        TransmitCommand c5 = new TransmitCommand(0x123, hexStringToByteArray("1234567812"));
        assertEquals("t12351234567812", c5.toString());
        
        TransmitCommand c6 = new TransmitCommand(0x123, hexStringToByteArray("12345678"));
        assertEquals("t123412345678", c6.toString());
        
        TransmitCommand c7 = new TransmitCommand(0x123, hexStringToByteArray("123456"));
        assertEquals("t1233123456", c7.toString());
        
        TransmitCommand c8 = new TransmitCommand(0x123, hexStringToByteArray("1234"));
        assertEquals("t12321234", c8.toString());
        
        TransmitCommand c9 = new TransmitCommand(0x123, hexStringToByteArray("12"));
        assertEquals("t123112", c9.toString());
        
        TransmitCommand c10 = new TransmitCommand(0x123, hexStringToByteArray(""));
        assertEquals("t1230", c10.toString());
    }
    
    @Test
    public void transmitTooBigIdTest()
    {
        boolean thrown = false;
        try {
            new TransmitCommand(0xEFFF, hexStringToByteArray("1234567812345678"));
        } catch (CanHackerException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }
}
