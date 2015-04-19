# Chat Program

Wondering how Skype or Yahoo messenger works?
Find your answer here in this project. Out of my curiosity, and I also got this question from time to time from my colleages/friends, I decided to create a simple program that demonstrates how a chat program works. You can find the source code in this project as well.

##Strategy/Algorithm
First, the main idea is to have a server that orchestrates and directs the flows of messages between clients. Note that this is not a peer-to-peer connection, therefore all messages have to go through the server. Server will determine which client to forward the message to. The main challenge is to manage all the threads that handle receiving and sending messages. 

###Server side:
1) Server has a thread, in which it is continuously listening and waiting for connections from new clients.
``` 
while(true) {
			Socket socket = serverSocket.accept();
			//Create a new client using this returned socket in a new threat, say, Client thread.
		}

	
```
2) The Client created in step 1) (in a separate thread) will continuously listen to its incoming stream (InputStream) of its socket. Whenever a message is received from the client (which is encapsulated in an XML string), the server will detect the type of the message. There are 2 types of messages:

* Message indicates that the client is leaving, thus disconnecting from the server.

* A text message that the client wants to send to another client.

If the message is a disconnecting message, the server has to inform all the client about a new updated list of clients.
If the message is a text message, the server simply determines who the receiver is, and forwards the message to that receiver.

###Client side:
Similar to the server, a client has a separate thread to listen to the incoming input stream from the server. The XML from the input stream has three types:

* A text message from another client

* An updated list of other clients (when new clients join the chat room or old clients leave the chat room)

* A message telling that the client is currently continueing chatting with someone who just left the chat room.

```
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
```





