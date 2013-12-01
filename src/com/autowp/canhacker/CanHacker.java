package com.autowp.canhacker;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TooManyListenersException;

import com.autowp.canclient.CanAdapter;
import com.autowp.canclient.CanAdapterException;
import com.autowp.canclient.CanFrame;
import com.autowp.canclient.CanFrameException;
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

public class CanHacker extends CanAdapter {
    protected SerialPort serialPort;
    protected String portName;
    protected int speed = 115200;
    
    /**
     * Gxx
     * Read register content from SJA1000 controller.
     * xx = Register to read (00-7F)
     * Return: Gdd[CR] 
     */
    public static final String G = "G";
    
    /**
     * Mxxxxxxxx[CR]
     * 
     * Set acceptance code register of SJA1000. This command works only if controller is setup with command “S” and in reset mode.
     * 
     * xxxxxxxx = Acceptance Code in hexadecimal, order ACR0 ACR1 ACR2 ACR3
     * Default value after power-up is 0x00000000 to receive all frames.
     * 
     * Return: [CR] or [BEL] 
     */
    public static final String M = "M";
    
    /**
     * mxxxxxxxx[CR]
     * 
     * Set acceptance mask register of SJA1000. This command works only if 
     * controller is setup with command “S” and in reset mode.
     * 
     * xxxxxxxx = Acceptance Mask in hexadecimal, order AMR0 AMR1 AMR2 AMR3
     * 
     * Default value after power-up is 0xFFFFFFFF to receive all frames.
     * 
     * Return [CR] or [BEL]
     * 
     * The acceptance filter is defined by the Acceptance Code Registers ACRn 
     * and the Acceptance Mask Registers AMRn. The bit patterns of messages 
     * to be received are defined within the acceptance code registers. 
     * The corresponding acceptance mask registers allow to define certain 
     * bit positions to be ‘don’t care’.
     * 
     * This device uses dual filter configuration.
     * For details of ACR and AMR usage see the SJA1000 datasheet. 
     */
    public static final String m = "m";
    
    /**
     * riiiL [CR]
     * 
     * This command transmits a standard remote 11 Bit CAN frame. 
     * It works only if controller is in operational mode after command “O”.
     * 
     * iii - Identifier in hexadecimal (000-7FF)
     * L   - Data length code (0-8)
     * 
     * Return: [CR] or [BEL] 
     */
    public static final String r = "r";
    
    /**
     * RiiiiiiiiL [CR]
     * 
     * This command transmits an extended remote 29 Bit CAN frame.
     * It works only if controller is in operational mode after command “O”.
     * 
     * iiiiiiii - Identifier in hexadecimal (00000000-1FFFFFFF) 
     * L        - Data length code (0-8)
     * 
     * Return: [CR] or [BEL] 
     */
    public static final String R = "R";

    /**
     * sxxyy[CR]
     * 
     * This command will set user defined values for the SJA1000 bit rate register BTR0 and BTR1.
     * It works only after power up or if controller is in reset mode after command “C”.
     * 
     * xx = hexadecimal value for BTR0 (00-FF)
     * yy = hexadecimal value for BTR1 (00-FF)
     * 
     * Return: [CR] or [BEL] 
     */
    public static final String s = "s";
    
    /**
     * TiiiiiiiiLDDDDDDDDDDDDDDDD[CR]
     * 
     * This command transmits an extended 29 Bit CAN frame. 
     * It works only if controller is in operational mode after command “O”.
     * 
     * iiiiiiii = Identifier in hexadecimal (00000000-1FFFFFFF)
     * L        = Data length code (0-8)
     * DD       = Data byte value in hexadecimal (00-FF). Number of given data bytes will be checked against given data length code.
     * 
     * Return: [CR] or [BEL] 
     */
    public static final String T = "T";
    
    /**
     * Wrrdd[CR]
     * 
     * Write SJA1000 register with data.
     * The data will be written to specified register without any check!
     * 
     * rr = Register number (00-7F)
     * dd = Data byte (00-FF)
     * 
     * Return: [CR] 
     */
    public static final String W = "W";
    
