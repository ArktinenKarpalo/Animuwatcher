package com.vatsul.awatcher.anidbapi;
import static com.vatsul.awatcher.Main.anidbConn;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import com.vatsul.awatcher.Main;

public class AnidbConnection {

	private InetSocketAddress SERVER = new InetSocketAddress("api.anidb.net", 9000);
	
	private DatagramSocket socket;
	
	private static long lastPacketSent; // Time when the latest packet was sent
	
	public String sessionKey;
	private String[] response;
	
	// Initializes and authenticates the session
	public AnidbConnection() {
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		// Authenticates with username and password from config
		System.out.println("Authenticating AniDB...");
		response = sendCommand("AUTH user="+Main.config.getAnidbUsername()+"&pass="+Main.config.getAnidbPassword()+"&protover=3&client=animuwatch&clientver=1&nat=1&comp=1&enc=UTF-8&mtu1400&imgserver=1").split(" ");
		checkCommonResponseErrors(response);
		switch(Integer.parseInt(response[0])) {
			case 200: // Login accepted
				System.out.println("Login Accepted");
				sessionKey = response[1];
				break;
			case 201: // Login accepted - new version available
				System.out.println("Login Accepted");
				System.out.println("New version of AniDB api available! - Please notify the developer");
				sessionKey = response[1];
				break;
			case 500: // Login failed
				throw(new Error("ANIDB API ERROR - Login failed"));
			case 503: // Client version outdated
				throw(new Error("ANIDB API ERROR - Client version outdated"));
			case 504: // Client banned
				throw(new Error("ANIDB API ERROR - Client banned"));
			case 505: // Illegal input or access denied
				throw(new Error("ANIDB API ERROR - Illegal input or access denied"));
			case 601: // AniDB out of service
				throw(new Error("ANIDB API ERROR - AniDB out of service - try again later"));
			default:
				throw(new Error("ANIDB API ERROR - UNSPECIFIED RESPONSE WHILE AUTHENTICATING"));
		}
		
		//Changes encoding into a more sensible one
		response = sendCommand("ENCODING name=UTF-8").split(" ");
		checkCommonResponseErrors(response);
		switch(Integer.parseInt(response[0])) {
			case 219: // Encoding successfully changed
				break;
			case 519:
				throw(new Error("ANIDB API ERROR - ENCODING NOT SUPPORTED"));
		}
	}
	
	public static void checkAnidbConnection() {
		// Open a new connection if one does not already exist
		// Server should close connection after 35 minutes of inactivity -> Open a new connection
		if(anidbConn==null || System.currentTimeMillis()-lastPacketSent>=2100000)
			anidbConn = new AnidbConnection();
	}
	
	// Checks input for the most common error codes
	public void checkCommonResponseErrors(String[] response) {
		// Checks possible error codes for all commands
		switch(Integer.parseInt(response[0])) {
			case 501:
				throw(new Error("ANIDB API ERROR - LOGIN FIRST"));
			case 502:
				throw(new Error("ANIDB API ERROR - ACCESS DENIED"));
			case 505:
				throw(new Error("ANIDB API ERROR - ILLEGAL INPUT OR ACCESS DENIED"));
			case 506:
				throw(new Error("ANIDB API ERROR - INVALID SESSION"));
			case 555:
				throw(new Error("ANIDB API ERROR - BANNED"));
			case 598:
				throw(new Error("ANIDB API ERROR - UNKNOWN COMMAND"));
			case 600:
				throw(new Error("ANIDB API ERROR - INTERNAL SERVER ERROR"));
			case 601:
				throw(new Error("ANIDB API ERROR - ANIDB OUT OF SERVICE - TRY AGAIN LATER"));
			case 602:
				throw(new Error("ANIDB API ERROR - SERVER BUSY - TRY AGAIN LATER"));
			case 604:
				throw(new Error("ANIDB API ERROR - TIMEOUT - DELAY AND RESUBMIT"));
		}
	}
	
	// Closes down the connection
	public void close() {
		sendCommand("LOGOUT");
		socket.close();
		System.out.println("AniDB session closed");
	}
	
	public String sendCommand(String cmd) {
		try {
			// Return response as a String
			// Only one package in each 2 seconds
			if(System.currentTimeMillis()-lastPacketSent<2000) {
				Thread.sleep(4000-(System.currentTimeMillis()-lastPacketSent));
			}
			byte[] buf = cmd.getBytes();
			DatagramPacket p = new DatagramPacket(buf, buf.length, SERVER);
			socket.send(p);
			lastPacketSent = System.currentTimeMillis();
			socket.receive(p);
			return new String(p.getData(), 0, p.getData().length);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
