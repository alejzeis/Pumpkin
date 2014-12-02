package pumpkin.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Random;

import pumpkin.MinecraftClient;
import pumpkin.network.minecraft.ClientConnectPacket;
import pumpkin.network.minecraft.MinecraftPacket;
import pumpkin.network.raknet.ACKPacket;
import pumpkin.network.raknet.ConnectionReply1;
import pumpkin.network.raknet.ConnectionReply2;
import pumpkin.network.raknet.ConnectionRequest1;
import pumpkin.network.raknet.ConnectionRequest2;
import pumpkin.network.raknet.RakNetPackets;
import pumpkin.util.ClientException;
import pumpkin.util.Util;

public class PacketManager extends Thread{
	private DatagramSocket sock;
	private MinecraftClient client;
	private boolean running = false;
	private int pktTimeout = 5;
	public final long session;
	private int currentPacket = 0;
	private int packetsACK = 0;
	private int packetsNACK = 0;
	
	public PacketManager(DatagramSocket sock, MinecraftClient client){
		this.sock = sock;
		this.client = client;
		this.session = new Random().nextLong();
	}
	
	public void Start(){
		if(!running){
			running = true;
			start();
		} else {
			throw new RuntimeException("Thread already running.");
		}
	}
	
	public void Stop() throws InterruptedException{
		if(running){
			running = false;
			join();
		} else {
			throw new RuntimeException("Thread not running.");
		}
	}
	
	public void run(){
		while(running){
			try{
				if(pktTimeout == 0){
					client.getLogger().error("Packet Timeout! No packets recieved for a while!");
					client.stop();
					running = false;
				}
				sock.setSoTimeout(2000);
				DatagramPacket p = new DatagramPacket(new byte[1024], 1024);
				
				sock.receive(p);
				if(pktTimeout < 5){
					pktTimeout++;
				}
				handlePacket(p);
			} catch(SocketTimeoutException e){
				pktTimeout = pktTimeout - 1;
			} catch(SocketException e){
				client.getLogger().error("SocketException (pumpkin.network.PacketManager.run): "+e.getMessage());
			} catch(IOException e){
				client.getLogger().error("IOException (pumpkin.network.PacketManager.run): "+e.getMessage());
			}
		}
	}
	
	public void handlePacket(DatagramPacket p){
		byte pid = p.getData()[0];
		int len = p.getLength();
		
		try{
			if(pid >= 0x01 && pid <= 0x1D){ //RakNet login packets range
				try{
					handleRakNetPacket(p);
				} catch(IOException e){
					client.getLogger().warn("Failed to handle packet, "+pid);
					client.getLogger().error("IOException: "+e.getMessage());
				}
			} else if(pid >= RakNetPackets.DATA_PACKET_0 && pid <= RakNetPackets.DATA_PACKET_F){ //Data packet range
				DeEncapsulatedPacket dep = new DeEncapsulatedPacket(p.getData());
				dep.deEncapsulate();
				if(dep.count > currentPacket){
					client.getLogger().warn("Missing Packet!");
					//TODO: Send NACK
					currentPacket++;
				}
				ackPacket(p);
				client.getPlayer().handlePacket(p);
			} else if(pid == RakNetPackets.ACK){
				handleACK(p);
			} else if(pid == RakNetPackets.NACK){
				handleNACK(p);
			} else {
				client.getLogger().warn("FAILED TO HANDLE PACKET! Unknown packet!");
				client.getLogger().debug("PID: "+pid+", Length: "+len+", Dump: "+Arrays.toString(p.getData()));
			}
		} catch(IOException e){
			client.getLogger().warn("FAILED TO HANDLE PACKET! IOException");
			client.getLogger().error("IOException: "+e.getMessage());
		}
	}
	
	private void handleACK(DatagramPacket p){
		ACKPacket ack = new ACKPacket(p.getData());
		ack.decode();
		client.getLogger().debug("ACK: "+"Aditional packet: "+ack.aditionalPacket+", count: "+ack.packetNumber1);
		//currentPacket++;
		packetsACK++;
	}
	
