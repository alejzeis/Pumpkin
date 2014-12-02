package pumpkin.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The logger for Pumpkin.
 * @author jython234
 *
 */
public class PumpkinLogger {
	private PrintStream out;
	private PrintStream err;
	
	private boolean toFile = false;
	private BufferedWriter fileOut;
	private BufferedWriter fileDebug;
	private BufferedWriter fileError;
	
	/**
	 * Create a new Logger using System.out, and System.err.
	 */
	public PumpkinLogger(){
		this.out = System.out;
		this.err = System.err;
	}
	
	/**
	 * Create a new Logger using the specified PrintStreams.
	 * @param out The stream for regular/warning data.
	 * @param err The stream for error data.
	 */
	public PumpkinLogger(PrintStream out, PrintStream err){
		this.out = out;
		this.err = err;
	}
	
	/**
	 * Create a new Logger using the specified PrintStreams, and the specified files.
	 * @param out The console stream for regular/warning data.
	 * @param err The console stream for error data.
	 * @param fileOut The file for regular/warning data.
	 * @param fileDebug The file for debug data.
	 * @param fileError The file for error data.
	 * @throws IOException If there is a problem with the files.
	 */
	public PumpkinLogger(PrintStream out, PrintStream err, File fileOut, File fileDebug, File fileError) throws IOException{
		this.out = out;
		this.err = err;
		
		this.fileOut = new BufferedWriter(new FileWriter(fileOut));
		this.fileDebug = new BufferedWriter(new FileWriter(fileDebug));
		this.fileError = new BufferedWriter(new FileWriter(fileError));
		
		toFile = true;
		
	}
	
	/**
	 * Log a DEBUG level message.
	 * @param message The message to be logged.
	 */
	public void debug(String message){
		log("DEBUG", message);
	}
	/**
	 * Log an INFO level message.
	 * @param message The message to be logged.
	 */	
	public void info(String message){
		log("INFO", message);
	}
	/**
	 * Log a WARNING level message.
	 * @param message The message to be logged.
	 */
	public void warn(String message){
		log("WARNING", message);
	}
	/**
	 * Log an ERROR level message.
	 * @param message The message to be logged.
	 */
	public void error(String message){
		log("ERROR", message);
	}
	
	
	private void log(String level, String message){
		String currentTime = formatTime();
		if(toFile){
			try{
				if(level == "DEBUG"){
					out.println(currentTime + " ["+level+"]: "+message);
					fileDebug.write(currentTime + " ["+level+"]: "+message);
				} else if(level == "INFO" || level == "WARNING"){
					out.println(currentTime + " ["+level+"]: "+message);
					fileOut.write(currentTime + " ["+level+"]: "+message);
				} else if(level == "ERROR"){
					err.println(currentTime + " ["+level+"]: "+message);
					fileError.write(currentTime + " ["+level+"]: "+message);
				} else {
					throw new RuntimeException("Unknown Level: "+level);
				}
			} catch(IOException e){
				throw new RuntimeException(e.getMessage());
			}
		} else {
			if(level == "DEBUG"){
				out.println(currentTime + " ["+level+"]: "+message);				
			} else if(level == "INFO" || level == "WARNING"){
				out.println(currentTime + " ["+level+"]: "+message);				
			} else if(level == "ERROR"){
				err.println(currentTime + " ["+level+"]: "+message);				
			} else {
				throw new RuntimeException("Unknown Level: "+level);
			}
		}
	}
	
	public void test(){
		log("INFO", "Running logger tests...");
		
		log("DEBUG", "Testing DEBUG");
		log("INFO", "Testing INFO");
		log("WARNING", "Testing WARNING");
		log("ERROR", "Testing ERROR");
		
		log("INFO", "Tests complete.");

	}
	
	public void destroy(){
		try {
			fileOut.close();
			fileError.close();
			fileDebug.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private String formatTime(){
		String time = "";
		String format = "[HH:mm:ss]";
		
		DateFormat dateformat = new SimpleDateFormat(format);
		Date date = new Date();
		
		time = dateformat.format(date);
		
		return time;
	}

}
