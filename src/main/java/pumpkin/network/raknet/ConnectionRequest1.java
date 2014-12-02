package pumpkin.network.raknet;

import pumpkin.network.PEByteBuffer;

public class ConnectionRequest1 implements RakNetPacket {
	
	public static final byte PID = RakNetPackets.ID_OPEN_CONNECTION_REQUEST_1;
	public static final byte[] MAGIC = new byte[16];
	public static final byte raknetVersion = RakNetPackets.RAKNET_VERSION;
	public static final byte[] nullPayload = new byte[531];
	
	private PEByteBuffer buf;
	
	public ConnectionRequest1(){
		buf = new PEByteBuffer(18 + 531);
	}
	
	public void encode(){
		buf.putByte(PID);
		buf.putMAGIC();
		buf.putByte(raknetVersion);
		buf.putBytes(nullPayload);
	}
	
	public void decode() { }
	
	public PEByteBuffer getBuffer(){
		return buf;
	}

}
