package network.packet;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class file_read {

    static Scanner sc = new Scanner(System.in);

    private static ArrayList<Integer> read_file(String inputFile) {

        ArrayList<Integer> v = new ArrayList<>();
        try (
                InputStream inputStream = new FileInputStream(inputFile);

        ) {
            int byteRead = -1;

            while ((byteRead = inputStream.read()) != -1) {

                v.add(byteRead);
            }

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return v;
    }

    public byte[] file_to_byte() {

        System.out.println("Enter the file to read : \n");
        String filename = sc.nextLine();

        ArrayList<Integer> data = read_file(filename);
        System.out.println(data.size() + " Bytes");
        System.out.println((data.size() / 1000) + " KB ~");
        System.out.println((data.size() / 1000000) + " MB ~");

        int n = data.size();
        byte[] byte_array = new byte[n];
        int i = 0;
        for (Integer c : data) {

            byte b = (byte) c.intValue();
            byte_array[i] = b;
            // System.out.println(b);
            i++;

        }
        return byte_array;
    }

    public void write_file(byte[] binaryData) {
        System.out.println("Enter the outputfile name : ");
        String fileName = sc.nextLine();
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            fos.write(binaryData);
            fos.close();
            System.out.println("Binary data written to file: " + fileName);
        } catch (IOException e) {
            System.out.println("Error writing binary data to file: " + fileName);
            e.printStackTrace();
        }

    }

}
