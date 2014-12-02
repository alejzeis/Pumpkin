package pumpkin.network.minecraft;

import java.util.Random;

import pumpkin.MinecraftClient;
import pumpkin.network.PEByteBuffer;
import pumpkin.util.Util;

public class ClientHandshakePacket implements MinecraftPacket {
	public static final byte encapPID = (byte) 0x84;
	public static final byte encapID = 0x60;
	
	public static final byte PID = MinecraftPackets.CLIENT_HANDSHAKE;
	public int cookie = 0x043f57fe;
	public byte security = (byte) 0xfb;
	public short port;
	//public byte unknownArrayLength; 
	//public byte[] unknownArray;
	//public byte[] dataArray;
	public short timestamp;
	public long session;
	public long session2;
	 //67
	
	private PEByteBuffer buf;
	
	public ClientHandshakePacket(MinecraftClient client){
		port = (short) client.getPort();
		session = new Random().nextLong();
		session2 = new Random().nextLong();
		timestamp = (short) client.startTime;
		
		buf = new PEByteBuffer(94);
	}
	
	public void encode(){ 
		buf.putByte(PID);
		buf.putInt(cookie);
		buf.putByte(security);
		buf.putShort(port);
		putDataArray1();
		putDataArray2();
		buf.putShort(timestamp);
		buf.putLong(session);
		buf.putLong(session2);
	}
	
	public void decode(){ }
	
	private void putDataArray1(){
		buf.putBytes(new byte[22]);
	}
	
	private void putDataArray2(){
		for(int i = 0; i < 9; i++){
			byte[] tri = Util.putTriad(2);
			buf.putBytes(tri);
			buf.putBytes(new byte[2]);
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
