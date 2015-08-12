package com.autowp;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;

import jssc.SerialPortList;

import com.autowp.arduinocan.ArduinoCanSerial;
import com.autowp.can.CanClient;
import com.autowp.can.CanClientException;
import com.autowp.can.CanFrame;
import com.autowp.can.CanFrameException;
import com.autowp.can.CanMessage;
import com.autowp.canhacker.CanHacker;
import com.autowp.canhacker.CanHackerSerial;
import com.autowp.canhacker.command.Command;
import com.autowp.canhacker.response.Response;
import com.autowp.dashboard.DashboardDialog;
import com.autowp.elm327.Elm327;
import com.autowp.psa.CanComfortSpecs;
import com.autowp.psa.bsi.BSI;
import com.autowp.psa.bsi.BSIDialog;
import com.autowp.psa.cdchanger.CDChanger;
import com.autowp.psa.cdchanger.CDChangerDialog;
import com.autowp.psa.columnkeypad.ColumnKeypadDialog;
import com.autowp.psa.display.DisplayDialog;
import com.autowp.psa.message.MessageException;
import com.autowp.psa.radiokeypad.RadioKeypadDialog;
import com.autowp.sender.SenderDialog;

public class Main {

    private JFrame frame;
    
    private CanClient client;
    private BSI mBSI;
    private CDChanger mCD;
    
    private JTextArea canhackerLogTextArea;
    private CanFrameTable canSentTable;
    private CanFrameTable canReceiveTable;
    private CanFrameTable canMessageReceiveTable;
    
    private DisplayDialog displayDialog;
    private DashboardDialog dashboardDialog;
    
    private JList<String> list;
    
    private JComboBox<String> portNameBox;
    private final Action connectAction = new ConnectAction();
    private final Action disconnectAction = new DisconnectAction();
    private final Action exitAction = new ExitAction();
    private final Action createFilterAction = new CreateFilterAction();
    private ArrayList<CanFilterFrame> filterFrames = new ArrayList<CanFilterFrame>();
    private final Action showDisplayAction = new ShowDisplayAction();
    private final Action createMessageFilterAction = new CreateMessageFilterAction();
    private final Action showDashboardAction = new ShowDashboardAction();
    private final Action showSenderAction = new ShowSenderAction();
    private final Action showColumnKeypadAction = new ShowColumnKeypadAction();
    private final Action showRadioKeypadAction = new ShowRadioKeypadAction();
    private final Action showBSIAction = new ShowBSIAction();
    private final Action showCDChangerAction = new ShowCDChangerAction();

    public SenderDialog senderDialog;

    public ColumnKeypadDialog columnKeypadDialog;
    public RadioKeypadDialog radioKeypadDialog;

    public BSIDialog mBSIDialog;

    public CDChangerDialog mCDChangerDialog;
    

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
    
    protected CanClient createClient()
    {
        CanClient client = new CanClient(new CanComfortSpecs());
        
        client.addEventListener(new CanClient.OnCanFrameTransferListener() {
            @Override
            public void handleCanFrameReceivedEvent(CanFrame frame) {
                canReceiveTable.addCanFrame(frame);
            }
            @Override
            public void handleCanFrameSentEvent(CanFrame frame) {
                canSentTable.addCanFrame(frame);
            }
        });
        
        client.addEventListener(new CanClient.OnCanMessageTransferListener() {
            @Override
            public void handleCanMessageReceivedEvent(CanMessage message) {
                canMessageReceiveTable.addCanMessage(message);
            }
            @Override
            public void handleCanMessageSentEvent(CanMessage message) {
                // TODO Auto-generated method stub
                
            }
        });
        
        return client;
    }

