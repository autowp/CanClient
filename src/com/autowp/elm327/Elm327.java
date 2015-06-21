package com.autowp.elm327;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import com.autowp.can.CanAdapter;
import com.autowp.can.CanAdapterException;
import com.autowp.can.CanFrame;
import com.autowp.can.CanFrameException;
import com.autowp.elm327.command.Command;
import com.autowp.elm327.command.DefaultsCommand;
import com.autowp.elm327.command.ResetCommand;
import com.autowp.elm327.command.SetHeaderCommand;
import com.autowp.elm327.command.TransmitCommand;
import com.autowp.elm327.response.Response;
import com.autowp.elm327.response.ResponseException;


public class Elm327 extends CanAdapter {
    protected SerialPort serialPort;
    protected String portName;
    protected int speed = 38400;
    
    private static final char COMMAND_DELIMITER = '\r';
    
    private List<CommandSendEventListener> commandSendListeners = new ArrayList<CommandSendEventListener>();
    
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
    
    protected class WaitForResponseReceivedEventClassListener implements ResponseReceivedEventClassListener {
        protected WaitForResponse waitForResponse;
        
        public WaitForResponseReceivedEventClassListener(WaitForResponse w)
        {
            waitForResponse = w;
        }
        
        @Override
        public void handleResponseReceivedEventClassEvent(ResponseReceivedEvent e) throws Elm327Exception {
            Response response = e.getCommand();
            if (waitForResponse.match(response)) {
                removeEventListener(this);
                waitForResponse.execute(response);
            }
        }
    }

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
    
    public synchronized Elm327 send(final Command c, final WaitForResponse w) throws Elm327Exception
    {
        final WaitForResponseReceivedEventClassListener listener = new WaitForResponseReceivedEventClassListener(w);
        this.addEventListener(listener);
        return this.send(c);
    }
    
    public synchronized Elm327 send(Command c) throws Elm327Exception
    {
        if (!this.isConnected()) {
            throw new Elm327Exception("ELM327 is not connected");
        }
        
        String command = c.toString() + "\n\r";
        
        System.out.println("Command: " + command);
        
        try {
            this.serialPort.writeString(command);
            
            fireCommandSendEvent(c);
            
        } catch (CanFrameException e) {
            throw new Elm327Exception("Can frame error: " + e.getMessage());
        } catch (SerialPortException e) {
            throw new Elm327Exception("Serial port error: " + e.getMessage());
        }
                
        return this;
    }

    @Override
    public void connect() throws CanAdapterException {
        if (this.isConnected()) {
            return;
        }
        
        try {
            
            this.serialPort = new SerialPort(this.portName);
            this.serialPort.openPort();
            this.serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
            this.serialPort.setParams(this.speed, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            serialPort.addEventListener(new SerialReader(), SerialPort.MASK_RXCHAR);
            
            
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
            
            //this.send(new EchoOnCommand());
            //this.send(new CanSilentMonitorCommand(false));
            this.send(new ResetCommand(), new WaitForResponse() {
                @Override
                public boolean match(Response response) {
                    System.out.println("Matching: " + response.toString());
                    System.out.println(response.toString().equals("ELM327 v1.5"));
                    System.out.println(response.toString().length());
                    System.out.println("ELM327 v1.5".length());
                    return response.toString().equals("ELM327 v1.5");
                }

                @Override
                public void execute(Response response) throws Elm327Exception {
                    System.out.println("execute");
                    send(new DefaultsCommand());
                }
            });
            this.send(new DefaultsCommand());
            /*
            this.send(new EchoOffCommand());
            this.send(new EchoOnCommand());
            this.send(new ProgParameterSetCommand(PP_CAN_ERROR_CHECKING, (byte)0x38));
            this.send(new ProgParameterOnCommand(PP_CAN_ERROR_CHECKING));*/
            
            /*byte options = PP_CAN_OPTIONS_ID_LENGTH_11 
                         | PP_CAN_OPTIONS_DATE_LENGTH_VARIABLE 
                         | PP_CAN_OPTIONS_RCV_ID_LENGTH_DEFAULT 
                         | PP_CAN_OPTIONS_BAUDRATE_MULTIPLIER_NONE
                         | PP_CAN_OPTIONS_DATA_FOMATTING_ISO15765_4;
            this.send(new ProgParameterSetCommand(PP_PROTOCOL_B_CAN_OPTIONS, options));
            this.send(new ProgParameterOnCommand(PP_PROTOCOL_B_CAN_OPTIONS));
            
            this.send(new ProgParameterSetCommand(PP_PROTOCOL_B_BAUDRATE_DIVISOR, divisor));
            this.send(new ProgParameterOnCommand(PP_PROTOCOL_B_BAUDRATE_DIVISOR));*/
            
            /*this.send(new SetProtocolCommand(SetProtocolCommand.USER1_CAN));
            
            this.send(new MonitorAllCommand());*/
            
        } catch (Elm327Exception | SerialPortException e) {
            this.serialPort = null;
            throw new CanAdapterException("Port error: " + e.getMessage());
        }
        
    }

    @Override
    public void disconnect() {
        if (this.serialPort != null) {
            try {
                this.serialPort.removeEventListener();
                this.serialPort.closePort();
            } catch (SerialPortException e) {
                e.printStackTrace();
                System.exit(-1);
            }

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
        Iterator<CommandSendEventListener> i = commandSendListeners.iterator();
        while(i.hasNext())  {
            ((CommandSendEventListener) i.next()).handleCommandSendEventClassEvent(event);
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
    
    private synchronized void fireResponseReceivedEvent(Response response) throws CanFrameException, Elm327Exception 
    {
        ResponseReceivedEvent event = new ResponseReceivedEvent(this, response);
        Iterator<ResponseReceivedEventClassListener> i = responseReceivedListeners.iterator();
        while (i.hasNext())  {
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
        private byte[] buffer = new byte[1024];
        private int bufferPos = 0;
        
        @Override
        public void serialEvent(SerialPortEvent event) {
            System.out.println("event");
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    byte[] bytes = serialPort.readBytes();
                    System.out.println(new String(bytes));
                    for (int i=0; i<bytes.length; i++) {
                        byte data = bytes[i];
                        char dataChar = (char)data;
                        if (dataChar != COMMAND_DELIMITER) {
                            buffer[bufferPos++] = data;
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
                } catch (SerialPortException | ResponseException | CanFrameException | Elm327Exception e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        }
    }

    public void addEventListener(CommandSendEventListener listener) {
        commandSendListeners.add(listener);
    }
}
