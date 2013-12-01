package com.autowp;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;

import java.awt.EventQueue;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import com.autowp.canclient.CanClient;
import com.autowp.canclient.CanClientException;
import com.autowp.canclient.CanFrameEvent;
import com.autowp.canclient.CanFrameEventClassListener;
import com.autowp.canclient.CanMessageEvent;
import com.autowp.canclient.CanMessageEventClassListener;
import com.autowp.canhacker.CanHacker;
import com.autowp.canhacker.ResponseReceivedEvent;
import com.autowp.canhacker.ResponseReceivedEventClassListener;
import com.autowp.canhacker.CommandSendEvent;
import com.autowp.canhacker.CommandSendEventClassListener;
import com.autowp.peugeot.CanComfort;
import com.autowp.peugeot.CanComfortException;
import com.autowp.peugeot.CanComfortSpecs;
import com.autowp.peugeot.DisplayDialog;

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
import javax.swing.JScrollPane;

public class Main {

    private JFrame frame;
    
    private CanClient client;
    
    private JTextArea canhackerLogTextArea;
    private CanFrameTable canSentTable;
    private CanFrameTable canReceiveTable;
    private CanFrameTable canMessageReceiveTable;
    
    private DisplayDialog displayDialog;
    
    private JComboBox<String> portNameBox;
    private final Action connectAction = new ConnectAction();
    private final Action disconnectAction = new DisconnectAction();
    private final Action exitAction = new ExitAction();
    private final Action createFilterAction = new CreateFilterAction();
    private JTextField vinTextField;
    private ArrayList<CanFilterFrame> filterFrames = new ArrayList<CanFilterFrame>();
    private final Action showDisplayAction = new ShowDisplayAction();
    private final Action createMessageFilterAction = new CreateMessageFilterAction();

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
        
        this.client = new CanClient(new CanComfortSpecs());
        
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
        
        
        
        canSentTable = new CanFrameTable();
        canSentTable.setEnabled(true);
        JScrollPane canSentScrollPane = new JScrollPane(canSentTable);
        tabbedPane.addTab("CAN sent", null, canSentScrollPane, null);
        
        canReceiveTable = new CanFrameTable();
        canReceiveTable.setEnabled(true);
        JScrollPane canReceiveScrollPane = new JScrollPane(canReceiveTable);
        tabbedPane.addTab("CAN rcvd frame", null, canReceiveScrollPane, null);
        
        canMessageReceiveTable = new CanFrameTable();
        canMessageReceiveTable.setEnabled(true);
        JScrollPane canMessageReceiveScrollPane = new JScrollPane(canMessageReceiveTable);
        tabbedPane.addTab("CAN rcvd message", null, canMessageReceiveScrollPane, null);

        
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
        
        JMenu mnNewMenu_1 = new JMenu("Tools");
        menuBar.add(mnNewMenu_1);
        
        JMenuItem createFilterMenuItem = new JMenuItem(createFilterAction);
        createFilterMenuItem.setText("Create frame filter");
        mnNewMenu_1.add(createFilterMenuItem);
        
        JMenuItem mntmCreateMessageFilter = new JMenuItem("Create message filter");
        mntmCreateMessageFilter.setAction(createMessageFilterAction);
        mnNewMenu_1.add(mntmCreateMessageFilter);
        
        JMenuItem displayMenuItem = new JMenuItem("Show display");
        displayMenuItem.setAction(showDisplayAction);
        mnNewMenu_1.add(displayMenuItem);

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
        @SuppressWarnings("rawtypes")
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
                
                client.addEventListener(new CanFrameEventClassListener() {
                    public void handleCanFrameReceivedEvent(CanFrameEvent e) {
                        canReceiveTable.addCanFrame(e.getFrame());
                    }
                    public void handleCanFrameSentEvent(CanFrameEvent e) {
                        canSentTable.addCanFrame(e.getFrame());
                    }
                });
                
                client.addEventListener(new CanMessageEventClassListener() {
                    @Override
                    public void handleCanMessageReceivedEvent(CanMessageEvent e) {
                        canMessageReceiveTable.addCanMessage(e.getMessage());
                    }
                    @Override
                    public void handleCanMessageSentEvent(CanMessageEvent e) {
                        // TODO Auto-generated method stub
                        
                    }
                });
                
              
                try {
                    client.connect();
                    logCanhacker("Connected");
                    CanComfort.emulateCar(client, vinTextField.getText());
                    logCanhacker("Emulation started");
                } catch (CanClientException e1) {
                    logCanhacker("Can client error: " + e1.getMessage());
                } catch (CanComfortException e1) {
                    logCanhacker("Can comfort error: " + e1.getMessage());
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
    
    @SuppressWarnings("serial")
    private class CreateFilterAction extends AbstractAction {
        public CreateFilterAction() {
            putValue(NAME, "Create filter");
            putValue(SHORT_DESCRIPTION, "Create filter");
        }
        public void actionPerformed(ActionEvent e) {
            final CanFilterFrame filterFrame = new CanFilterFrame(client, false);
            filterFrames.add(filterFrame);
            
            filterFrame.setVisible(true);
            
            filterFrame.addWindowListener(new WindowAdapter(){
                public void windowClosed(WindowEvent e){
                    filterFrames.remove(filterFrame);
                }
            });
        }
    }
    @SuppressWarnings("serial")
    private class ShowDisplayAction extends AbstractAction {
        public ShowDisplayAction() {
            putValue(NAME, "Show display");
            putValue(SHORT_DESCRIPTION, "Show display");
        }
        public void actionPerformed(ActionEvent e) {
            if (displayDialog == null) {
                displayDialog = new DisplayDialog(client);
            }
            displayDialog.setVisible(true);
            displayDialog.toFront();
        }
    }
    @SuppressWarnings("serial")
    private class CreateMessageFilterAction extends AbstractAction {
        public CreateMessageFilterAction() {
            putValue(NAME, "Create message filter");
            putValue(SHORT_DESCRIPTION, "Create message filter");
        }
        public void actionPerformed(ActionEvent e) {
            final CanFilterFrame filterFrame = new CanFilterFrame(client, true);
            filterFrames.add(filterFrame);
            
            filterFrame.setVisible(true);
            
            filterFrame.addWindowListener(new WindowAdapter(){
                public void windowClosed(WindowEvent e){
                    filterFrames.remove(filterFrame);
                }
            });
        }
    }
}