    /**
     * Initialize the contents of the frame.
     */
    @SuppressWarnings({ "serial" })
    private void initialize() {
        disconnectAction.setEnabled(false);
        frame = new JFrame();
        frame.setBounds(100, 100, 487, 623);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
        String[] comboBoxItems = getAvailableSerialPorts();
        
        final DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>(comboBoxItems);
        model.setSelectedItem("COM5");
        
        this.client = createClient();
        
        JToolBar toolBar = new JToolBar();
        
        list = new JList<String>();
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setModel(new AbstractListModel<String>() {
            String[] values = new String[] {"ArduinoCan", "ELM327", "CanHacker"};
            public int getSize() {
                return values.length;
            }
            public String getElementAt(int index) {
                return values[index];
            }
        });
        list.setSelectedIndex(0);
        toolBar.add(list);
        portNameBox = new JComboBox<String>(model);
        toolBar.add(portNameBox);
        
        JButton connectBtn = new JButton(connectAction);
        toolBar.add(connectBtn);
        
        JButton disconnectBtn = new JButton(disconnectAction);
        toolBar.add(disconnectBtn);
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
        
        JMenuItem mntmShowBSI = new JMenuItem("Show BSI");
        mntmShowBSI.setAction(showBSIAction);
        mnNewMenu_1.add(mntmShowBSI);
        
        JMenuItem displayMenuItem = new JMenuItem("Show display");
        displayMenuItem.setAction(showDisplayAction);
        mnNewMenu_1.add(displayMenuItem);
        
        JMenuItem dashboardMenuItem = new JMenuItem("Show dashboard");
        dashboardMenuItem.setAction(showDashboardAction);
        mnNewMenu_1.add(dashboardMenuItem);
        
        JMenuItem mntmShowSender = new JMenuItem("Show sender");
        mntmShowSender.setAction(showSenderAction);
        mnNewMenu_1.add(mntmShowSender);
        
        JMenuItem mntmShowColumnKeypad = new JMenuItem("Show column keypad");
        mntmShowColumnKeypad.setAction(showColumnKeypadAction);
        mnNewMenu_1.add(mntmShowColumnKeypad);
        
        JMenuItem mntmShowRadioKeypad = new JMenuItem("Show radio keypad");
        mntmShowRadioKeypad.setAction(showRadioKeypadAction);
        mnNewMenu_1.add(mntmShowRadioKeypad);
        
        JMenuItem mntmShowCdChanger = new JMenuItem("Show CD changer");
        mntmShowCdChanger.setAction(showCDChangerAction);
        mnNewMenu_1.add(mntmShowCdChanger);

    }
    
    protected void logCanhacker(String str)
    {
        canhackerLogTextArea.append(str + "\n");
    }
    
    public static String[] getAvailableSerialPorts() {
        return SerialPortList.getPortNames();
    }
    
