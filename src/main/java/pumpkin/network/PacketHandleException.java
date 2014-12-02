package pumpkin.network;

@SuppressWarnings("serial")
public class PacketHandleException extends RuntimeException {
	
	public PacketHandleException(){
		super("Failed to handle packet.");
	}
	
	public PacketHandleException(String s){
		super(s);
	}

}
