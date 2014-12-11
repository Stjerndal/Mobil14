package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {

	private Vector<NewClient> clientSockets;

	public Server() throws IOException {
		clientSockets = new Vector<NewClient>();
		int portnumber = 11111;
		ServerSocket serverSocket = new ServerSocket(portnumber);
		System.out.println("Server is running");

		while (true) {
			try {
				Socket clientSocket = serverSocket.accept();
				NewClient client = new NewClient(clientSocket);
				clientSockets.add(client);
				System.out.println("There are now " + clientSockets.size() + " clients running");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws IOException {

		Server server = new Server();
	}

	private class NewClient extends Thread {
		Socket clientSocket;
		BufferedReader in;
		PrintWriter out;

		public NewClient(Socket clientSocket) {
			this.clientSocket = clientSocket;
			try {

				this.out = new PrintWriter(clientSocket.getOutputStream(), true);
				this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				System.out.println(clientSocket.getInetAddress() + " connected..");
				out.println("Welcome to Måns test server 2.0;  " + clientSocket.getInetAddress()
						+ ", you are now connected");

			} catch (IOException e) {
				System.err.println("Something went wrong when trying to connect " + clientSocket.getInetAddress());
			}
			start();

		}

		public void run() {

			String inputLine;
			try {

				while ((inputLine = in.readLine()) != null) {
					spreadMessage(inputLine);
					// System.out.println("Client("+clientSocket.getInetAddress()+") said: "
					// + inputLine);
				}

			} catch (IOException e) {
				clientSockets.remove(this);
				System.out.println(clientSocket.getInetAddress() + " has left. no of clients: " + clientSockets.size());
				// e.printStackTrace();
			}
		}

		private void spreadMessage(String message) throws IOException {
			PrintWriter tempOut;
			for (int i = 0; i < clientSockets.size(); i++) {
				tempOut = new PrintWriter(clientSockets.get(i).clientSocket.getOutputStream(), true);
				tempOut.println("Client(" + clientSocket.getInetAddress() + ") said: " + message);
				System.out.println(message);
			}
		}
	}
}