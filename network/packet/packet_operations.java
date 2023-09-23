package network.packet;

import java.util.Scanner;

import certificate.certificate;

public class packet_operations {

  private String client_id;
  private String client_ip;
  private String dest_ip;
  private String message;
  private String msg_name;
  private int payload;
  private String username;
  private String password;

  Scanner sc = new Scanner(System.in);

  // converting message into byte array

  private byte[] convert_msg_byte(String msg) {

    // length of message 

    int n = msg.length(); 
    char[] list = new char[n];

    // making array of characters from string
    for (int i = 0; i < list.length; i++) {
      list[i] = msg.charAt(i);
    }

    // making array of bytes 

    byte[] byte_info = new byte[n];
    for (int i = 0; i < byte_info.length; i++) {

      byte_info[i] = (byte) list[i];
    }

    return byte_info;

  }

  // set packet attributes

  private packet set_attributes(int number_of_packets , int pkt_count , String msg_name , int payload , certificate cert) {

    packet pkt = new packet();
    pkt.client_id = client_id;
    pkt.client_ip = client_ip;
    pkt.dest_ip = dest_ip;
    pkt.num_packets = number_of_packets;
    pkt.pkid = pkt_count;
    pkt.payload = new byte[payload];
    pkt.name_msg = msg_name;
    pkt.certificate = cert;

    return pkt;

  } 

  // display information about packets 

  private void packets_info(int payload , int n , int number_of_packets ){

    System.out.println("\n=================================================\n");
    System.out.println("Payload = " + payload);
    System.out.println("Total length : " + n);
    System.out.println("Number of pakcets : " + number_of_packets);
    System.out.println("\n=================================================\n");
  }

  // Make packets from byte array

  private packet[] make_packets(byte[] info, int payload) {

    // take_inputs();
    int n = info.length;
    int number_of_packets = (n / payload);
    int div = n % payload;

    // check if the message is a multpile of payload or not
    if (div != 0) {

      number_of_packets += 1;
    }

    // displaying the information about packets to be made
    packets_info(payload, n, number_of_packets);

    // creating a container for storing all the packets 

    packet[] packets = new packet[number_of_packets];

    // increment byte count to set bytes in each packet
    int byte_count = 0;

    // pakcet count for interating over packets
    int pkt_count = 1;


    // loop to set data for each packrt

    for (int index = 0; index < packets.length; index++) {

      // setting over the packet attributes 
      certificate cert = new certificate();
      cert.username = username;
      cert.password = password;
      packet pkt = set_attributes(number_of_packets , pkt_count , msg_name, payload , cert);

      // setting payload of each paket
      int count = payload;
      pkt._payload_capacity = payload;

      // if packet was not divisible by the payload then we hava to set the last packet capacity to new value
      if (div != 0 && index == packets.length - 1) {
        count = n % payload;
        pkt._payload_capacity = count;
      }

      // setting the bytes of packet
      for (int j = 0; j < count; j++) {

        pkt.payload[j] = info[byte_count++];

      }

      // adding packet to container
      packets[index] = pkt;

      // incrementing the packet count
      pkt_count++;


    }

    // returning all the packets 
    return packets;

  }

  // display packet attributes

  private void display_attributes(packet pkt){

    // display packet attributes

    System.out.println("\n--------------------------------------------------\n");
    System.out.println("Source ID : " + pkt.client_id);
    System.out.println("Source IP : " + pkt.client_ip);
    System.out.println("Destination IP : " + pkt.dest_ip);
    System.out.println("Packet ID : " + pkt.pkid);
    System.out.println("Number of packets : " + pkt.num_packets);
    System.out.println("Packet capacity : " + pkt._payload_capacity);
    System.out.println("Message name : " + pkt.name_msg);
    System.out.print("Data : ");
    for (int j = 0; j < pkt._payload_capacity; j++) {
      
      System.out.print(pkt.payload[j] + " ");
      
    }
    System.out.println("\n--------------------------------------------------\n");


  }

  // display packets
  private void packets_display(packet[] packets) {

    
    System.out.println("Packets Data: \n");
    int n = packets.length;

    // displaying each packet in container 

    for (int i = 0; i < n; i++) {

        display_attributes(packets[i]);
    }
  }

  // Convert packets to message

  private String convert_byte_msg(packet[] packets) {

    String ans = "";

    int n = packets.length;

    // iterating over the packets

    for (int i = 0; i < n; i++) {

      // iterting over the payload

      for (int j = 0; j < packets[i]._payload_capacity; j++) {

        // appending the each byte encoded into message and decoding it
        ans += (char) packets[i].payload[j];

      }

    }

    // returning decoded message 

    return ans;

  }
  

  // Get Client ID 
  
  private void get_client_id() {

    System.out.println("Enter the client id : ");
    client_id = sc.nextLine();

  }

  // Get Client IP address  

  private void get_client_ip() {

    System.out.println("Enter the client ip : ");
    client_ip = sc.nextLine();


  }

  // Get destination IP address 

  private void get_dest_ip() {

    System.out.println("Enter the destination ip : ");
    dest_ip = sc.nextLine();
  }

  // Take input message to be sent as input 

  private void get_input_msg(){

    System.out.println("Enter the message : ");
    message = sc.nextLine();

  }
  // get message name 
  private void get_msg_name(){

    System.out.println("Enter the message name : ");
    msg_name = sc.nextLine();

  }

  // Take payload as input 

  private void get_payload(){

    System.out.println("Enter the payload : ");
    payload = sc.nextInt();

  }

  private void get_username(){

    System.out.println("Enter the username : ");
    Scanner k = new Scanner(System.in);
    username = k.nextLine();
  

  }
  private void get_password(){

    System.out.println("Enter the password : ");
    Scanner k = new Scanner(System.in);
    password = k.nextLine();
    

  }


  
  // Take all inputs 

  private void take_inputs() {

    get_client_id();
    get_client_ip();
    get_dest_ip();
    get_msg_name();
    get_input_msg();
    get_payload();
    get_username();
    get_password();
  }
  
  private void take_inputs_file() {
    
    get_client_id();
    get_client_ip();
    get_dest_ip();
    get_msg_name();
    // get_input_msg();
    get_payload();
    get_username();
    get_password();
  }

  // Procedure to execute the packetizing and unpacketizing

  public packet[] excute_procedure(){

    packet_operations pkts = new packet_operations();
    pkts.take_inputs();
    packet[] packets = pkts.make_packets(pkts.convert_msg_byte(pkts.message), pkts.payload);
    pkts.packets_display(packets);
    System.out.println("\nMessage : " + pkts.convert_byte_msg(packets));
    System.out.println("\n");
    return packets;
    

  }

  public  packet[] execute_procedure_for_file(){

    packet_operations pkts = new packet_operations();
    pkts.take_inputs_file();
    file_read f = new file_read();
    byte [] byte_array = f.file_to_byte();
    packet[] packets = pkts.make_packets(byte_array, pkts.payload);
    pkts.packets_display(packets);
    System.out.println("\n");
    // f.write_file(byte_array);
    return packets;
    


  }

}
