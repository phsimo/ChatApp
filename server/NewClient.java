package server;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;


import javax.xml.parsers.*;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;

/*
 * Class that creates a thread, which receives all the messages from one client and forwards them to the specified client.
 */
public class NewClient implements Runnable {
	Socket socket;
	BufferedReader reader;
	String xmlString;
	String toUsername;
	Server server;
	
	
	public NewClient(Socket socket, Server server) {
		this.socket=socket;
		this.server=server;
		Thread thread =  new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		while(true) {
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); //gets messages from the client
				if((xmlString= reader.readLine())!=null) {
					try{
						parseXML(xmlString); 
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * Decodes the XML in order to retrieve the destination client for the message.
	 * if the client disocnnects, the servers makes an update to its clients' list and informs the remaining clients
	 */
	public void parseXML(String xml) throws Exception {
		String root;
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder= dbfactory.newDocumentBuilder();
		ByteArrayInputStream bytearray = new ByteArrayInputStream(xml.getBytes());
		Document doc= builder.parse(bytearray);
		root=doc.getDocumentElement().getNodeName();
		if(root.equalsIgnoreCase("Message")) {
			NodeList nList= doc.getElementsByTagName("toUsername");//gets the username of the destination
			Node node= nList.item(0);
			if(node.getNodeType()==Node.ELEMENT_NODE)
				sendXMLMessage(node.getTextContent()); //method that forwards the message
		}
		else 
			if(root.equalsIgnoreCase("Disconnected")) { // message from the client
				NodeList nList= doc.getElementsByTagName("fromUsername"); //gets the username of the disconnected client
				Node node= nList.item(0);
				server.synClientList.remove(node.getTextContent()); //removes the client from the server list of clients
				server.serverFrame.update(server.synClientList.keySet().toArray()); //updates the server frame.
				server.sendList(); //sends the updated list of clients to every one of them
				return; //closes this client thread
			}
	}
	
	/*
	 * Send the message to the specified destination.
	 * if the destination is not connected anymore, an appropriate message is sent back to the sender.
	 */
	public void sendXMLMessage(String username) {
		Socket destinationSocket = server.synClientList.get(username);
		try {
		if(destinationSocket!=null) { //checks if the destination socket exists
			PrintWriter writer= new PrintWriter(destinationSocket.getOutputStream(), true);
			writer.println(xmlString);
		}
		else{ //if the destination client has disconnected, the sender gets a message from the server.
			PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+"<Disconnected>"+ //message in XMLformat
													"<toUsername>"+username+"</toUsername>"+"</Disconnected>");
		}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

}
