package pumpkin.network.raknet;

import pumpkin.network.Packet;

public interface RakNetPacket extends Packet{
	
	void decode();
	void encode();

}
