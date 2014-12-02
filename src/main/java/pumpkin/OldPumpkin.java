package pumpkin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

import pumpkin.util.PumpkinLogger;

/**
 * The (old) default run class for Pumpkin.
 * @author jython234
 *
 */
public class OldPumpkin {

	public static void main(String[] args) {
		/*
		Scanner scanner = new Scanner(System.in);
		System.out.print("Welcome to Pumpkin!\nEnter an IP to connect to: ");
		
		String ip = scanner.nextLine();
		
		System.out.print("Enter the port of "+ip+": ");
		int port = scanner.nextInt();
		
		System.out.print("Enter your name (ONLY a-z, 1-9, and _): ");
		String name = scanner.nextLine();
		
		System.out.println();
		
		scanner.close();
		*/
		Scanner scanner = new Scanner(System.in);
		
		
		try {
			PumpkinLogger logger = setupLogger();
			logger.info("Welcome to Pumpkin!");
			//System.out.println();
			//logger.test();
			System.out.println();
			
			logger.info("Searching for connection files...");
			
			File[] configs = readConnectionFiles();
			if(configs.length == 0){
				logger.error("Did not find any server connection files.");
				System.exit(1);
			}
			logger.info("Found "+configs.length+" server connection files.");
			for(int i =0; i < configs.length; i++){
				System.out.println("["+i+"]: "+configs[i].getName());
			}
			logger.info("Enter a number (1-"+configs.length+")");
			int selection = scanner.nextInt();
			if(selection <= configs.length || selection == 1){
				MinecraftClient client = readConnectionFile(configs[selection - 1], logger);
				client.connect();
			} else {
				logger.error("Invalid entry!");
				System.exit(1);
			}
			
			//InetAddress addr = InetAddress.getByName(ip);
			//MinecraftClient client = new MinecraftClient(addr, port, name);
			//client.connect();
		} catch (UnknownHostException e) {
			e.printStackTrace(System.err);
		} catch (IOException e){
			e.printStackTrace(System.err);
		}

	}
	
	private static File[] readConnectionFiles(){
		File[] files;
		ArrayList<File> connectionFiles = new ArrayList<File>();
		File folder = new File(".");
		files = folder.listFiles();
		
		for(int i = 0; i < files.length; i++){
			if(files[i].isFile() && files[i].getName().endsWith(".mcs")){
				connectionFiles.add(files[i]);
			}
		}
		if(connectionFiles.size() == 0){
			return new File[0];
		} else {
			File[] connectionFiles2 = new File[connectionFiles.size()];
			for(int i = 0; i < connectionFiles.size(); i++){
				connectionFiles2[i] = connectionFiles.get(i);
			}
			return connectionFiles2;
		}
		
	}
	
	private static MinecraftClient readConnectionFile(File file, PumpkinLogger logger) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(file));
		Properties prop = new Properties();
		prop.load(reader);
		
		InetAddress addr = InetAddress.getByName(prop.getProperty("ip"));
		int port = Integer.parseInt(prop.getProperty("port", "19132"));
		String name = prop.getProperty("name", "pumpkin");
		
		return new MinecraftClient(addr, port, name, logger);
	}
	
	private static PumpkinLogger setupLogger() throws IOException{
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

}
