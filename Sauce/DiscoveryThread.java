import java.io.*;
import java.net.*;

public class DiscoveryThread extends Thread {

    MulticastSocket socket = null;
    static final String groupAddr = "230.108.108.108";
    static final int port = 1080;
    InetAddress localhost = InetAddress.getLocalHost();
    

    public DiscoveryThread() throws IOException {
    	this("DiscoveryThread");
    }

    public DiscoveryThread(String name) throws IOException {
        super(name);
        socket = new MulticastSocket(port);
        socket.joinGroup(InetAddress.getByName(groupAddr));
    
    }

    public void run() {
    	boolean quit = false;
        while (!quit) {
            try {
                byte[] buf = new byte[256];

                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("DATA RECEIVED: "+received);
                if (received.toLowerCase().startsWith("aardvarkxremote discovery request")){
                	String response = "aardvarkx discovery response\n" + localhost.getHostAddress() + "\n" + port +"\n"+localhost.getHostName()+"\n"+System.getProperty("user.name");
                	buf = response.getBytes();
                	 InetAddress address = packet.getAddress();
                     int port = packet.getPort();
                     packet = new DatagramPacket(buf, buf.length, address, port);
                     socket.send(packet);
                }
                else if (received.toLowerCase().startsWith("aardvarkxremote quit command"))
                	quit = true;
                

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socket.close();
        
    }
}
