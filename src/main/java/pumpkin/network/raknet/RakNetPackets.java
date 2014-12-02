package pumpkin.network.raknet;

public class RakNetPackets {
	public static final byte ID_CONNECTED_PING_OPEN_CONNECTIONS = 0x01;
	public static final byte ID_OPEN_CONNECTION_REQUEST_1 = 0x05;
	public static final byte ID_OPEN_CONNECTION_REPLY_1 = 0x06;
	public static final byte ID_OPEN_CONNECTION_REQUEST_2 = 0x07;
	public static final byte ID_OPEN_CONNECTION_REPLY_2 = 0x08;
	
	public static final byte RAKNET_VERSION = 5;
	
	public static final byte DATA_PACKET_0 = (byte)0x80;
	public static final byte DATA_PACKET_1 = (byte)0x81;
	public static final byte DATA_PACKET_2 = (byte)0x82;
	public static final byte DATA_PACKET_3 = (byte)0x83;
	public static final byte DATA_PACKET_4 = (byte)0x84;
	public static final byte DATA_PACKET_5 = (byte)0x85;
	public static final byte DATA_PACKET_6 = (byte)0x86;
	public static final byte DATA_PACKET_7 = (byte)0x87;
	public static final byte DATA_PACKET_8 = (byte)0x88;
	public static final byte DATA_PACKET_9 = (byte)0x89;
	public static final byte DATA_PACKET_A = (byte)0x8a;
	public static final byte DATA_PACKET_B = (byte)0x8b;
	public static final byte DATA_PACKET_C = (byte)0x8c;
	public static final byte DATA_PACKET_D = (byte)0x8d;
	public static final byte DATA_PACKET_E = (byte)0x8e;
	public static final byte DATA_PACKET_F = (byte)0x8f;

	public static final byte NACK = (byte)0xa0;
	public static final byte ACK = (byte)0xc0;

}
