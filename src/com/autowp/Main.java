package com.autowp;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.JToggleButton;

import com.autowp.canhacker.CommandReceivedEvent;
import com.autowp.canhacker.CommandReceivedEventClassListener;
import com.autowp.canhacker.CommandSendEvent;
import com.autowp.canhacker.CommandSendEventClassListener;
import javax.swing.JComboBox;

public class Main {

    private JFrame frame;

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
        frame = new JFrame();
        frame.setBounds(100, 100, 487, 623);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new GridLayout(2, 2, 10, 10));
        
        final CanClient client = new CanClient();
        
        JPanel panel_1 = new JPanel();
        frame.getContentPane().add(panel_1);
        
        JPanel panel = new JPanel();
        frame.getContentPane().add(panel);
        panel.setLayout(new BorderLayout(0, 0));
        
        final JTextArea textArea = new JTextArea();
        panel.add(textArea, BorderLayout.CENTER);
        textArea.setEditable(false);
        panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        
        JToggleButton connectButton = new JToggleButton("Connect");
        
        HashSet<CommPortIdentifier> ports = getAvailableSerialPorts();
        
        Vector<String> comboBoxItems = new Vector<String>();
        final DefaultComboBoxModel model = new DefaultComboBoxModel(comboBoxItems);
        for (CommPortIdentifier port : ports) {
            comboBoxItems.add(port.getName());
        }
        model.setSelectedItem("COM3");
        
        final JComboBox portName = new JComboBox(model);
        panel_1.add(portName);

        panel_1.add(connectButton);
        
        
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                AbstractButton abstractButton = (JToggleButton) actionEvent.getSource();
                if (abstractButton.isSelected()) {
                    if (!client.isConnected()) {
                        textArea.append("Connecting\n");
                        
                        client.setPortName((String) portName.getSelectedItem());
                        try {
                            client.connect();
                            textArea.append("Connected\n");
                        } catch (PortInUseException e) {
                            textArea.append("Error: Port " + client.getPortName() + " in use\n");
                        } catch (NoSuchPortException e) {
                            textArea.append("Error: Port " + client.getPortName() + " not found\n");
                        } catch (Exception e) {
                            textArea.append("Exception: " + e.toString() + "\n");
                        }
                        
                        abstractButton.setSelected(client.isConnected());
                    }
                } else {
                    if (client.isConnected()) {
                        textArea.append("Disconnecting\n");
                        client.disconnect();
                        textArea.append("Disconnected\n");
                    }
                }
            }
        });
        
        client.getCanHacker().addEventListener(new CommandSendEventClassListener() {
            public void handleCommandSendEventClassEvent(CommandSendEvent e) {
                textArea.append("-> " + e.getCommand().toString() + "\n");
            }
        });
        
        client.getCanHacker().addEventListener(new CommandReceivedEventClassListener() {
            public void handleCommandReceivedEventClassEvent(CommandReceivedEvent e) {
                textArea.append("<- " + e.getCommand() + "\n");
            }
        });
        
        
    }

    public static HashSet<CommPortIdentifier> getAvailableSerialPorts() {
        HashSet<CommPortIdentifier> h = new HashSet<CommPortIdentifier>();
        Enumeration thePorts = CommPortIdentifier.getPortIdentifiers();
        while (thePorts.hasMoreElements()) {
            CommPortIdentifier com = (CommPortIdentifier) thePorts.nextElement();
            switch (com.getPortType()) {
            case CommPortIdentifier.PORT_SERIAL:
                try {
                    CommPort thePort = com.open("CommUtil", 50);
                    thePort.close();
                    h.add(com);
                } catch (PortInUseException e) {
                    System.out.println("Port, "  + com.getName() + ", is in use.");
                } catch (Exception e) {
                    System.err.println("Failed to open port " +  com.getName());
                    e.printStackTrace();
                }
            }
        }
        return h;
    }
}
