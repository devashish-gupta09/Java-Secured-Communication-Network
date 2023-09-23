package machines;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import network.packet.packet;
import network.packet.packet_operations;

public class mc1{

    public static void main(String[] args) {
        send();
    }

    

    public static void send(){

        try {
            Socket socket = new Socket("localhost", 8081);
            System.out.println("Connected to Server");
    
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        
            packet_operations op = new packet_operations();
            packet[] pkts = op.execute_procedure_for_file();
        
            out.writeObject(pkts);
            out.flush();
        
            boolean success = in.readBoolean();
            System.out.println("Data sent successfully");
        
            socket.close();
            System.out.println("Disconnected");
            
        } catch (Exception e) {

            System.out.println("ERROR : " + e.getMessage());
        }
        
    }
    
}
