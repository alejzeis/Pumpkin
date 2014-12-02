package pumpkin.network;

public class InternalPacket {
	public final byte PID;
	public final byte[] data;
	public final int length;
	
	public InternalPacket(byte[] data){
		PID = data[0];
		this.data = data;
		length = data.length;
	}
	

}