	private void ackPacket(DatagramPacket p) throws IOException{
		DeEncapsulatedPacket dep = new DeEncapsulatedPacket(p.getData());
		dep.deEncapsulate();
		ACKPacket ack = new ACKPacket(dep.count);
		client.getLogger().debug("Sent ack. count: "+dep.count);
		ack.encode();
		sendPacket(ack);
	}
	
	private void handleNACK(DatagramPacket p){
		client.getLogger().warn("NACK");
		packetsNACK++;
	}
	
	
	private Packet handleRakNetPacket(DatagramPacket p) throws IOException{
		byte pid = p.getData()[0];
		int len = p.getLength();
		
		switch(pid){
		case RakNetPackets.ID_OPEN_CONNECTION_REPLY_1:
			ConnectionReply1 cr1 = new ConnectionReply1(p.getData());
			cr1.decode();
			return cr1;
			
		case RakNetPackets.ID_OPEN_CONNECTION_REPLY_2:
			ConnectionReply2 cr2 = new ConnectionReply2(p.getData());
			cr2.decode();
			client.getLogger().debug("Recieved 0x08");
			
			ClientConnectPacket ccp = new ClientConnectPacket(session, client.clientID);
			ccp.encode();
			byte[] buffer = encapsulatePacket(ccp, currentPacket);
			sendPacket(buffer);
			client.getLogger().debug("Sent 0x09");
			
			break;
			
		default:
			client.getLogger().warn("Unknown packet recieved! Skipped 1 packet ("+len+" bytes).");
			client.getLogger().debug("Unknown PacketID: "+pid+", dump: "+Arrays.toString(p.getData()));
			return null;
		}
		return null;
		
	}
	
	
	public boolean connect() throws IOException, UnknownPacketException{
		boolean success = false;
		
		sock.setSoTimeout(500);
		
		DatagramPacket p = null;
		long startTime = System.currentTimeMillis();
		boolean contact = false;
		for(int tries = 1; tries < 5; tries++){
			ConnectionRequest1 cr1 = new ConnectionRequest1();
			cr1.encode();
			
			client.getLogger().debug("Sent 0x05, try: "+tries);
			DatagramPacket cr1P = new DatagramPacket(cr1.getBuffer().toBytes(), cr1.getBuffer().toBytes().length, client.getAddress(), client.getPort());
			sock.send(cr1P);
			try{
				p = new DatagramPacket(new byte[1024], 1024);
				sock.receive(p);
				contact = true;
				break;
			} catch(SocketTimeoutException e){
				
			}
		}
		
		if(contact){
			long totalTime = System.currentTimeMillis() - startTime;
			client.getLogger().info("Recieved reply from server in "+totalTime+" ms.");
			rakNetConnect(p);
			success = true;
		} else {
			client.getLogger().info("No response from server.");
			success = false;
		}
		return success;
	}

	private void rakNetConnect(DatagramPacket p) throws UnknownPacketException, IOException {
		byte PID = p.getData()[0];
		if(PID != RakNetPackets.ID_OPEN_CONNECTION_REPLY_1){
			throw new UnknownPacketException("Unknown Packet: "+PID);
		} else {
			Packet pk = handleRakNetPacket(p);
			if(pk instanceof ConnectionReply1){
				try{
					ConnectionReply1 cr1 = (ConnectionReply1) pk;
					
					ConnectionRequest2 cr2 = new ConnectionRequest2(client, cr1);
					cr2.encode();
					sendPacket(cr2);
					client.getLogger().debug("Sent 0x07");
					
					Start();
					
				} catch(IOException e){
					throw new ClientException(e.getMessage());
				}
				
			} else {
				throw new ClientException("Wrong packet recieved!");
			}
		}
	}
	
