package certificate;

import java.io.Serializable;

public class certificate implements Serializable {

    public String certificate_number = null;
    public String username = null;
    public String password = null;
    public String issure_id = null;
    public String logical_address = null;
    

    public void display_cerificate( certificate cert){


        System.out.println("Number : " + cert.certificate_number);
        System.out.println("Username : " + cert.username);
        System.out.println("Password : " + cert.password);
        System.out.println("Issuer ID : " + cert.issure_id);

    }

  
}
