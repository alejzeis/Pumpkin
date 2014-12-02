package pumpkin;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Random;

import pumpkin.network.PacketManager;
import pumpkin.network.UnknownPacketException;
import pumpkin.network.minecraft.MinecraftPackets;
import pumpkin.util.ClientException;
import pumpkin.util.PumpkinLogger;

/**
 * Represents an implementation of a Minecraft PE client.
 * @author jython234
 *
 */
public class MinecraftClient {
	protected InetAddress address;
	protected int port;
	protected PumpkinLogger logger;
	protected PacketManager packetManager;
	protected DatagramSocket sock;
	protected Player child;
	protected ClientClock clock;
	
	public final String name;
	public final long startTime;
	public final long clientID;
	public final int protocol = MinecraftPackets.CURRENT_PROTOCOL;
	
	private boolean connected = false;
	
	/**
	 * Creates a new Minecraft PE client based on the following parameters.
	 * @param address The Address of the server.
	 * @param port The port of the server.
	 * @param name The name of the player.
	 * @param logger The logger to log to.
	 */
	public MinecraftClient(InetAddress address, int port, String name, PumpkinLogger logger){
		this.address = address;
		this.port = port;
		this.name = name;
		this.logger = logger;
		this.clientID = new Random().nextLong();
		this.startTime = System.currentTimeMillis();
	}
	
	
	/**
	 * Connect to the server.
	 */
	public void connect(){
		if(! connected){
			try{
				//setupLogger();
				sock = new DatagramSocket();
				child = new Player(this, true);
				packetManager = new PacketManager(sock, this);
				
				logger.info("Connecting to: "+address.getHostName()+":"+port);
				if(! packetManager.connect()){
					throw new ClientException("Failed to connect to server! Timeout.");
				}
				
				clock = new ClientClock(this);
				clock.start();
				
			} catch(IOException e){
				e.printStackTrace();
			} catch (UnknownPacketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			throw new ClientException("The client is already connected!");
		}
		//logger.destroy();
	}
	
	private void setupLogger() throws IOException{
		File logDir = new File("logs");
		File out = new File("logs"+File.separator+"clientOUT.log");
		File debug = new File("logs"+File.separator+"clientDEBUG.log");
		File error = new File("logs"+File.separator+"clientERROR.log");
		if(logDir.exists() && logDir.isDirectory()){
			
		} else {
			logDir.mkdir();
		}
		
		if(! out.exists()){
			out.createNewFile();
		}
		
		if(! debug.exists()){
			debug.createNewFile();
		}
		
		if(! error.exists()){
			error.createNewFile();
		}
		
		logger = new PumpkinLogger(System.out, System.err, out, debug, error);
		
	}
	
	public void stop(){
		if(clock != null){
			clock.stop();
		}
	}
	
	public PumpkinLogger getLogger(){
		return logger;
	}
	
	public InetAddress getAddress(){
		return address;
	}
	
	public int getPort(){
		return port;
	}
	
	public Player getPlayer(){
		return child;
	}

}
