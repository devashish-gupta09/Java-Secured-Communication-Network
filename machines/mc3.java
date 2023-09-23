package machines;

import java.util.Map;
import java.util.Scanner;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import network.packet.file_read;
import network.packet.packet;
import network.packet.packet_operations;

public class mc3 {
    public static ArrayList<packet> packets = new ArrayList<packet>();
    public static Map<String, ArrayList<packet>> buffer = new HashMap<>();

    public static void main(String[] args) {
        Storing storing = new Storing(buffer, packets);
        Thread storingThread = new Thread(storing);
        storingThread.start();
        receive();
        // server_req();
    }

    public static void receive() {

        try {

            ServerSocket serverSocket = new ServerSocket(5002);

            // Listen for incoming requests indefinitely
            while (true) {
                // Accept incoming connections

                try {
                    System.out.println("MC2 Server waiting .......\n");
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Connected : " + clientSocket.getInetAddress() + "/" + clientSocket.getPort());

                    ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());

                    packet pkts = (packet) in.readObject();

                    packets.add(pkts);

                    System.out.println("Packets in buffer : " + packets.size());
                    out.writeBoolean(true);
                    // Handle the incoming request on a separate thread
                    out.flush();
                    clientSocket.close();
                    System.out.println("Disconnected");
                } catch (Exception e) {

                    System.out.println("ERROR : " + e.getMessage());
                }

            }

        } catch (Exception e) {
            System.out.println("ERROR : " + e.getMessage());
        }
    }

    public static void send() {

        try {

            Socket socket = new Socket("localhost", 8081);
            System.out.println("Connected to Server");

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            packet_operations op = new packet_operations();
            packet[] pkts = op.execute_procedure_for_file();

            out.writeObject(pkts);

            out.flush();
            boolean auth = in.readBoolean();
            socket.close();
            System.out.println("Disconnected");

        } catch (Exception e) {

            System.out.println("ERROR : " + e.getMessage());
        }

    }

    public static void server_req() {
        try {

            Socket s = new Socket("localhost", 8080);

            ObjectOutputStream ious = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream iis = new ObjectInputStream(s.getInputStream());
            ious.writeObject("Devashish");
            ious.writeObject("Gupta");
            ious.writeObject("asds");

            // boolean auth = iis.readBoolean();
            // if (auth) {
            // // buffer.add(packet);
            // System.out.println("Accpeted");
            // }
            ious.close();
            s.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}

class Storing implements Runnable {

    private Map<String, ArrayList<packet>> buffer;
    private ArrayList<packet> packets;

    public Storing(Map<String, ArrayList<packet>> buffer, ArrayList<packet> packets) {
        this.packets = packets;
        this.buffer = buffer;
    }

    public void run() {
        while (true) {
            // Do some work with the buffer
            // For example, print out its size every second

            try {

                while (packets.size() > 0) {

                    packet pkt = packets.remove(0);
                    String key = pkt.name_msg;
                    int cur_packs = 0;
                    int num_packets = pkt.num_packets;
                    ArrayList<packet> pkts;
                    if (buffer.containsKey(key)) {

                        pkts = buffer.get(key);
                        pkts.add(pkt);
                        buffer.put(key, pkts);
                        cur_packs += pkts.size();
                    } else {

                        pkts = new ArrayList<packet>();
                        pkts.add(pkt);
                        buffer.put(key, pkts);
                        cur_packs++;

                    }

                    if (cur_packs == num_packets) {
                        
                        int n = 0;
                        for (packet packet : pkts) {

                            n += packet._payload_capacity;
                        }
                        
                        Collections.sort(pkts , new Comparator<packet>() {
                            public int compare(packet p1, packet p2) {
                                return p1.pkid - p2.pkid;
                            }
                        });

                        byte [] bytes_data = new byte[n];
                        int k = 0;
                        for (packet packet : pkts) {

                            n += packet._payload_capacity;
                            for (int i = 0; i < packet._payload_capacity; i++) {
                               
                                bytes_data[k] = packet.payload[i];
                                k++;

                            }
                            
                        }
                        String fileName = pkt.name_msg;
                        try {
  
                            FileOutputStream fos = new FileOutputStream(fileName);
                            fos.write(bytes_data);
                            fos.close();
                            System.out.println("Binary data written to file: " + fileName);
                        } catch (IOException e) {
                            System.out.println("Error writing binary data to file: " + fileName);
                            e.printStackTrace();
                        }
                        

                        buffer.remove(key);
                    }

                }
                Thread.sleep(1000);

                System.out.println("Buffered files : " + buffer.size());
            } catch (InterruptedException e) {
                System.out.println("Storing thread interrupted: " + e.getMessage());
            }
        }
    }
}