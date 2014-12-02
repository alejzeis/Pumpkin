package pumpkin;

public class ClientClock {
	
	private MinecraftClient client;
	private boolean running = false;
	private int tps = 20;
	private long currentTick = 0;
	private long nextPingAt = 80;
	
	
	public ClientClock(MinecraftClient client){
		this.client = client;
	}
	
	public void start(){
		running = true;
		run();
	}
	
	public void stop(){
		running = false;
	}
	
	private void run(){
		client.getLogger().info("ClientClock started.");
		while(running){
			int ms = 1000 / tps;
			currentTick++;
			pingServer();
			try {
				Thread.sleep(ms);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void pingServer(){
		if(currentTick == nextPingAt){
			nextPingAt = nextPingAt + 80;
			client.child.ping();
			//client.getLogger().info("Ping Server!");
		}
	}

}
