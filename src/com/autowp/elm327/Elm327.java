package com.autowp.elm327;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TooManyListenersException;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import com.autowp.canclient.CanAdapter;
import com.autowp.canclient.CanAdapterException;
import com.autowp.canclient.CanFrame;
import com.autowp.canclient.CanFrameException;
import com.autowp.elm327.command.Command;
import com.autowp.elm327.command.DefaultsCommand;
import com.autowp.elm327.command.EchoOffCommand;
import com.autowp.elm327.command.EchoOnCommand;
import com.autowp.elm327.command.MonitorAllCommand;
import com.autowp.elm327.command.ProgParameterOnCommand;
import com.autowp.elm327.command.ProgParameterSetCommand;
import com.autowp.elm327.command.ResetCommand;
import com.autowp.elm327.command.SetHeaderCommand;
import com.autowp.elm327.command.SetProtocolCommand;
import com.autowp.elm327.command.TransmitCommand;
import com.autowp.elm327.response.Response;


public class Elm327 extends CanAdapter {
    protected SerialPort serialPort;
    protected String portName;
    protected int speed = 38400;
    
    private static final char COMMAND_DELIMITER = '\r';
    
    private List<CommandSendEventClassListener> commandSendListeners = new ArrayList<CommandSendEventClassListener>();
    
    private List<ResponseReceivedEventClassListener> responseReceivedListeners = new ArrayList<ResponseReceivedEventClassListener>();
    
    public final byte PP_CAN_ERROR_CHECKING = 0x2A;
    public final byte PP_PROTOCOL_B_CAN_OPTIONS = 0x2C;
    public final byte PP_PROTOCOL_B_BAUDRATE_DIVISOR = 0x2D;
    
    public final byte PP_CAN_OPTIONS_ID_LENGTH_29 = 0x00;
    public final byte PP_CAN_OPTIONS_ID_LENGTH_11 = (byte) 0x80;
    
    public final byte PP_CAN_OPTIONS_DATE_LENGTH_8 = 0x00;
    public final byte PP_CAN_OPTIONS_DATE_LENGTH_VARIABLE = 0x40;
    
    public final byte PP_CAN_OPTIONS_RCV_ID_LENGTH_DEFAULT = 0x00;
    public final byte PP_CAN_OPTIONS_RCV_ID_LENGTH_BOTH = 0x20;
    
    public final byte PP_CAN_OPTIONS_BAUDRATE_MULTIPLIER_NONE = 0x00;
    public final byte PP_CAN_OPTIONS_BAUDRATE_MULTIPLIER_8_7 = 0x10;
    
    public final byte PP_CAN_OPTIONS_DATA_FOMATTING_NONE = 0x00;
    public final byte PP_CAN_OPTIONS_DATA_FOMATTING_ISO15765_4 = 0x01;
    public final byte PP_CAN_OPTIONS_DATA_FOMATTING_SAE_J1939 = 0x02;

    @Override
    public void send(CanFrame message) throws CanAdapterException {
        int id = message.getId();
        byte b0 = (byte)(id & 0x000F);
        byte b1 = (byte)((id & 0x00F0) >> 4);
        byte b2 = (byte)((id & 0x0F00) >> 8);
        
        try {
            this.send(new SetHeaderCommand(new byte[] {b0, b1, b2}));
            this.send(new TransmitCommand(message.getData()));
        } catch (Elm327Exception e) {
            throw new CanAdapterException("Elm327: " + e.getMessage());
        }
        
        this.fireFrameSentEvent(message);
    }
    
    public synchronized Elm327 send(Command c) throws Elm327Exception
    {
        if (!this.isConnected()) {
            throw new Elm327Exception("ELM327 is not connected");
        }
        
        String command = "AT" + c.toString() + COMMAND_DELIMITER;
        
        try {
            this.serialPort.getOutputStream().write(command.getBytes("ISO-8859-1"));
            this.serialPort.getOutputStream().flush();
            
            fireCommandSendEvent(c);
            
        } catch (IOException e) {
            throw new Elm327Exception("I/O error: " + e.getMessage());
        } catch (CanFrameException e) {
            throw new Elm327Exception("Can frame error: " + e.getMessage());
        }
                
        return this;
    }

