# Chat Program

Wondering how Skype or Yahoo messenger works?
Find your answer here in this project. Out of my curiosity, and I also got this question from time to time, 
I decided to create a simple program that demonstrates how a chat program works. You can find the source code in this project as well.

##Strategy/Algorithm
First, the main idea is to have a server that orchestrates and directs the flows of messages between clients. Note that this is not a peer-to-peer connection, therefore all messages have to go through the server. Server will determine which client to forward the message to.
Server has a thread, in which it is continuously listening and waiting for connections from new clients.
``` 
while(true) {
			Socket socket = serverSocket.accept();
			//Create a new client using this returned socket
		}

	
```
The main challenge is to manage all the threads that handle receiving and sending messages. 

