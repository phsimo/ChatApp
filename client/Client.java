package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Class that contains several that control the flow of messages that the user sends and recives from the server
 */
public class Client {
	
	ClientFrame frame;
	BufferedReader reader;;
	Socket socket;
	String username;
	Map<String, MessageFrame> frameList=new HashMap<String, MessageFrame>(); // contains the usernames and the message frames, for those clients the user is having a conversation with
	
	
	public Client() {
		frame=new ClientFrame(this); // creates a the main frame of the client
	}
	
	public void sendUsername(String user) throws Exception { //sends the username of this client to the server
			username=user;
			socket = new Socket(InetAddress.getLocalHost(), 5000);
			PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
			writer.println(username);
			new Incoming(this); // createss a new listening thread for incoming messages from the server
	}
	
	public void receiveList(List<String> list) { //receive the lists of the usernames of the clients connected to the server
		frame.showList(list,frameList);
	}
	
	/*
	 * Inserts the messages received from the server to the appropriate message frame.
	 * if no message frame has been opened with the sender, a new frame is created
	 */
	public void pasteMessage(String senderUsername,String textMessage) { 
		MessageFrame mFrame=frameList.get(senderUsername);
		if(mFrame!=null) //checks if a frame (therefore a conversation) already exits with the sender
			mFrame.insertMessage(senderUsername,textMessage);
		else { // if not a new frame is created
			mFrame= new MessageFrame(this,senderUsername);
			frameList.put(senderUsername, mFrame); // new frame gets inserted into the list of users and frames
			mFrame.insertMessage(senderUsername, textMessage);
		}
	}
	
	/*
	 * if the sender has been disconnected from the server, the text are of it message frame gets deactivated
	 */
	public void disconnected(String senderUsername) {
		MessageFrame mFrame=frameList.get(senderUsername);
		mFrame.blockFrame(senderUsername);
	}
	
	/*
	 * Returns the username of this client
	 */
	public String getUsername() {
		return username;
	}
	
	/*
	 * Inserts the message received from the sender, to its appropriate message frame
	 */
	public void insertMessageFrame(String toUsername,MessageFrame mFrame) {
		frameList.put(toUsername, mFrame);
	}
	
	/*
	 * Sends message in xml format to the server
	 */
	public void sendString(String xmlString) throws Exception {
		PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
		writer.println(xmlString);
	}
	
	/*
	 * New class thread that listens to the XML messages send by the server
	 */
	class Incoming implements Runnable {
		Client client;
		public Incoming(Client client) {
			this.client=client;
			Thread t = new Thread(this);
			t.start();
		}
		
		@Override
		public void run() {
			String s=null;
			while(true) {
				try{  
					reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					 s=reader.readLine();
					 if(s!=null) {
						 new XMLProcessing(s,client); // process the received messages		 
					 }
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
	
		
		
	
		



