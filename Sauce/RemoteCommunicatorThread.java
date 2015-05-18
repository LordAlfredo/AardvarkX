import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class RemoteCommunicatorThread extends Thread {
    public static final String group = "230.108.108.108";
    public static final int port = 1080;
	private Socket clientSocket = null;

	public RemoteCommunicatorThread(Socket socket) throws IOException{
		super("RemoteCommunicatorThread");
		this.clientSocket=socket;
	}
	public void run(){ 
		try{
		 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
	        BufferedReader in = new BufferedReader(
					new InputStreamReader(
					clientSocket.getInputStream()));

	        boolean nowImReadyToStart = false;
	        int option = -1;
	        boolean release = true;
	        String passwd = "";
			while (!nowImReadyToStart) {// wait for the gun
				String header = in.readLine();
				if (header != null) {
					if (header.equals("time")) {// It wants the time
						out.println(System.currentTimeMillis());
					} else if (header.equals("settings")) {// It wants to give
															// us settings
						option = Integer.parseInt(in.readLine());
						release = Boolean.parseBoolean(in.readLine());
						passwd = in.readLine();
					} else if (header.equals("ready?")) {
						String reason = "";
						if (option == -1)
							reason += "Something's wrong with those settings";
						out.println((reason.length() == 0) ? "true" : "false");
						out.println(reason);
					} else if (header.equals("go!")) {
						
						long goTime = Long.parseLong(in.readLine());
						nowImReadyToStart = true;
						// everybody clean up
						in.close();
						out.close();
						clientSocket.close();
						//BEEP
						Toolkit.getDefaultToolkit().beep();
						System.out.print('\007');
						// wait for it...
						try {
							Thread.sleep(goTime - System.currentTimeMillis());
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// It's go time!
						new Aardvark(option, release, passwd, true).setVisible(true);
					}
				}
			}
	        
		} catch (IOException e){e.printStackTrace();}
	}
}
