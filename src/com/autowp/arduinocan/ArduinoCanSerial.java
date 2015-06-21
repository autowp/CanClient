package com.autowp.arduinocan;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

import com.autowp.can.CanAdapterException;
import com.autowp.can.CanFrame;

public class ArduinoCanSerial extends ArduinoCan {
    protected SerialPort serialPort;
    protected String portName;

    @Override
    public void send(CanFrame frame) throws CanAdapterException {
        if (!this.isConnected()) {
            throw new ArduinoCanSerialException("ArduinoCanSerial is not connected");
        }
        
        byte[] data = buildArduinoCanMessage(frame);
        byte line[] = new byte[data.length + 1];
        System.arraycopy(data, 0, line, 0, data.length);
        line[line.length - 1] = 0x0D;
        
        try {
            OutputStream output = this.serialPort.getOutputStream();
            
            output.write(line, 0, line.length);
            output.flush();
            
            this.fireFrameSentEvent(frame);
            
        } catch (IOException e) {
            throw new ArduinoCanSerialException("I/O error: " + e.getMessage());
        }
    }

    @Override
    public void connect() throws CanAdapterException {
        if (this.isConnected()) {
            return;
        }
        
        try {
            
            CommPortIdentifier portIdentifier;
            portIdentifier = CommPortIdentifier.getPortIdentifier(this.portName);
            
            CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
            
            if (!(commPort instanceof SerialPort)) {
                throw new ArduinoCanSerialException(this.portName + " is not serial port");
            }
            
            this.serialPort = (SerialPort)commPort;
            
            this.serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
            
            this.serialPort.setSerialPortParams(BAUDRATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            
            InputStream in = serialPort.getInputStream();
            
            serialPort.addEventListener(new SerialReader(in));
            
        } catch (UnsupportedCommOperationException | TooManyListenersException | IOException e) {
            throw new CanAdapterException(e.getMessage());
        } catch (NoSuchPortException e) {
            throw new CanAdapterException("No such port");
        } catch (PortInUseException e) {
            throw new CanAdapterException("Port in use: " + e.getMessage());
        }
        
        serialPort.notifyOnDataAvailable(true);

    }

    @Override
    public void disconnect() {
        if (this.serialPort != null) {
            this.serialPort.notifyOnDataAvailable(false);
            this.serialPort.removeEventListener();
            this.serialPort.close();
            
            this.serialPort = null;
        }
    }

    @Override
    public boolean isConnected() {
        return this.serialPort != null;
    }
    
    public ArduinoCanSerial setPortName(String portName)
    {
        this.portName = portName;
        
        return this;
    }
    
    public String getPortName()
    {
        return portName;
    }

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
                        if (dataChar != '\n' && dataChar != '\r') {
                            buffer[bufferPos++] = (byte)data;
                        } else {
                            if (bufferPos > 0) {
                                byte[] commandBytes = new byte[bufferPos];
                                System.arraycopy(buffer, 0, commandBytes, 0, bufferPos);
                                
                                try {
                                    processInputLine(commandBytes);
                                } catch (ArduinoCanException e) {
                                    fireErrorEvent(e);
                                }
                                
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
