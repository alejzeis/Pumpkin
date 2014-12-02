package pumpkin.network.minecraft;

import pumpkin.network.Packet;

public interface MinecraftPacket extends Packet {
	
	void encode();
	void decode();
	
	byte getEncapPID();
	byte getEncapID();

}
