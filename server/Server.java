package server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/*
 * Class that handles the establishment of the connection with the clients and sends an updated list of clients,
 * every time a client joins or leaves the server.
 */
public class Server {
	public static int PORT_NUMBER= 5000; // port number for the server
	public Map<String, Socket> synClientList; // holds the usernames and the sockets of the connected clients
	ServerSocket serverSocket;
	String username;
	ServerFrame serverFrame;
	
	public Server() throws IOException {
		synClientList = Collections.synchronizedMap(new HashMap<String, Socket>());
		serverSocket = new ServerSocket(PORT_NUMBER);
		serverFrame= new ServerFrame();
	}
	
	/*
	 * Sends an updated list of clients in XML format to each client
	 */
	public void sendList() {
		String list= createXMLMAp(); // all usernames are put into a XML string
		for(Socket socket:synClientList.values()) {
			try {
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				out.println(list);
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * Creates a XML String with all the usernames of the clients connected to the server.
	 * Called when a new client joins, or an existing client leaves the server.
	 */
	public String createXMLMAp() {
		String xmlDeclaration="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		Set<String> usernameList=synClientList.keySet();
		String xmlBody="";
		for (String s:usernameList) {
			xmlBody+="<username>"+s+"</username>";
			}
		return xmlDeclaration+"<List>"+xmlBody+"</List>";
	}
	
	/*
	 * Continuously listens for connection with new clients
	 * Creates a new listening thread for each client
	 */
	public void startReceivingClients() throws Exception {
		while(true) {
			Socket socket = serverSocket.accept();
			BufferedReader contactInfoStream= new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while((username = contactInfoStream.readLine())==null);
			synClientList.put(username, socket);//inserts the username and the socket of the new client into the HashMap
			sendList();//send the updated list to all the users
			serverFrame.update(synClientList.keySet().toArray()); //updates the server frame with the new list of clients
			new NewClient(socket,this); //creates a new listening thread for the new client
		}
	}
	
	public static void main(String[] args) throws Exception {
		Server server1= new Server();
		server1.startReceivingClients();
	}
	
}
