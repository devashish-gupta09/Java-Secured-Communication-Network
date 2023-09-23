import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class security_server {

    static String ISSUER_ID = "1X1DI";

    public static void main(String[] args) {

        server();

    }

    public static String generateAlphanumeric(String username, String password) {

        // Concatenate the username and password
        String concatenated = username + password;

        try {

            // Generate a hash of the concatenated string
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(concatenated.getBytes(StandardCharsets.UTF_8));

            // Convert the hash bytes to a hexadecimal string
            BigInteger hashInt = new BigInteger(1, hashBytes);
            String hashString = hashInt.toString(16);

            // Pad the hexadecimal string with leading zeros if necessary
            int paddingLength = (hashBytes.length * 2) - hashString.length();
            if (paddingLength > 0) {
                hashString = String.format("%0" + paddingLength + "d", 0) + hashString;
            }

            // Replace any non-alphanumeric characters with a random digit
            StringBuilder resultBuilder = new StringBuilder();
            for (char c : hashString.toCharArray()) {
                if (Character.isLetterOrDigit(c)) {
                    resultBuilder.append(c);
                } else {
                    int randomDigit = (int) (Math.random() * 10);
                    resultBuilder.append(randomDigit);
                }
            }

            return resultBuilder.toString();

        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error generating alphanumeric string: " + e.getMessage());
            return null;
        }
    }

    public static int get_certificate_number() {

        // Define the file path
        int currentValue = -1;
        String filePath = "sercurity_server\\cert_issued.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            // Read the existing value from the file
            currentValue = Integer.parseInt(reader.readLine());

            // Increment the value by 1
            int newValue = currentValue + 1;

            // Write the updated value back to the file

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(String.valueOf(newValue));
                System.out.println("File updated successfully!");
            } catch (IOException e) {

                System.err.println("Error writing to file: " + e.getMessage());
            }

        } catch (IOException e) {
            System.err.println("Error reading from file: " + e.getMessage());
        }

        return currentValue;

    }

    public static String get_logical_address(Socket s, String port) {

        String add = s.getInetAddress().toString() + "/" + port;
        return add;
    }

    public static void writeCertificate(String certificate_Number, String username, String password, String issuerId,
            String alphanumeric, String add) {
        try {
            // Open the file in append mode
            FileWriter writer = new FileWriter("sercurity_server\\certificates.txt", true);

            // Write the certificate data to the file
            writer.write(certificate_Number + "," + username + "," + password + "," + issuerId + "," + alphanumeric
                    + "," + add + "\n");

            // Close the file
            writer.close();

            System.out.println("Certificate data written to file successfully!");

        } catch (IOException e) {
            System.err.println("Error writing certificate data to file: " + e.getMessage());
        }
    }

    public static ArrayList<String []> readCertificates() {

        String fileName = "sercurity_server\\certificates.txt";
        String line = "";
        ArrayList<String []> certificates = new ArrayList<String []>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            while ((line = br.readLine()) != null) {
                // Split the line into parts using comma as the separator
                String[] parts = line.split(",");
                // Store each part in a variable
            
                certificates.add(parts);
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return certificates;
    }

    public static boolean register_certificate(Socket serve , String username , String password , String client_port) throws IOException, ClassNotFoundException{
        
        
         String cert_number = String.valueOf(get_certificate_number());
         String cert_name = generateAlphanumeric(username, password);
         String address = get_logical_address(serve, client_port);
         System.out.println("Cerficate issued number : " + cert_number);
         System.out.println("Certificate name : " + cert_name);
         System.out.println("Logical Address : " + address);

         // write in a file
         writeCertificate(cert_number, username, password, ISSUER_ID, cert_name, address);
         // Authenticate the user (in this example, always succeeds)
         boolean authenticated = true;

         // Send the authentication result to the client
        return authenticated;

    
    }
    public static boolean query(Socket serve , String username , String password ) throws IOException, ClassNotFoundException{
        
        
        boolean authenticated = false;
        String cert_name = generateAlphanumeric(username, password);
        
        ArrayList<String[]> cerificates =  readCertificates();
        System.out.println("Records Found : " + cerificates.size());

        for (String[] strings : cerificates) {

            // System.out.println("Matching "+ strings[4] + " == " + cert_name);

            if (strings[4].equals(cert_name)){

                authenticated =  true;
                System.out.println("Found");
                break;
            }
            
        }

         // Send the authentication result to the client
        return authenticated;

    
    }


    public static void server() {

        try {
            try (ServerSocket ss = new ServerSocket(8080)) {

                while (true) {

                    System.out.println("Waiting for connection .....");
                    Socket serve = ss.accept();
                    System.out.println("Connected to : " + serve.getInetAddress() + "/" + serve.getPort());
                
                    // input stream
                    ObjectInputStream inputStream = new ObjectInputStream(serve.getInputStream());
                    ObjectOutputStream out = new ObjectOutputStream(serve.getOutputStream());
                      // data to write
                    String username = (String) inputStream.readObject();
                    String password = (String) inputStream.readObject();
                    String client_port = (String) inputStream.readObject();
                    int req_type = (int) inputStream.readObject();

                    System.out.println("name - " + username);
                    System.out.println("password - " + password);
                    System.out.println("Request type : " + req_type);
                    out.flush();
                    boolean authenticated=false;
                    switch (req_type) {
                        
                        case 3:
                            authenticated =  register_certificate(serve, username, password, client_port);
                            break;
                        case 4:
                            // deregister
                            break;
                        case 5:
                            authenticated = query(serve, username, password);
                            break;
                        default:
                            break;
                    }
                    System.out.println("Auth - " + authenticated);
                    out.writeBoolean(authenticated);
                    System.out.println("Disconnected");
                    out.flush();
                    serve.close();
                    

                }
            }

        } catch (Exception e) {

            System.out.println("Error : " + e.getMessage());
        }

    }

}
