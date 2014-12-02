package pumpkin.util;

@SuppressWarnings("serial")
public class ClientException extends RuntimeException {
	
	public ClientException(String reason){
		super(reason);
	}

}