    private BSI getBSI() throws MessageException
    {
        if (mBSI == null) {
            mBSI = new BSI(client);
            mBSI.setVIN("21496464");
        }
        
        return mBSI;
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
                
                switch (list.getSelectedValue()) {
                    case "ArduinoCan":
                        ArduinoCanSerial canAdapter = new ArduinoCanSerial();
                        canAdapter.setPortName((String) portNameBox.getSelectedItem());
                        
                        /*canAdapter.addEventListener(new com.autowp.arduinocan.ArduinoCan.OnResponseReceivedListener() {

                            @Override
                            public void handleResponseReceivedEvent(
                                    com.autowp.arduinocan.Response response) {
                                logCanhacker("<- " + response.toString());
                            }
                            
                        });*/
                        
                        client.setAdapter(canAdapter);
                        
                        break;
                
                    case "ELM327":
                        Elm327 elm327 = new Elm327();
                        elm327.setPortName((String) portNameBox.getSelectedItem());
                        
                        /*elm327.addEventListener(new com.autowp.elm327.CommandSendEventListener() {
                            @Override
                            public void handleCommandSendEventClassEvent(com.autowp.elm327.CommandSendEvent e) {
                                logCanhacker("-> " + e.getCommand().toString());
                            }
                        });
                        
                        elm327.addEventListener(new com.autowp.elm327.ResponseReceivedEventClassListener() {
                            @Override
                            public void handleResponseReceivedEventClassEvent(com.autowp.elm327.ResponseReceivedEvent e) {
                                logCanhacker("<- " + e.getCommand().toString());
                            }
                        });*/
                        
                        client.setAdapter(elm327);
                        
                        break;
                        
                    case "CanHacker":
                        CanHackerSerial canHacker = new CanHackerSerial();
                        canHacker.setPortName((String) portNameBox.getSelectedItem());
                        canHacker.setSpeed(115200);
                        
                        /*canHacker.addEventListener(new CanHacker.OnCommandSentListener() {
                            @Override
                            public void handleCommandSentEvent(Command command) {
                                logCanhacker("-> " + command.toString());
                            }
                        });
                        
                        canHacker.addEventListener(new CanHacker.OnResponseReceivedListener() {
                            @Override
                            public void handleResponseReceivedEvent(
                                    Response response) {
                                logCanhacker("<- " + response.toString());
                            }
                        });*/
                        
                        client.setAdapter(canHacker);
                        break;

                }
                
                logCanhacker("Connecting ...");
                
                try {
                    client.connect();
                    logCanhacker("Connected");
                    
                    BSI bsi = getBSI();
                    bsi.setReceive(true);
                    bsi.startStatus();
                    bsi.startInfo();
                    bsi.startVIN();
                    bsi.startInfoWindow();
                    bsi.setDashboardLightingBrightness((byte) 0x08);
                    logCanhacker("Emulation started");
                    
                    mCD = new CDChanger(client);
                    mCD.start();
                    
                    if (mBSIDialog != null) {
                        mBSIDialog.refreshControls();
                    }
                    
                } catch (CanClientException e1) {
                    logCanhacker("Can client error: " + e1.getMessage());
                } catch (CanFrameException e1) {
                    logCanhacker("Can frame error: " + e1.getMessage());
                } catch (MessageException e1) {
                    logCanhacker("Message error: " + e1.getMessage());
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
                logCanhacker("Disconnecting");
                try {
                    BSI bsi = getBSI();
                    bsi.stopStatus();
                    bsi.stopInfo();
                    bsi.stopInfoWindow();
                    bsi.stopVIN();
                    
                    if (mBSIDialog != null) {
                        mBSIDialog.refreshControls();
                    }
                    
                    mCD.stop();
                    
                    client.disconnect();
                    logCanhacker("Disconnected");
                } catch (MessageException e1) {
                    logCanhacker("Message error: " + e1.getMessage());
                }
                
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
    private class ShowDashboardAction extends AbstractAction {
        private static final long serialVersionUID = 1L;
        public ShowDashboardAction() {
            putValue(NAME, "Show dashboard");
            putValue(SHORT_DESCRIPTION, "Show dashboard");
        }
        public void actionPerformed(ActionEvent e) {
            if (dashboardDialog == null) {
                dashboardDialog = new DashboardDialog(client);
            }
            dashboardDialog.setVisible(true);
            dashboardDialog.toFront();
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
    private class ShowSenderAction extends AbstractAction {
        private static final long serialVersionUID = 1L;
        public ShowSenderAction() {
            putValue(NAME, "Show sender");
            putValue(SHORT_DESCRIPTION, "Some short description");
        }
        public void actionPerformed(ActionEvent e) {
            if (senderDialog == null) {
                senderDialog = new SenderDialog(client);
            }
            senderDialog.setVisible(true);
            senderDialog.toFront();
        }
    }
    private class ShowColumnKeypadAction extends AbstractAction {
        private static final long serialVersionUID = 1L;
        public ShowColumnKeypadAction() {
            putValue(NAME, "Show column keypad");
            putValue(SHORT_DESCRIPTION, "Some short description");
        }
        public void actionPerformed(ActionEvent e) {
            if (columnKeypadDialog == null) {
                columnKeypadDialog = new ColumnKeypadDialog(client);
            }
            columnKeypadDialog.setVisible(true);
            columnKeypadDialog.toFront();
        }
    }
    private class ShowRadioKeypadAction extends AbstractAction {
        private static final long serialVersionUID = 1L;
        public ShowRadioKeypadAction() {
            putValue(NAME, "Show radio keypad");
            putValue(SHORT_DESCRIPTION, "Some short description");
        }
        public void actionPerformed(ActionEvent e) {
            if (radioKeypadDialog == null) {
                radioKeypadDialog = new RadioKeypadDialog(client);
            }
            radioKeypadDialog.setVisible(true);
            radioKeypadDialog.toFront();
        }
    }
    
    private class ShowBSIAction extends AbstractAction {
        private static final long serialVersionUID = 1L;
        public ShowBSIAction() {
            putValue(NAME, "Show BSI");
        }
        public void actionPerformed(ActionEvent e) {
            try {
                if (mBSIDialog == null) {
                    mBSIDialog = new BSIDialog(getBSI());
                }
                mBSIDialog.setVisible(true);
                mBSIDialog.toFront();
                mBSIDialog.refreshControls();
            } catch (MessageException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }
    
    private class ShowCDChangerAction extends AbstractAction {
        private static final long serialVersionUID = 1L;
        public ShowCDChangerAction() {
            putValue(NAME, "Show CD changer");
        }
        public void actionPerformed(ActionEvent e) {
            if (mCDChangerDialog == null) {
                mCDChangerDialog = new CDChangerDialog(mCD);
            } else {
                mCDChangerDialog.setCDChanger(mCD);
            }
            
            mCDChangerDialog.setVisible(true);
            mCDChangerDialog.toFront();
        }
    }
}
