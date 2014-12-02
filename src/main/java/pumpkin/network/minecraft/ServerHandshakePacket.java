package pumpkin.network.minecraft;

import pumpkin.network.PEByteBuffer;

public class ServerHandshakePacket implements MinecraftPacket {
	
	public static final byte PID = MinecraftPackets.SERVER_HANDSHAKE;
	public byte[] cookie;
	public byte securityFlags;
	public short port;
	public byte[] dataArray;
	public byte[] unknown; //2 bytes
	public long session;
	public byte[] unknown2; //7 bytes
	
	private PEByteBuffer buf;
	
	public ServerHandshakePacket(byte[] data){
		buf = new PEByteBuffer(data);
	}
	
	public void encode(){ }
	
	public void decode(){ 
		buf.getByte(); //PID
		cookie = buf.getBytes(4);
		securityFlags = buf.getByte();
		port = buf.getShort();
		dataArray = buf.getBytes(71);
		unknown = buf.getBytes(2);
		session = buf.getLong();
		unknown2 = buf.getBytes(7);
	}
	
	
	public byte getEncapPID(){
		return (byte) 0x84;
	}
	
	public byte getEncapID(){
		return 0x60;
	}
	
	public PEByteBuffer getBuffer(){
		return buf;
	}

}
