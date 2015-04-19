package client;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/*
 * Class that holds several methods for converting to and from XML
 */
public class XMLProcessing {
	String xmlString;
	Client client;
	DocumentBuilderFactory factory;
	DocumentBuilder builder;
	Document doc;
	NodeList nList;
	String root,sender,message;
	
	/*
	 * Constructor used when converting to XML
	 */
	public XMLProcessing(Client client) {
		this.client=client;
	}
	
	/*
	 * Constructor used when converting from XML
	 */
	public XMLProcessing(String xmlString, Client client) {
		this.xmlString=xmlString;
		this.client=client;
		getRoot(xmlString);
	}
	
	/*
	 * Gets the root of the XML string sent by the server
	 */
	public void getRoot(String xmlString) {
		try {
			root="";
			factory=DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			ByteArrayInputStream bytearray = new ByteArrayInputStream(xmlString.getBytes());
			doc= builder.parse(bytearray);
			root=doc.getDocumentElement().getNodeName();
			if(root.equalsIgnoreCase("List")) // server has send the list of users
				getUserList();
			else if (root.equalsIgnoreCase("Message")) //server has send a message from another client
				getMessageInfo();
			else if (root.equalsIgnoreCase("Disconnected")) { //server has send a message signaling that the client we are chating has disconnected
				getDisconnectionInfo();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/*
	 * Retrieves the name of the user that has disconnected from the server from the XML string
	 */
	public void getDisconnectionInfo() {
		nList=doc.getElementsByTagName("toUsername");
		sender=nList.item(0).getTextContent();
		client.disconnected(sender);
	}
	
	/*
	 * Extracts all the users connected to the server from the XML string
	 */
	public void getUserList() throws Exception {
			nList=doc.getElementsByTagName("username");
			List<String> listOfUsers= new ArrayList<String>();
			for(int i=0;i<nList.getLength();i++) {
				listOfUsers.add(i, nList.item(i).getTextContent());;
			}
			listOfUsers.remove(client.getUsername());// removes the name of the user from the list
			client.receiveList(listOfUsers);
	}
	
	/*
	 * Retrieves the name of the client and the its message from the XML string
	 */
	public void getMessageInfo() throws Exception {
			nList=doc.getElementsByTagName("fromUsername");
			sender=nList.item(0).getTextContent();
			nList=doc.getElementsByTagName("text");
			message=nList.item(0).getTextContent();
			client.pasteMessage(sender,message);
	}
	
	/*
	 * Creates the XML version of the text message send by the user to the specified client
	 */
	public void createXML(String receiver,String message) throws Exception {
		String declaration="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		String fromUsername=client.getUsername();
		String toUsername=receiver;
		String xml=declaration+"<Message>"+"<fromUsername>"+fromUsername+"</fromUsername>"
				+"<toUsername>"+toUsername+"</toUsername>"+"<text>"+message+"</text>"+"</Message>";
		client.sendString(xml); // sends XML to the server
	}
	
	/*
	 * Creates a XML version of the string that the user sends to the server before disconnecting
	 */
	public String createDisconnectionXML() {
		String declaration="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		String fromUsername=client.getUsername();
		String xml=declaration+"<Disconnected>"+"<fromUsername>"+fromUsername+"</fromUsername>"+"</Disconnected>";
		return xml;
	}
}
