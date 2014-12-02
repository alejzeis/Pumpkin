package pumpkin;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import pumpkin.network.DeEncapsulatedPacket;
import pumpkin.network.PacketManager;
import pumpkin.network.minecraft.ClientHandshakePacket;
import pumpkin.network.minecraft.LoginPacket;
import pumpkin.network.minecraft.MinecraftPacket;
import pumpkin.network.minecraft.MinecraftPackets;
import pumpkin.network.minecraft.PingPacket;
import pumpkin.network.minecraft.ServerHandshakePacket;

public class Player {
	public final MinecraftClient parent;
	public final boolean isLocalClient;
	
	public final String name;
	
	public double x;
	public double y;
	public double z;
	public double yaw;
	public double pitch;
	
	protected ArrayList<MinecraftPacket> packetBuffer;
	protected long currentIdentifier;
	
	public Player(MinecraftClient client, boolean isLocal){
		parent = client;
		name = client.name;
		isLocalClient = isLocal;
	}
	
	protected synchronized void addToQueue(MinecraftPacket p){ //TODO: Actually buffer the packet
		byte[] data = PacketManager.encapsulatePacket(p, parent.packetManager.getNextCount());
		try {
			parent.packetManager.sendPacket(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void handlePacket(DatagramPacket p){
		byte epid = p.getData()[0];
		int elen = p.getLength();
		DeEncapsulatedPacket dep = new DeEncapsulatedPacket(p.getData());
		dep.deEncapsulate();
		
		for(int i = 0; i < dep.dataPackets.size(); i++){
			byte[] packet = dep.dataPackets.get(i).data;
			byte pid = packet[0];
			int len = packet.length;
			
			switch(pid){
			case MinecraftPackets.SERVER_HANDSHAKE:
				parent.getLogger().debug("ServerHandshake.");
				ServerHandshakePacket shp = new ServerHandshakePacket(packet);
				shp.decode();
				parent.getLogger().debug("Session is: "+shp.session+" Our session: "+parent.packetManager.session);
				
				ClientHandshakePacket chp = new ClientHandshakePacket(parent);
				chp.encode();
				addToQueue(chp);
				parent.getLogger().debug("Sent clientHandshake.");
				
				LoginPacket lp = new LoginPacket(parent);
				lp.encode();
				addToQueue(lp);
				parent.getLogger().debug("Sent Login.");
				
				break;
				
			case MinecraftPackets.START_GAME:
				
			default:
				parent.getLogger().warn("[at pumpkin.Player.handlePacket()]: FAILED TO HANDLE PACKET! Unknown packet!");
				parent.getLogger().debug("PID: "+pid+", Length: "+len+", Dump: "+Arrays.toString(p.getData()));
			}
		}
		
	}
	
	protected void ping(){
		currentIdentifier = new Random().nextLong();
		PingPacket p = new PingPacket();
		p.encode();
		
		//parent.getLogger().debug("Ping!");
		//addToQueue(p);
	}

}
