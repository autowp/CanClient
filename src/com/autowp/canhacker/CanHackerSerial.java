package com.autowp.canhacker;

import java.io.IOException;
import java.io.InputStream;
import java.util.TooManyListenersException;

import com.autowp.can.CanAdapterException;
import com.autowp.can.CanFrameException;
import com.autowp.canhacker.command.*;
import com.autowp.canhacker.command.BitRateCommand.BitRate;
import com.autowp.canhacker.response.*;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

public class CanHackerSerial extends CanHacker {
    protected SerialPort serialPort;
    protected String portName;
    protected int speed = 115200;
    
    public CanHackerSerial()
    {
        
    }
    
    public void connect() throws CanAdapterException
    {
        if (this.isConnected()) {
            return;
        }
        
        CommPortIdentifier portIdentifier;
        try {
            portIdentifier = CommPortIdentifier.getPortIdentifier(this.portName);
        
        
            CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
            
            if (!(commPort instanceof SerialPort)) {            
                throw new CanHackerSerialException(this.portName + " is not serial port");
            }
            
            this.serialPort = (SerialPort)commPort;
            this.serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
            this.serialPort.setSerialPortParams(this.speed, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            
            InputStream in = serialPort.getInputStream();
            
            serialPort.addEventListener(new SerialReader(in));
            serialPort.notifyOnDataAvailable(true);
            
            BitRate busSpeed;
            switch (this.specs.getSpeed()) {
                case 10:   busSpeed = BitRate.S0; break;
                case 20:   busSpeed = BitRate.S1; break;
                case 50:   busSpeed = BitRate.S2; break;
                case 100:  busSpeed = BitRate.S3; break;
                case 125:  busSpeed = BitRate.S4; break;
                case 250:  busSpeed = BitRate.S5; break;
                case 500:  busSpeed = BitRate.S6; break;
                case 800:  busSpeed = BitRate.S7; break;
                case 1000: busSpeed = BitRate.S8; break;
                default:
                    throw new CanHackerSerialException("Unsupported bus speed");
            }
            
            this.send(new ResetModeCommand());
            this.send(new BitRateCommand(busSpeed));
            this.send(new OperationalModeCommand());
        } catch (NoSuchPortException | PortInUseException | UnsupportedCommOperationException | IOException | TooManyListenersException | CommandException e) {
            throw new CanAdapterException("Port error: " + e.getMessage());
        }
    }
    
    public void disconnect()
    {
        if (this.serialPort != null) {
            this.serialPort.notifyOnDataAvailable(false);
            this.serialPort.removeEventListener();
            this.serialPort.close();
            
            this.serialPort = null;
        }
    }
    
    public boolean isConnected()
    {
        return this.serialPort != null;
    }
    
    public CanHackerSerial setPortName(String portName)
    {
        this.portName = portName;
        
        return this;
    }
    
    public String getPortName()
    {
        return portName;
    }
    
    public CanHackerSerial setSpeed(int speed)
    {
        this.speed = speed;
        
        return this;
    }
    
    public synchronized CanHackerSerial send(Command c) throws CanHackerSerialException
    {
        if (!this.isConnected()) {
            throw new CanHackerSerialException("CanHacker is not connected");
        }
        
        String command = c.toString() + COMMAND_DELIMITER;
        
        try {
            this.serialPort.getOutputStream().write(command.getBytes("ISO-8859-1"));
            this.serialPort.getOutputStream().flush();
            
            fireCommandSendEvent(c);
            
        } catch (IOException e) {
            throw new CanHackerSerialException("I/O error: " + e.getMessage());
        } catch (CanFrameException e) {
            throw new CanHackerSerialException("Can frame error: " + e.getMessage());
        }
                
        return this;
    }
    
    
    
    /**
     * Handles the input coming from the serial port. A new line character
     * is treated as the end of a block in this example. 
     */
    private class SerialReader implements SerialPortEventListener 
    {
        private InputStream in;
        private byte[] buffer = new byte[1024];
        private int bufferPos = 0;
        
        public SerialReader (InputStream in)
        {
            this.in = in;
        }
        
        public void serialEvent(SerialPortEvent evt) {
            if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                
                try {
                    int data;
                   
                    while ((data = in.read()) > -1) {
                        char dataChar = (char)data;
                        if (dataChar != COMMAND_DELIMITER) {
                            buffer[bufferPos++] = (byte)data;
                        }
                        
                        if (dataChar == COMMAND_DELIMITER || dataChar == BELL) {
                            if (bufferPos > 0) {
                                byte[] commandBytes = new byte[bufferPos];
                                System.arraycopy(buffer, 0, commandBytes, 0, bufferPos);
                                Response response = Response.fromBytes(commandBytes);
                                fireResponseReceivedEvent(response);
                            }
                            bufferPos = 0;
                        }
                        
                        byte[] commandBytes = new byte[bufferPos];
                        System.arraycopy(buffer, 0, commandBytes, 0, bufferPos);
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(-1);
                }             
            }
        }

    }

    
    
    
}
