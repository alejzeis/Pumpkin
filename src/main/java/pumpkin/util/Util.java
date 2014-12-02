package pumpkin.util;

import java.nio.ByteBuffer;

import pumpkin.network.PEByteBuffer;

public class Util {
	
	public static int getTriad(byte[] data, int offset){
		return (int) (data[offset++] << 16 | data[offset++] << 8  | data[offset]);
	}
	public static int getTriad(PEByteBuffer bb){
		return (int) (bb.getByte() << 16 | bb.getByte() << 8 | bb.getByte());
	}
	public static int getLTriad(byte[] data, int offset){
		return (data[offset] & 0xff) | (data[offset+1] & 0xff) << 8 | (data[offset+2] & 0xff) << 16;
	}

	/*
	public static byte[] putTriad(int v){
		return put(v, 3, false);
	}
	public static byte[] putLTriad(int v){
		return put(v, 3, true);
	}
	*/
	
	public static byte[] putTriad(int i){
		byte b1, b2, b3;
		
		b3 = (byte)(i & 0xFF);
		b2 = (byte)((i >> 8) & 0xFF);
		b1 = (byte)((i >> 16) & 0xFF);
		
		byte[] triad = {b1, b2, b3};
		return triad;
	}
	
	public static byte[] put(int x, int len, boolean reverse){
		byte[] buffer = new byte[len];
		int shift = (len - 1) * 8;
		for(int i = 0; i < len; i++){
			buffer[reverse ? (len - i - 1):i] = (byte) (x >> shift);
			shift -= 8;
		}
		return buffer;
	}

}
