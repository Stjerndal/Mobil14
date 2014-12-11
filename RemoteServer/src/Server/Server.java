package Server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

		public NewClient(Socket clientSocket) {

			this.clientSocket = clientSocket;

			start();

		}

		public void run() {
			System.out.println(clientSocket.getInetAddress() + " connected..");
			File file = new File("SensorData.txt");

			System.out.println("Writing file: " + file.getAbsolutePath());
			try {
				// file.createNewFile(); // For some reason this needs to be
				// done

				byte[] bytes = new byte[1024];
				FileOutputStream fos = new FileOutputStream(file);
				InputStream is = clientSocket.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				// BufferedOutputStream out = new
				// BufferedOutputStream(clientSocket.getOutputStream());
				System.out.println(file.length());
				int count;
				while ((count = bis.read(bytes)) > 0) {
					fos.write(bytes, 0, count);
					System.out.println(count);
				}

				fos.flush();
				// clientSocket.close();

			} catch (IOException e) {
				clientSockets.remove(this);
				System.out.println(clientSocket.getInetAddress() + " has left. no of clients: " + clientSockets.size());
				e.printStackTrace();
			}
			System.out.println("Received file");
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