    private static final char COMMAND_DELIMITER = '\r';
    
    private static final char BELL = (char)0x07;
    
    private List<CommandSendEventClassListener> commandSendListeners = new ArrayList<CommandSendEventClassListener>();
    
    private List<ResponseReceivedEventClassListener> responseReceivedListeners = new ArrayList<ResponseReceivedEventClassListener>();
    
    public CanHacker()
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
                throw new CanHackerException(this.portName + " is not serial port");
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
                    throw new CanHackerException("Unsupported bus speed");
            }
            
            this.send(new ResetModeCommand());
            this.send(new BitRateCommand(busSpeed));
            this.send(new OperationalModeCommand());
        } catch (NoSuchPortException | PortInUseException | UnsupportedCommOperationException | IOException | TooManyListenersException e) {
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
    
    public CanHacker setPortName(String portName)
    {
        this.portName = portName;
        
        return this;
    }
    
    public String getPortName()
    {
        return portName;
    } 
    
    public CanHacker setSpeed(int speed)
    {
        this.speed = speed;
        
        return this;
    }
    
    public synchronized CanHacker send(Command c) throws CanHackerException
    {
        if (!this.isConnected()) {
            throw new CanHackerException("CanHacker is not connected");
        }
        
        String command = c.toString() + COMMAND_DELIMITER;
        
        try {
            this.serialPort.getOutputStream().write(command.getBytes("ISO-8859-1"));
            this.serialPort.getOutputStream().flush();
            
            fireCommandSendEvent(c);
            
        } catch (IOException e) {
            throw new CanHackerException("I/O error: " + e.getMessage());
        } catch (CanFrameException e) {
            throw new CanHackerException("Can frame error: " + e.getMessage());
        }
                
        return this;
    }
    
    public synchronized void addEventListener(CommandSendEventClassListener listener)
    {
        commandSendListeners.add(listener);
    }
    
    public synchronized void removeEventListener(CommandSendEventClassListener listener)
    {
        commandSendListeners.remove(listener);
    }
    
    private synchronized void fireCommandSendEvent(Command command) throws CanFrameException
    {
        CommandSendEvent event = new CommandSendEvent(this, command);
        Iterator<CommandSendEventClassListener> i = commandSendListeners.iterator();
        while(i.hasNext())  {
            ((CommandSendEventClassListener) i.next()).handleCommandSendEventClassEvent(event);
        }
        
        if (command instanceof TransmitCommand) {
            TransmitCommand transmitCommand = (TransmitCommand)command;
            
            CanFrame frame = new CanFrame(transmitCommand.getId(), transmitCommand.getData());
            
            this.fireFrameSentEvent(frame);
        }
    }
    
    public synchronized void addEventListener(ResponseReceivedEventClassListener listener) 
    {
        responseReceivedListeners.add(listener);
    }
    
    public synchronized void removeEventListener(ResponseReceivedEventClassListener listener)   
    {
        responseReceivedListeners.remove(listener);
    }
    
    private synchronized void fireResponseReceivedEvent(Response response) throws CanFrameException 
    {
        ResponseReceivedEvent event = new ResponseReceivedEvent(this, response);
        Iterator<ResponseReceivedEventClassListener> i = responseReceivedListeners.iterator();
        while(i.hasNext())  {
            ((ResponseReceivedEventClassListener) i.next()).handleResponseReceivedEventClassEvent(event);
        }
        
        if (response instanceof FrameResponse) {
            FrameResponse frameResponse = (FrameResponse)response;
            
            CanFrame frame = new CanFrame(frameResponse.getId(), frameResponse.getData());
            
            this.fireFrameReceivedEvent(frame);
        }
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

    @Override
    public void send(CanFrame message) throws CanHackerException
    {
        TransmitCommand command = new TransmitCommand(message.getId(), message.getData());

        this.send(command);
    }
    
    
}
