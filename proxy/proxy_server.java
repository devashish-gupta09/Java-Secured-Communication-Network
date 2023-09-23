package proxy;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import network.packet.packet;

public class proxy_server {
    private static ArrayList<packet> buffer = new ArrayList<>();

    public static void main(String[] args) {
        int port = 8081;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started");

            // Start the receiver thread
            Thread receiverThread = new Thread(new ReceiverThread(serverSocket, buffer));
            receiverThread.start();

            // Start the sender thread
            Thread senderThread = new Thread(new SenderThread(buffer));
            senderThread.start();

        } catch (IOException e) {
            System.out.println("Error starting server");
            e.printStackTrace();

        }
    }
}

class ReceiverThread implements Runnable {
    
    private ServerSocket serverSocket;
    private ArrayList<packet> buffer;

    public ReceiverThread(ServerSocket serverSocket, ArrayList<packet> buffer) {
        this.serverSocket = serverSocket;
        this.buffer = buffer;
    }

    public void run() {
        try {

            while (true) {

                System.out.println("Waiting for connection .....");
                Socket serve = serverSocket.accept();
                System.out.println("Connected to : " + serve.getInetAddress() + "/" + serve.getPort());

                // input stream
                ObjectInputStream inputStream = new ObjectInputStream(serve.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(serve.getOutputStream());
                // data to write
                packet[] pkts = (packet[]) inputStream.readObject();

                for (packet packet : pkts) {

                    try {

                        Socket s = new Socket("localhost", 8080);
                        ObjectOutputStream ious = new ObjectOutputStream(s.getOutputStream());
                        ObjectInputStream iis = new ObjectInputStream(s.getInputStream());
                        ious.writeObject(packet.certificate.username);
                        ious.writeObject(packet.certificate.password);
                        ious.writeObject(packet.client_id);
                        ious.writeObject(5);
                        boolean auth = iis.readBoolean();
                        System.out.println("Auth = " + auth);
                        if (auth) {
                            System.out.println("Packet added to Buffer");
                            buffer.add(packet);
                        }

                        ious.flush();
                        s.close();
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                }
                out.writeBoolean(true);
                System.out.println("Disconnected");
                out.flush();
                serve.close();

            }

        } catch (Exception e) {

            System.out.println("Error : " + e.getMessage());
        }
    }
}

class SenderThread implements Runnable {
    private ArrayList<packet> buffer;

    public SenderThread(ArrayList<packet> buffer) {
        this.buffer = buffer;
    }

    public void run() {

        while (true) {
            System.out.println("Packets in buffer : " + buffer.size());
            if (!buffer.isEmpty()) {
                // Get the request from the buffer
                packet request = buffer.remove(0);
                System.out.println("Sending packet");
                try {

                    Socket socket = new Socket( request.dest_ip,5000);
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    out.writeObject(request);
                    boolean auth = in.readBoolean();
                    if (auth) {
                        System.out.println("Packet sent ! ");
                    } else {
                        buffer.add(request);
                    }
                    out.close();
                    socket.close();

                } catch (Exception e) {

                    buffer.add(request);
                }

                // Connect to the client and send the request

            }
        }
    }
}
