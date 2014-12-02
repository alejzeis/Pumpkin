package pumpkin.network.minecraft;

import pumpkin.network.PEByteBuffer;

public class ClientConnectPacket implements MinecraftPacket {
	public static final byte encapPID = (byte) 0x84;
	public static final byte encapID = 0x40;
	
	public static final byte PID = MinecraftPackets.CLIENT_CONNECT;
	public long clientID;
	public long session;
	public final byte unknown = 0;
	
	private PEByteBuffer buf;
	
	public ClientConnectPacket(long session, long clientID){
		this.session = session;
		this.clientID = clientID;
		
		buf = new PEByteBuffer(18);
	}
	
	public void encode(){
		buf.putByte(PID);
		buf.putLong(clientID);
		buf.putLong(session);
		buf.putByte(unknown);
	}
	
	public void decode(){ }
	
	
	public byte getEncapPID(){
		return encapPID;
	}
	
	public byte getEncapID(){
		return encapID;
	}
	
	public PEByteBuffer getBuffer(){
		return buf;
	}

}
