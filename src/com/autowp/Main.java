package com.autowp;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;

import java.awt.EventQueue;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.Vector;

import com.autowp.canclient.CanClient;
import com.autowp.canclient.FrameReceivedEvent;
import com.autowp.canclient.FrameReceivedEventClassListener;
import com.autowp.canclient.FrameSentEvent;
import com.autowp.canclient.FrameSentEventClassListener;
import com.autowp.canhacker.CanHacker;
import com.autowp.canhacker.ResponseReceivedEvent;
import com.autowp.canhacker.ResponseReceivedEventClassListener;
import com.autowp.canhacker.CommandSendEvent;
import com.autowp.canhacker.CommandSendEventClassListener;
import com.autowp.peugeot.CanComfort;

import javax.swing.JFrame;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextArea;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JToolBar;
import javax.swing.JTextField;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Main {

    private JFrame frame;
    
    private CanClient client;
    
    private JTextArea canhackerLogTextArea;
    private CanTable canSentTable;
    private CanTable canReceiveTable;
    
    private JComboBox<String> portNameBox;
    private final Action connectAction = new ConnectAction();
    private final Action disconnectAction = new DisconnectAction();
    private final Action exitAction = new ExitAction();
    private JTextField vinTextField;
    private JTextField textField;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Main window = new Main();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public Main() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        disconnectAction.setEnabled(false);
        frame = new JFrame();
        frame.setBounds(100, 100, 487, 623);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
        Vector<String> comboBoxItems = getAvailableSerialPorts();
        
        final DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>(comboBoxItems);
        model.setSelectedItem("COM3");
        
        this.client = new CanClient();
        
        JToolBar toolBar = new JToolBar();
        portNameBox = new JComboBox<String>(model);
        toolBar.add(portNameBox);
        
        JButton connectBtn = new JButton(connectAction);
        toolBar.add(connectBtn);
        
        JButton disconnectBtn = new JButton(disconnectAction);
        toolBar.add(disconnectBtn);
        
        vinTextField = new JTextField();
        vinTextField.setToolTipText("VIN");
        vinTextField.setText("21496464");
        toolBar.add(vinTextField);
        vinTextField.setColumns(10);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));
        frame.getContentPane().add(toolBar, BorderLayout.NORTH);
        
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
        
        canhackerLogTextArea = new JTextArea();
        canhackerLogTextArea.setEditable(false);
        
        JScrollPane canHackerScrollPane = new JScrollPane(canhackerLogTextArea);
        tabbedPane.addTab("CanHacker", null, canHackerScrollPane, null);
        
        
        
        canSentTable = new CanTable();
        canSentTable.setEnabled(true);
        JScrollPane canSentScrollPane = new JScrollPane(canSentTable);
        tabbedPane.addTab("CAN sent", null, canSentScrollPane, null);
        
        canReceiveTable = new CanTable();
        canReceiveTable.setEnabled(true);
        JScrollPane canReceiveScrollPane = new JScrollPane(canReceiveTable);
        tabbedPane.addTab("CAN received", null, canReceiveScrollPane, null);
        
        JPanel monitor = new JPanel();
        tabbedPane.addTab("Display", null, monitor, null);
        
        textField = new JTextField();
        textField.setEditable(false);
        monitor.add(textField);
        textField.setColumns(10);
        
        
        

        
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        JMenu mnNewMenu = new JMenu("File");
        menuBar.add(mnNewMenu);

        JMenuItem mntmConnect = new JMenuItem(connectAction);
        mnNewMenu.add(mntmConnect);
        
        JMenuItem mntmDisconnect = new JMenuItem(disconnectAction);
        mnNewMenu.add(mntmDisconnect);
        
        JMenuItem mntmExit = new JMenuItem(exitAction);
        mnNewMenu.add(mntmExit);

        /*
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                AbstractButton abstractButton = (JToggleButton) actionEvent.getSource();
                if (abstractButton.isSelected()) {
                    connect();
                    abstractButton.setSelected(client.isConnected());
                } else {
                    disconnect();
                }
            }
        });
        */
    }
    
    protected void logCanhacker(String str)
    {
        canhackerLogTextArea.append(str + "\n");
    }
    
    public static Vector<String> getAvailableSerialPorts() {
        Vector<String> h = new Vector<String>();
        //HashSet<CommPortIdentifier> h = new HashSet<CommPortIdentifier>();
        Enumeration thePorts = CommPortIdentifier.getPortIdentifiers();
        while (thePorts.hasMoreElements()) {
            CommPortIdentifier com = (CommPortIdentifier) thePorts.nextElement();
            switch (com.getPortType()) {
            case CommPortIdentifier.PORT_SERIAL:
                try {
                    CommPort thePort = com.open("CommUtil", 50);
                    thePort.close();
                    h.add(com.getName());
                } catch (PortInUseException e) {
                    System.err.println("Port, "  + com.getName() + ", is in use.");
                } catch (Exception e) {
                    System.err.println("Failed to open port " +  com.getName());
                    e.printStackTrace();
                }
            }
        }
        return h;
    }
    @SuppressWarnings("serial")
    private class ConnectAction extends AbstractAction {
        public ConnectAction() {
            putValue(NAME, "Connect");
            putValue(SHORT_DESCRIPTION, "Connect action");
        }
        public void actionPerformed(ActionEvent e) {
            if (!client.isConnected()) {
                logCanhacker("Connecting\n");
                
                CanHacker canHacker = new CanHacker();
                canHacker.setPortName((String) portNameBox.getSelectedItem());
                canHacker.setSpeed(115200);
                
                canHacker.addEventListener(new CommandSendEventClassListener() {
                    public void handleCommandSendEventClassEvent(CommandSendEvent e) {
                        logCanhacker("-> " + e.getCommand().toString());
                    }
                });
                
                canHacker.addEventListener(new ResponseReceivedEventClassListener() {
                    public void handleResponseReceivedEventClassEvent(ResponseReceivedEvent e) {
                        logCanhacker("<- " + e.getCommand().toString());
                    }
                });
                
                
                client.setAdapter(canHacker);
                
                client.addEventListener(new FrameReceivedEventClassListener() {
                    public void handleFrameReceivedEvent(FrameReceivedEvent e) {
                        canReceiveTable.addCanFrame(e.getFrame());
                    }
                });
                client.addEventListener(new FrameSentEventClassListener() {
                    public void handleFrameSentEvent(FrameSentEvent e) {
                        canSentTable.addCanFrame(e.getFrame());
                    }
                });
                
                try {
                    client.connect();
                    logCanhacker("Connected");
                    
                    CanComfort.emulateCar(client, vinTextField.getText());
                    logCanhacker("Emulation started");
                    
                } catch (PortInUseException exception) {
                    logCanhacker("Error: Port " + canHacker.getPortName() + " in use");
                } catch (NoSuchPortException exception) {
                    logCanhacker("Error: Port " + canHacker.getPortName() + " not found");
                } catch (Exception exception) {
                    logCanhacker("Exception: " + exception.toString());
                }
            }
            
            connectAction.setEnabled(!client.isConnected());
            disconnectAction.setEnabled(client.isConnected());
        }
    }
    @SuppressWarnings("serial")
    private class DisconnectAction extends AbstractAction {
        public DisconnectAction() {
            putValue(NAME, "Disconnect");
            putValue(SHORT_DESCRIPTION, "Some short description");
        }
        public void actionPerformed(ActionEvent e) {
            if (client.isConnected()) {
                logCanhacker("Disconnecting\n");
                client.disconnect();
                logCanhacker("Disconnected\n");
            }
            
            connectAction.setEnabled(!client.isConnected());
            disconnectAction.setEnabled(client.isConnected());
        }
    }
    @SuppressWarnings("serial")
    private class ExitAction extends AbstractAction {
        public ExitAction() {
            putValue(NAME, "Exit");
            putValue(SHORT_DESCRIPTION, "Exit application");
        }
        public void actionPerformed(ActionEvent e) {
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        }
    }
}
