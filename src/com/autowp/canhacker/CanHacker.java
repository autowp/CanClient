package com.autowp.canhacker;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.autowp.canhacker.command.BitRateCommand;
import com.autowp.canhacker.command.Command;
import com.autowp.canhacker.command.FirmwareVersionCommand;
import com.autowp.canhacker.command.ListenOnlyModeCommand;
import com.autowp.canhacker.command.OperationalModeCommand;
import com.autowp.canhacker.command.ResetModeCommand;
import com.autowp.canhacker.command.VersionCommand;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class CanHacker {
    protected SerialPort serialPort;
    protected String portName;
    protected int speed = 57600;
    
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
    
    private List<CommandSendEventClassListener> commandSendListeners = new ArrayList<CommandSendEventClassListener>();
    
    private List<CommandReceivedEventClassListener> commandReceivedListeners = new ArrayList<CommandReceivedEventClassListener>();
    
    public CanHacker()
    {
        
    }
    
    public boolean connect() throws Exception
    {
        if (this.isConnected()) {
            return true;
        }
        
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(this.portName);
        
        CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
        
        if (!(commPort instanceof SerialPort)) {            
            throw new Exception(this.portName + " is not serial port");
        }
        
        System.out.println("Connect to port " + this.portName);
        
        this.serialPort = (SerialPort)commPort;
        this.serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
        this.serialPort.setRTS(true);
        this.serialPort.setSerialPortParams(this.speed, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        
        InputStream in = serialPort.getInputStream();
        
        //serialPort.addEventListener(new SerialReader(in, this));
        //serialPort.notifyOnDataAvailable(true);
        serialPort.getOutputStream().write("V\n".getBytes());
        
        (new Thread(new SerialReader2(in))).start();
        
        /*this.send(new ResetModeCommand());
        this.send(new BitRateCommand(BitRateCommand.BitRate.S4));
        this.send(new OperationalModeCommand());
        this.send(new ResetModeCommand());
        this.send(new ListenOnlyModeCommand());
        this.send(new VersionCommand());*/
        for (int i = 0; i<5; i++) {
        this.send(new FirmwareVersionCommand());
        this.send(new FirmwareVersionCommand());
        this.send(new FirmwareVersionCommand());
        this.send(new VersionCommand());
        }
        
        
        return this.isConnected();
    }
    
    /** */
    public static class SerialReader2 implements Runnable 
    {
        InputStream in;
        
        public SerialReader2 ( InputStream in )
        {
            System.out.println("init 2");
            this.in = in;
        }
        
        public void run ()
        {
            System.out.println("run");
            byte[] buffer = new byte[10];
            int len = -1;
            try
            {
                /*System.out.println("run2");
                System.out.println(this.in.read(buffer));*/
                System.out.println(buffer.length);
                while ( ( len = this.in.read(buffer)) > -1 )
                {
                    if (len > 0) {
                        System.out.println("!!!!!!!!!!!!!!");
                    } 
                    System.out.print(new String(buffer,0,len));
                }
                System.out.println("end of stream. close");
            }
            catch ( IOException e )
            {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }            
        }
    }
    
    public CanHacker disconnect()
    {
        if (this.serialPort != null) {
            this.serialPort.notifyOnDataAvailable(false);
            this.serialPort.removeEventListener();
            this.serialPort.close();
            
            this.serialPort = null;
        }
        
        return this;
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
    
    public CanHacker send(Command c) throws Exception
    {
        if (!this.isConnected()) {
            throw new Exception("CanHacker is not connected");
        }
        
        String command = c.toString() + "\n";
        
        System.out.print(command);
        
        this.serialPort.getOutputStream().write(command.getBytes());
        
        this.serialPort.getOutputStream().flush();
        
        fireCommandSendEvent(c);
                
        return this;
    }
    
    public synchronized void addEventListener(CommandSendEventClassListener listener)  {
        commandSendListeners.add(listener);
    }
    
    public synchronized void removeEventListener(CommandSendEventClassListener listener)   {
        commandSendListeners.remove(listener);
    }
    
    private synchronized void fireCommandSendEvent(Command command)
    {
        CommandSendEvent event = new CommandSendEvent(this, command);
        Iterator<CommandSendEventClassListener> i = commandSendListeners.iterator();
        while(i.hasNext())  {
            ((CommandSendEventClassListener) i.next()).handleCommandSendEventClassEvent(event);
        }
    }
    
    public synchronized void addEventListener(CommandReceivedEventClassListener listener)  {
        commandReceivedListeners.add(listener);
    }
    
    public synchronized void removeEventListener(CommandReceivedEventClassListener listener)   {
        commandReceivedListeners.remove(listener);
    }
    
    private synchronized void fireCommandReceivedEvent(String command)
    {
        CommandReceivedEvent event = new CommandReceivedEvent(this, command);
        Iterator<CommandReceivedEventClassListener> i = commandReceivedListeners.iterator();
        while(i.hasNext())  {
            ((CommandReceivedEventClassListener) i.next()).handleCommandReceivedEventClassEvent(event);
        }
    }
    
    public void notifyCommandReceived(String command) 
    {
        fireCommandReceivedEvent(command);
    }
    
    /**
     * Handles the input coming from the serial port. A new line character
     * is treated as the end of a block in this example. 
     */
    public static class SerialReader implements SerialPortEventListener 
    {
        private InputStream in;
        private CanHacker canHacker;
        private byte[] buffer = new byte[1024];
        
        public SerialReader (InputStream in, CanHacker canHacker)
        {
            System.out.println("SerialReader init");
            this.in = in;
            this.canHacker = canHacker;
        }
        
        public void serialEvent(SerialPortEvent arg0) {
            System.out.println("serialEvent");
            System.exit(-1);
            
            int data;
          
            try {
                int len = 0;
                while ( (( data = in.read()) > -1) && len <= 0 )
                {
                    if ( data == '\n' ) {
                        break;
                    }
                    buffer[len++] = (byte) data;
                }
                System.out.print(new String(buffer, 0, len));
                this.canHacker.notifyCommandReceived(new String(buffer, 0, len));
                
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }             
        }

    }
    
    
}