	/**
	 * Send a Packet to the specified server.
	 * @param p The packet to be sent (must implement Packet).
	 * @param address The address of the server.
	 * @param port The port of the server.
	 * @throws IOException If the packet can't be sent.
	 */
	public void sendPacket(Packet p, InetAddress address, int port) throws IOException{
		byte[] bytes = p.getBuffer().toBytes();
		DatagramPacket dp = new DatagramPacket(bytes, bytes.length, address, port);
		
		sock.send(dp);
		if(p instanceof MinecraftPacket){
			currentPacket++;
		}
	}
	
	/**
	 * Send a packet to the server that is specified in the client.
	 * @param p The packet to be sent (must implement Packet).
	 * @throws IOException If the packet can't be sent.
	 */
	public void sendPacket(Packet p) throws IOException{
		byte[] bytes = p.getBuffer().toBytes();
		DatagramPacket dp = new DatagramPacket(bytes, bytes.length, client.getAddress(), client.getPort());
		
		sock.send(dp);
		if(p instanceof MinecraftPacket){
			currentPacket++;
		}
	}
	/**
	 * Send a packet to the server that is specified in the client.
	 * @param data The packet's buffer.
	 * @throws IOException If the packet can't be sent.
	 */
	public void sendPacket(byte[] data) throws IOException{
		DatagramPacket dp = new DatagramPacket(data, data.length, client.getAddress(), client.getPort());
		
		sock.send(dp);
		currentPacket++;
	}
	/**
	 * Gets the next count value, and increments the current one.
	 * @return The next count value.
	 */
	public synchronized int getNextCount(){
		currentPacket++;
		return currentPacket;
	}
	
	
	/**
	 * Encapsulate a packet.
	 * @param packet The packet to be encapsulated (must implement MinecraftPacket)
	 * @return A byte array of the final, encapsulated packet.
	 */
	public static byte[] encapsulatePacket(MinecraftPacket packet, int count){
		byte[] buf = new byte[packet.getBuffer().toBytes().length];
		buf = packet.getBuffer().toBytes();
		int len = 0;
		PEByteBuffer bb = null;
		
		if(packet.getEncapID() == 0x00){
			len = packet.getBuffer().toBytes().length + 7;
			bb = new PEByteBuffer(len);
			
			bb.putByte(packet.getEncapPID());
			bb.putBytes(Util.putTriad(count));
			
			bb.putShort((short) (packet.getBuffer().toBytes().length * 8)); //Length in bits
			bb.putBytes(buf);
		} else if(packet.getEncapID() == 0x40){
			len = buf.length + 10;
			//System.out.println("Total Length is: "+len);
			//System.out.println("DP Length is: "+buf.length);
			bb = new PEByteBuffer(len);
			
			bb.putByte(packet.getEncapPID());
			//System.out.println("(Encap PID): At "+bb.getPosition()+" byte(s)");
			bb.putBytes(Util.putTriad(count));
			//System.out.println("(Count): At "+bb.getPosition()+" byte(s)");
			
			bb.putByte(packet.getEncapID());
			//System.out.println("(Encap ID): At "+bb.getPosition()+" byte(s)");
			
			bb.putShort((short) (packet.getBuffer().toBytes().length * 8)); //Length in bits
			//System.out.println("(DP Length): At "+bb.getPosition()+" byte(s)");
			bb.putBytes(new byte[3]); //Unknown "count"
			//System.out.println("(Unknown Count): At "+bb.getPosition()+" byte(s)");
			bb.putBytes(buf);
			//System.out.println("(DP): At "+bb.getPosition()+" byte(s)");
		} else if(packet.getEncapID() == 0x60){
			len = packet.getBuffer().toBytes().length + 16;
			bb = new PEByteBuffer(len);
			
			bb.putByte(packet.getEncapPID());
			bb.putBytes(Util.putTriad(count));
			
			bb.putShort((short) (packet.getBuffer().toBytes().length * 8)); //Length in bits
			bb.putBytes(new byte[3]); //Unknown "count"
			bb.putBytes(new byte[4]); //Unknown "unknown"
			bb.putBytes(buf);
		}
		
		buf = bb.toBytes();
		
		return buf;
		
	}

}
