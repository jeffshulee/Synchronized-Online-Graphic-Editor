import java.io.*;
import java.net.Socket;

/**
 * Handles communication between the server and one client, for SketchServer
 * Dartmouth CS 10, Winter 2015
 */
public class SketchServerCommunicator extends Thread {
	private Socket sock;					// to talk with client
	private BufferedReader in;				// from client
	private PrintWriter out;				// to client
	private SketchServer server;			// handling communication for

	public SketchServerCommunicator(Socket sock, SketchServer server) {
		this.sock = sock;
		this.server = server;
	}

	/**
	 * Sends a message to the client
	 * @param msg
	 */
	public void send(String msg) {
		out.println(msg);
	}
	
	/**
	 * Keeps listening for and handling (your code) messages from the client
	 */
	public void run() {
		try {
			System.out.println("someone connected");
			
			// Communication channel
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(sock.getOutputStream(), true);

			// Tell the client the current state of the world (server updated its sketch, now telling everyone else to update the same thing)
			// TODO: YOUR CODE HERE
			
			for (int i = 0; i < server.getSketch().size(); i++) {	
				if (server.getSketch().get(i) != null) {	// if there is an index for sketch
					send("add" + server.getSketch().get(i));	//// a new client joins, tell him what has been drawn already 
				}
					

			};		
			

			// Keep getting and handling messages from the client
			// TODO: YOUR CODE HERE
			String line;
			while ((line = in.readLine()) != null) {		//  Server is doing the same thing editor communicator is doing but on server sketch
				Message message = new Message(line);
				message.update(server.getSketch());
				
				//broadcast that message out to everyone   
				server.broadcast(message.toString());			
				
			}

			// Clean up -- note that also remove self from server's list so it doesn't broadcast here
			server.removeCommunicator(this);
			out.close();
			in.close();
			sock.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
