import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JOptionPane;

/*
 * Program: Obs³uga serwera ksi¹¿ki telefonicznej
 *    Plik: PhoneBook.java
 *          
 *   Autor: Damian Bednarz 241283
 *    Data: styczeñ 2019 r.
 *	
 */

public class PhoneBookServer implements Runnable {

	static final int SERVER_PORT = 25000;
	
	PhoneBook phoneBook = new PhoneBook ();
	
	public static void main(String [] args){
		new PhoneBookServer();
	}
	
	PhoneBookServer(){
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		boolean socketCreated = false;
		
		try (ServerSocket serwer = new ServerSocket (SERVER_PORT)){
			String host = InetAddress.getLocalHost().getHostName();
			System.out.println("Host:  " + host);
			socketCreated=true;
			
			while(true){
				Socket socket = serwer.accept();
				if(socket != null) {
					new ClientThread(this,socket);
				}
			}
		} catch (IOException e) {
			System.out.println(e);
			if(!socketCreated) {
				JOptionPane.showMessageDialog(null, "Nie mo¿na utworzyæ gniazdka");
				System.exit(0);
			}else {
				JOptionPane.showMessageDialog(null, "B³¹d po³¹czenia z klientem");
			}
		}
		
	}

}

class ClientThread implements Runnable {

	private Socket socket;
	private String name;
	private PhoneBookServer server;	
	private ObjectOutputStream outputStream = null;
	
	ClientThread(String prototypeDisplayValue){
		name = prototypeDisplayValue;
	}
	
	ClientThread(PhoneBookServer server, Socket socket) { 
		this.server = server;
	  	this.socket = socket;
	  	new Thread(this).start();  
	}
	

	public String getName(){ return name; }
	
	public String toString(){ return name; }
	
	public void sendMessage(String message) {
		try {
			outputStream.writeObject(message);
			String [] command = message.split(" ");
			if(command[0].equals("BYE")) {
				socket.close();
				socket = null;
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		String message;
		try( ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
		   		 ObjectInputStream input = new ObjectInputStream(socket.getInputStream()); ){
			outputStream = output;
	   		name = (String)input.readObject();
			while(true){
				message = (String)input.readObject();
				String [] command = message.split(" ");
				try {
					if (command[0].toUpperCase().equals("SAVE")){
						sendMessage(server.phoneBook.save(command[1]));		
					}else
					if (command[0].toUpperCase().equals("LOAD")){
						sendMessage(server.phoneBook.load(command[1]));		
					}else
					if (command[0].toUpperCase().equals("GET")){
						sendMessage(server.phoneBook.get(command[1]));		
					}else
					if (command[0].toUpperCase().equals("PUT")){
						sendMessage(server.phoneBook.put(command[1],command[2]));		
					}else
					if (command[0].toUpperCase().equals("REPLACE")){
						sendMessage(server.phoneBook.replace(command[1],command[2]));		
					}else
					if (command[0].toUpperCase().equals("DELETE")){
						sendMessage(server.phoneBook.delete(command[1]));		
					}else
					if (command[0].toUpperCase().equals("LIST")){
						sendMessage(server.phoneBook.list());		
					}else
					if(command[0].toUpperCase().equals("BYE")) {
						break;
					}else {
						sendMessage("nieznana komenda");
					}
				}catch (PhoneBookException e) {
					sendMessage(e.getMessage());
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			socket.close();
			socket = null;
		}catch (Exception e){
			e.printStackTrace();
		}
		   	
		
	}
	
}
