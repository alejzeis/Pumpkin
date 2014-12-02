package pumpkin.network.minecraft;

import pumpkin.MinecraftClient;
import pumpkin.network.PEByteBuffer;

public class LoginPacket implements MinecraftPacket {
	public static final byte encapPID = (byte) 0x84;
	public static final byte encapID = 0x40;
	
	public static final byte PID = MinecraftPackets.LOGIN;
	public String username;
	public int protocol1;
	public int protocol2;
	public long clientID;
	public byte[] realmsData = new byte[2];
	
	private PEByteBuffer buf;
	
	public LoginPacket(MinecraftClient client){
		username = client.name;
		clientID = client.clientID;
		protocol1 = client.protocol;
		protocol2 = client.protocol;
		
		buf = new PEByteBuffer(22 + username.length());
	}
	
	public void encode(){
		buf.putByte(PID);
		buf.putString(username);
		buf.putInt(protocol1);
		buf.putInt(protocol2);
		buf.putLong(clientID);
		//buf.putShort((short) 2);
		buf.putBytes(realmsData);
	}
	
	public void decode() { }
	
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
