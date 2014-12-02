package pumpkin.network.raknet;

import pumpkin.MinecraftClient;
import pumpkin.network.PEByteBuffer;

public class ConnectionRequest2 implements RakNetPacket {
	
	public final static byte PID = RakNetPackets.ID_OPEN_CONNECTION_REQUEST_2;
	public final static byte[] MAGIC = PEByteBuffer.MAGIC;
	public byte[] securityCookie = PEByteBuffer.hexToBytes("0x043f57fefd");
	public short serverPort;
	public short mtuSize;
	public long clientID;
	
	private PEByteBuffer buf;
	
	public ConnectionRequest2(MinecraftClient client, ConnectionReply1 cr1){
		serverPort = (short) client.getPort();
		mtuSize = cr1.mtuSize;
		clientID = client.clientID;
		
		buf = new PEByteBuffer(35);
	}
	
	public void encode(){
		buf.putByte(PID);
		buf.putMAGIC();
		buf.putBytes(securityCookie);
		buf.putShort(serverPort);
		buf.putShort(mtuSize);
		buf.putLong(clientID);
	}
	
	public void decode() { }
	
	public PEByteBuffer getBuffer(){
		return buf;
	}

}
