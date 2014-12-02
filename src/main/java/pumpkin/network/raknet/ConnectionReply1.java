package pumpkin.network.raknet;

import pumpkin.network.PEByteBuffer;

public class ConnectionReply1 implements RakNetPacket {
	
	public final static byte PID = RakNetPackets.ID_OPEN_CONNECTION_REPLY_1;
	public byte[] MAGIC;
	public long serverID;
	public byte security;
	public short mtuSize;
	
	private PEByteBuffer buf;
	
	public ConnectionReply1(byte[] data){
		buf = new PEByteBuffer(data);
	}
	
	public void encode() { }
	
	public void decode(){
		buf.getByte(); //PID
		MAGIC = buf.getBytes(16); //MAGIC
		serverID = buf.getLong();
		security = buf.getByte();
		mtuSize = buf.getShort();
	}
	
	public PEByteBuffer getBuffer(){
		return buf;
	}

}
