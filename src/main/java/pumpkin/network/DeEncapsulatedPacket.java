package pumpkin.network;

import java.util.ArrayList;

import pumpkin.util.Util;

public class DeEncapsulatedPacket {
	
	public byte dataPacketID;
	public int count;
	public byte encapsulationID;
	public ArrayList<InternalPacket> dataPackets = new ArrayList<InternalPacket>();
	
	private PEByteBuffer buf;
	
	public DeEncapsulatedPacket(byte[] pkt){
		buf = new PEByteBuffer(pkt);
	}
	
	public void deEncapsulate(){
		dataPacketID = buf.getByte();
		count = Util.getTriad(buf);
		encapsulationID = buf.getByte();
		
		while(buf.getPosition() < buf.getLength()){
			if(encapsulationID == 0x00){
				short dpLength = buf.getShort();
				dpLength = (short) (dpLength / 8); //Bits to Bytes
				if(dpLength == 0){
					break;
				}
				
				InternalPacket dataPacket = new InternalPacket(buf.getBytes(dpLength));
				dataPackets.add(dataPacket);
			} else if(encapsulationID == 0x40){
				short dpLength = buf.getShort();
				dpLength = (short) (dpLength / 8); //Bits to Bytes
				buf.getBytes(3); //Unknown 3 byte "count"
				
				InternalPacket dataPacket = new InternalPacket(buf.getBytes(dpLength));
				dataPackets.add(dataPacket);
			} else if(encapsulationID == 0x60){
				short dpLength = buf.getShort();
				dpLength = (short) (dpLength / 8); //Bits to Bytes
				buf.getBytes(3); //Unknown 3 byte "count"
				buf.getBytes(4); //Unknown 4 bytes
				
				InternalPacket dataPacket = new InternalPacket(buf.getBytes(dpLength));
				dataPackets.add(dataPacket);
			}
		}
		
	}

}
