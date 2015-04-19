package client;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.MouseInputListener;


/*
 * The main frame for Client, from where he inserts his username and can connect to other clients already connected to the server
 */
public class ClientFrame extends JFrame {

	Container contentPane;
	JPanel topPanel,bottomPanel;
	JLabel label;
	JList list=null;
	JFrame clientFrame=this;
	JTextField textfield;
	JButton connectButton,chatButton;
	Client client;
	
	List<String> uList = new ArrayList<String>();
	Map<String, MessageFrame> fList=new HashMap<String, MessageFrame>();
	
	ActionListenerClass aListener = new ActionListenerClass();
	ChatActionListener chatActionListener = new ChatActionListener();
	MyWindowListener framListener=new MyWindowListener();
	

	public ClientFrame(Client client) {
		super();
		this.client=client;
		init();
	}
    /*
     * Initialization for the  client frame.
     * It contains two panels:top and bottom. Top is where the client enters its username.
     * Bottom contains the list of  other clients to chat with
     */
	private void init() {
		contentPane=getContentPane();
		topPanel= new JPanel();
		contentPane.setLayout(new BorderLayout());
		label = new JLabel("Username");
		textfield = new JTextField("Type Username", 15);
		textfield.addActionListener(aListener);
		connectButton = new JButton("Connect to Server");
		connectButton.addActionListener(aListener);
		topPanel.setLayout(new FlowLayout());
		label.setVerticalAlignment(FlowLayout.LEFT);
		topPanel.add(label);
		topPanel.add(textfield);
		topPanel.add(connectButton);
		setSize(600, 600);
		contentPane.add(topPanel, BorderLayout.NORTH);
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.addWindowListener(framListener);
	}
	
	/*
	 * Window Listener that sends a XML disconnection message to the server before closing
	 */
	class MyWindowListener extends WindowAdapter {
				
		public void windowClosing(WindowEvent e) {
			try {
				XMLProcessing xmlProcess = new XMLProcessing(client);
				String xml=xmlProcess.createDisconnectionXML();
				client.sendString(xml);
				System.exit(0);
			}catch(Exception i) {
				i.printStackTrace();
			}
		}

	}
	
	/*
	 * ActionListener that gets the username from the textfield after the "Connect to Server" button is pressed.
	 */
	class ActionListenerClass implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			try {
				textfield.setEditable(false);
				connectButton.setEnabled(false); // username can be sent only once
				client.sendUsername(textfield.getText()); //sends the username to the server
			}catch(Exception a){
				a.printStackTrace();
			}
		}
	}

	/*
	 * Action Listener that opens a new message for the user to chat with the selected client.
	 */
	class ChatActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String newUser=null;
			MessageFrame messageFrame=null;
			newUser=(String) list.getSelectedValue();
			if((newUser!=null) && (fList.containsKey(newUser)==false)) { //checks if a message frame has already been opened with the selected client
				messageFrame=new MessageFrame(client,newUser);
				client.insertMessageFrame(newUser, messageFrame);
			}
		}
	}
	/*
	 * Shows the list of clients connected to the server
	 * The user can start a chat with any of them by selecting the username and clicking the button "Chart"
	 */
	public void showList(List<String> userList,Map<String, MessageFrame> frameList) {
		
		uList=userList;
		fList=frameList;
		if(list==null) { // if it's the first time that the list is created, all the JComponents are created
			list= new JList(uList.toArray());
			chatButton=new JButton("Chat");
			bottomPanel=new JPanel();
			chatButton.addActionListener(chatActionListener);
			list.setSize(300, 500);
			bottomPanel.setLayout(new BorderLayout());
			bottomPanel.add(list,BorderLayout.CENTER);
			bottomPanel.add(chatButton,BorderLayout.EAST);
			contentPane.add(bottomPanel, BorderLayout.CENTER);
			bottomPanel.revalidate();
		}else
		{
			list.setListData(uList.toArray()); // updates the list with the current clients connected to the servers
			bottomPanel.revalidate();
		}
	}

}
