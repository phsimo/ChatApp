# Chat Program

Wondering how Skype or Yahoo messenger works?
Find your answer here in this project. Out of my curiosity, and I also got this question from time to time, 
I decided to create a simple program that demonstrates how a chat program works. You can find the source code in this project as well.

##Strategy/Algorithm
First, the main idea is to have a server that orchestrates and directs the flows of messages between clients. Note that this is not a peer-to-peer connection, therefore all messages have to go through the server. Server will determine which client to forward the message to.
Server has a thread, in which it is continuously listening and waiting for connections from new clients.
```while(true) {
			Socket socket = serverSocket.accept();
			BufferedReader contactInfoStream= new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while((username = contactInfoStream.readLine())==null);
			synClientList.put(username, socket);//inserts the username and the socket of the new client into the HashMap
			sendList();//send the updated list to all the users
			serverFrame.update(synClientList.keySet().toArray()); //updates the server frame with the new list of clients
			new NewClient(socket,this); //creates a new listening thread for the new client
		}```

The main challenge is to manage all the threads that handle receiving and sending messages. 

