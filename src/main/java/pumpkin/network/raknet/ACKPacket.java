package pumpkin.network.raknet;

import pumpkin.network.PEByteBuffer;
import pumpkin.util.Util;

public class ACKPacket implements RakNetPacket {
	
	public final static byte PID = RakNetPackets.ACK;
	public final short unknownShort = 1;
	public byte aditionalPacket; //True when there is only ONE packet recieved
	public int packetNumber1;
	public int packetNumber2;
	
	private PEByteBuffer buf;
	
	public ACKPacket(int packetNumber){
		packetNumber1 = packetNumber;//Util.putTriad(packetNumber);
		aditionalPacket = 0x00;
		
		buf = new PEByteBuffer(7);
	}
	
	public ACKPacket(int packetNumber, int packetNumber2){
		packetNumber1 = packetNumber; //Util.putTriad(packetNumber);
		this.packetNumber2 = packetNumber2; //Util.putTriad(packetNumber2);
		aditionalPacket = 0x01;
		
		buf = new PEByteBuffer(10);
	}
	
	public ACKPacket(byte[] data){
		buf = new PEByteBuffer(data);
	}
	
	public void encode(){
		if(aditionalPacket == 0x00){
			buf.putByte(PID);
			buf.putByte((byte) 0x00);
			buf.putBytes(Util.putTriad(packetNumber1));
		} else {
			buf.putByte(PID);
			buf.putByte((byte) 0x01);
			buf.putBytes(Util.putTriad(packetNumber1));
			buf.putBytes(Util.putTriad(packetNumber2));
		}
	}
	
	public void decode(){
		buf.getByte(); //PID
		buf.getShort(); //Unknown
		aditionalPacket = buf.getByte();
		packetNumber1 = Util.getTriad(buf);
		if(aditionalPacket == 0x01){
			packetNumber2 = Util.getTriad(buf);
		}
	}
	
	public PEByteBuffer getBuffer(){
		return buf;
	}

}
