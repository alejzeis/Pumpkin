package pumpkin.network.raknet;

import pumpkin.network.PEByteBuffer;

public class ConnectionReply2 implements RakNetPacket {
	
	public final static byte PID = RakNetPackets.ID_OPEN_CONNECTION_REPLY_2;
	public byte[] MAGIC;
	public long serverID;
	public short clientPort;
	public short mtuSize;
	public byte security;
	
	private PEByteBuffer buf;
	
	public ConnectionReply2(byte[] data){
		buf = new PEByteBuffer(data);
	}
	
	public void encode(){ }
	
	public void decode(){
		buf.getByte(); //PID
		MAGIC = buf.getBytes(16);
		serverID = buf.getLong();
		clientPort = buf.getShort();
		mtuSize = buf.getShort();
		security = buf.getByte();
	}
	
	public PEByteBuffer getBuffer(){
		return buf;
	}

}
