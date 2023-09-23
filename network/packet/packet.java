package network.packet;

import java.io.Serializable;

import certificate.certificate;

// packet class contains the packet attributes 

public class packet implements Serializable{

    public String client_id;
    public String client_ip;
    public String dest_ip;
    public int num_packets;
    public byte[] payload;
    public int pkid;
    public int _payload_capacity;
    public String name_msg;
    public certificate certificate;
    


}
