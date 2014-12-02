package pumpkin.network.minecraft;

import java.util.Random;

import pumpkin.network.PEByteBuffer;
import pumpkin.network.PacketHandleException;

//THIS IS A TWO WAY PACKET!

/**
 * Represents a PING/PONG packet. Encapsulated.
 * @author jython234
 *
 */
public class PingPacket implements MinecraftPacket {
	public static final byte encapPID = (byte) 0x84;
	public static final byte encapID = 0x00;
	
	public static final byte PID = MinecraftPackets.PING;
	public long identifier;
	
	private PEByteBuffer buf;
	private boolean isPing = true;
	
	public PingPacket(long identifier){
		this.identifier = identifier;
		buf = new PEByteBuffer(9);
	}
	
	public PingPacket(){
		identifier = new Random().nextLong();
		buf = new PEByteBuffer(9);
	}
	
	public PingPacket(byte[] data){
		buf = new PEByteBuffer(data);
		isPing = false;
	}
	
	public void encode(){
		if(isPing){
			buf.putByte(PID);
			buf.putLong(identifier);
		} else{
			throw new PacketHandleException("Can not encode packet when it is set to be decoded!");
		}
	}
	
	public void decode() { 
		if(! isPing){
			buf.getByte(); //PID
			identifier = buf.getLong();
		} else {
			throw new PacketHandleException("Can not decode packet when it is set to be encoded!");
		}
	}

	
	public byte getEncapPID() {
		return encapPID;
	}

	public byte getEncapID() {
		return encapID;
	}
	
	public PEByteBuffer getBuffer() {
		return buf;
	}

	

}
