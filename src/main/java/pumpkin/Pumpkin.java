package pumpkin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Properties;

import pumpkin.util.PumpkinLogger;

public class Pumpkin {
	private InetAddress ip;
	private int port;
	private String name;
	private boolean isInteractive;
	
	public void run(String[] args){
		try{
			String arg = args[0];
			if(arg == "-i"){ //Interactive
				isInteractive = true;
			} else if(arg == "-a"){ //Automated
				isInteractive = false;
			} else { //Unknown
				isInteractive = true; //Default
			}
		} catch(ArrayIndexOutOfBoundsException e){
			//Default to interactive mode
			isInteractive = true;
		}
		try{
			PumpkinLogger logger = setupLogger();
			loadConfig();
			logger.info("Loaded config.");
			logger.info("Interactive mode: "+isInteractive);
			MinecraftClient client = new MinecraftClient(ip, port, name, logger);
			client.connect();
			
		} catch(IOException e){
			System.err.println("Failed to setup Utilities!\nExit 1");
			e.printStackTrace();
			System.exit(1);
		}
	}

	
	private PumpkinLogger setupLogger() throws IOException{
		File logDir = new File("logs");
		File out = new File("logs"+File.separator+"clientOUT.log");
		File debug = new File("logs"+File.separator+"clientDEBUG.log");
		File error = new File("logs"+File.separator+"clientERROR.log");
		if(logDir.exists() && logDir.isDirectory()){
			
		} else {
			logDir.mkdir();
		}
		
		if(! out.exists()){
			out.createNewFile();
		}
		
		if(! debug.exists()){
			debug.createNewFile();
		}
		
		if(! error.exists()){
			error.createNewFile();
		}
		
		return new PumpkinLogger(System.out, System.err, out, debug, error);
		
	}
	
	private void loadConfig() throws IOException{
		File conf = new File("client.conf");
		if( conf.exists() && conf.isFile()){
			
		} else {
			conf.createNewFile();
		}
		BufferedReader reader = new BufferedReader(new FileReader("client.conf"));
		Properties prop = new Properties();
		prop.load(reader);
		
		ip = InetAddress.getByName(prop.getProperty("ip", "localhost"));
		port = Integer.parseInt(prop.getProperty("port", "19132"));
		name = prop.getProperty("name", "pumpkin");
	}
	
	
	public static void main(String[] args) {
		Pumpkin pumpkin = new Pumpkin();
		pumpkin.run(args);

	}

}