    @Override
    public void connect() throws CanAdapterException {
        if (this.isConnected()) {
            return;
        }
        
        CommPortIdentifier portIdentifier;
        try {
            portIdentifier = CommPortIdentifier.getPortIdentifier(this.portName);
        
        
            CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
            
            if (!(commPort instanceof SerialPort)) {            
                throw new Elm327Exception(this.portName + " is not serial port");
            }
            
            this.serialPort = (SerialPort)commPort;
            
            this.serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
            this.serialPort.setSerialPortParams(this.speed, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            
            InputStream in = serialPort.getInputStream();
            
            serialPort.addEventListener(new SerialReader(in));
            serialPort.notifyOnDataAvailable(true);
            
            /*byte divisor = 0; // baudrate = 500 / divisor
            switch (this.specs.getSpeed()) {
                case 20:   divisor = 25; break;
                case 25:   divisor = 20; break;
                case 50:   divisor = 10; break;
                case 100:  divisor = 5; break;
                case 125:  divisor = 4; break;
                case 250:  divisor = 2; break;
                case 500:  divisor = 1; break;
                default:
                    throw new Elm327Exception("Unsupported bus speed");
            }*/
            
            this.send(new ResetCommand());
            this.send(new DefaultsCommand());
            this.send(new EchoOnCommand());
            this.send(new ProgParameterSetCommand(PP_CAN_ERROR_CHECKING, (byte)0x38));
            this.send(new ProgParameterOnCommand(PP_CAN_ERROR_CHECKING));
            
            /*byte options = PP_CAN_OPTIONS_ID_LENGTH_11 
                         | PP_CAN_OPTIONS_DATE_LENGTH_VARIABLE 
                         | PP_CAN_OPTIONS_RCV_ID_LENGTH_DEFAULT 
                         | PP_CAN_OPTIONS_BAUDRATE_MULTIPLIER_NONE
                         | PP_CAN_OPTIONS_DATA_FOMATTING_ISO15765_4;
            this.send(new ProgParameterSetCommand(PP_PROTOCOL_B_CAN_OPTIONS, options));
            this.send(new ProgParameterOnCommand(PP_PROTOCOL_B_CAN_OPTIONS));
            
            this.send(new ProgParameterSetCommand(PP_PROTOCOL_B_BAUDRATE_DIVISOR, divisor));
            this.send(new ProgParameterOnCommand(PP_PROTOCOL_B_BAUDRATE_DIVISOR));*/
            
            this.send(new SetProtocolCommand(SetProtocolCommand.USER1_CAN));
            
            this.send(new MonitorAllCommand());
            
        } catch (Elm327Exception | NoSuchPortException | PortInUseException | IOException | TooManyListenersException | UnsupportedCommOperationException e) {
            throw new CanAdapterException("Port error: " + e.getMessage());
        }
        
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
    
    public Elm327 setPortName(String portName)
    {
        this.portName = portName;
        
        return this;
    }
    
    public String getPortName()
    {
        return portName;
    }
    
    private synchronized void fireCommandSendEvent(Command command) throws CanFrameException
    {
        CommandSendEvent event = new CommandSendEvent(this, command);
        Iterator<CommandSendEventClassListener> i = commandSendListeners.iterator();
        while(i.hasNext())  {
            ((CommandSendEventClassListener) i.next()).handleCommandSendEventClassEvent(event);
        }
        
        /*if (command instanceof TransmitCommand) {
            TransmitCommand transmitCommand = (TransmitCommand)command;
            
            CanFrame frame = new CanFrame(transmitCommand.getId(), transmitCommand.getData());
            
            this.fireFrameSentEvent(frame);
        }*/
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
        
        /*if (response instanceof FrameResponse) {
            FrameResponse frameResponse = (FrameResponse)response;
            
            CanFrame frame = new CanFrame(frameResponse.getId(), frameResponse.getData());
            
            this.fireFrameReceivedEvent(frame);
        }*/
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
                        
                        if (dataChar == COMMAND_DELIMITER) {
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

    public void addEventListener(CommandSendEventClassListener listener) {
        commandSendListeners.add(listener);
    }
}
