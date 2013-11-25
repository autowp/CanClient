package com.autowp.canhacker.response;

import static org.junit.Assert.*;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ResponsesTest {

    @Test
    public void bellTest() throws ResponseException, DecoderException {
        assertTrue(Response.fromBytes(new byte[] {0x07}) instanceof BellResponse);
        try {
            Response.fromBytes((((char)0x07) + "A").getBytes()); fail();
        } catch (Exception e) {
            assertTrue(true);
        }
        
        try {
            Response.fromBytes("xxx".getBytes()); fail();
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void versionTest()
    {
        try {
            Response.fromBytes("V".getBytes()); fail();
        } catch (Exception e) {
            assertTrue(true);
        }
        
        try {
            Response.fromBytes("V0".getBytes()); fail();
        } catch (Exception e) {
            assertTrue(true);
        }
        
        try {
            Response.fromBytes("V01".getBytes()); fail();
        } catch (Exception e) {
            assertTrue(true);
        }
        
        try {
            Response.fromBytes("V012".getBytes()); fail();
        } catch (Exception e) {
            assertTrue(true);
        }
        
        try {
            Response.fromBytes("V01234".getBytes()); fail();
        } catch (Exception e) {
            assertTrue(true);
        }
        
        
        try {
            Response r = Response.fromBytes("V0123".getBytes());
            assertTrue(r instanceof VersionResponse);
            assertEquals("V0123", r.toString());
            VersionResponse v = (VersionResponse)r;
            assertEquals("0123", v.getVersion());
        } catch (Exception e) {
            fail();
        }
    }
    
    @Test
    public void canErrorTest() throws ResponseException, DecoderException
    {
        assertTrue(Response.fromBytes("F00".getBytes()) instanceof CanErrorResponse);
        
        try {
            Response.fromBytes("F012".getBytes()); fail();
        } catch (Exception e) {
            assertTrue(true);
        }
        
        try {
            Response.fromBytes("F0123".getBytes()); fail();
        } catch (Exception e) {
            assertTrue(true);
        }
        
        try {
            Response.fromBytes("F0".getBytes()); fail();
        } catch (Exception e) {
            assertTrue(true);
        }
        
        try {
            Response.fromBytes("F".getBytes()); fail();
        } catch (Exception e) {
            assertTrue(true);
        }
        
        try {
            Response r = Response.fromBytes("FFF".getBytes());
            assertTrue(r instanceof CanErrorResponse);
            assertEquals("FFF", r.toString());
            CanErrorResponse v = (CanErrorResponse)r;
            assertEquals(255, v.getErrorCode());
        } catch (Exception e) {
            fail();
        }
        
        try {
            Response r = Response.fromBytes("F00".getBytes());
            assertTrue(r instanceof CanErrorResponse);
            assertEquals("F00", r.toString());
            CanErrorResponse v = (CanErrorResponse)r;
            assertEquals(0, v.getErrorCode());
        } catch (Exception e) {
            fail();
        }
        
        try {
            Response r = Response.fromBytes("F65".getBytes());
            assertTrue(r instanceof CanErrorResponse);
            assertEquals("F65", r.toString());
            CanErrorResponse v = (CanErrorResponse)r;
            assertEquals(101, v.getErrorCode());
        } catch (Exception e) {
            fail();
        }
    }
    
    @Test
    public void firmwareVersionTest() throws ResponseException, DecoderException
    {
        assertTrue(Response.fromBytes("v0000".getBytes()) instanceof FirmwareVersionResponse);
        
        try {
            Response r = Response.fromBytes("v0000".getBytes());
            assertTrue(r instanceof FirmwareVersionResponse);
            assertEquals("v0000", r.toString());
            FirmwareVersionResponse v = (FirmwareVersionResponse)r;
            assertEquals("0000", v.getVersion());
        } catch (Exception e) {
            fail();
        }
        
        try {
            Response r = Response.fromBytes("v0001".getBytes());
            assertTrue(r instanceof FirmwareVersionResponse);
            assertEquals("v0001", r.toString());
            FirmwareVersionResponse v = (FirmwareVersionResponse)r;
            assertEquals("0001", v.getVersion());
        } catch (Exception e) {
            fail();
        }
        
        try {
            Response r = Response.fromBytes("vFFFF".getBytes());
            assertTrue(r instanceof FirmwareVersionResponse);
            assertEquals("vFFFF", r.toString());
            FirmwareVersionResponse v = (FirmwareVersionResponse)r;
            assertEquals("FFFF", v.getVersion());
        } catch (Exception e) {
            fail();
        }
    }
    
    @Test
    public void frameTest() throws ResponseException, DecoderException
    {
        assertTrue(Response.fromBytes("t12351234512345".getBytes()) instanceof FrameResponse);
        
        try {
            Response.fromBytes("t82351234512345".getBytes()); fail();
        } catch (ResponseException e) {
            assertTrue(true);
        }
        
        try {
            Response.fromBytes("t12341234512345".getBytes()); fail();
        } catch (ResponseException e) {
            assertTrue(true);
        }
        
        try {
            Response.fromBytes("t12361234512345".getBytes()); fail();
        } catch (ResponseException e) {
            assertTrue(true);
        }
        
        try {
            Response r = Response.fromBytes("T12361234512345".getBytes());
            if (r instanceof FrameResponse) {
                fail();
            }
        } catch (ResponseException e) {
            assertTrue(true);
        }
        
        try {
            new FrameResponse("T12361234512345".getBytes()); fail();
        } catch (ResponseException e) {
            assertTrue(true);
        }
        
        try {
            Response r = Response.fromBytes("t1230".getBytes());
            assertTrue(r instanceof FrameResponse);
            assertEquals("t1230", r.toString());
            FrameResponse v = (FrameResponse)r;
            assertEquals(0x123, v.getId());
        } catch (Exception e) {
            fail();
        }
        
        try {
            Response r = Response.fromBytes("t123112".getBytes());
            assertTrue(r instanceof FrameResponse);
            assertEquals("t123112", r.toString());
            FrameResponse v = (FrameResponse)r;
            assertEquals(0x123, v.getId());
        } catch (Exception e) {
            fail();
        }
        
        try {
            Response r = Response.fromBytes("t12321234".getBytes());
            assertTrue(r instanceof FrameResponse);
            assertEquals("t12321234", r.toString());
            FrameResponse v = (FrameResponse)r;
            assertEquals(0x123, v.getId());
        } catch (Exception e) {
            fail();
        }
        
        try {
            Response r = Response.fromBytes("t1233123451".getBytes());
            assertTrue(r instanceof FrameResponse);
            assertEquals("t1233123451", r.toString());
            FrameResponse v = (FrameResponse)r;
            assertEquals(0x123, v.getId());
        } catch (Exception e) {
            fail();
        }
        
        try {
            Response r = Response.fromBytes("t123412345123".getBytes());
            assertTrue(r instanceof FrameResponse);
            assertEquals("t123412345123", r.toString());
            FrameResponse v = (FrameResponse)r;
            assertEquals(0x123, v.getId());
        } catch (Exception e) {
            fail();
        }
        
        try {
            Response r = Response.fromBytes("t12351234512345".getBytes());
            assertTrue(r instanceof FrameResponse);
            assertEquals("t12351234512345", r.toString());
            FrameResponse v = (FrameResponse)r;
            assertEquals(0x123, v.getId());
        } catch (Exception e) {
            fail();
        }
        
        
    }
}
