package carverse;

import java.sql.Date;

public class CustomerNode {
    int id;
    String name;
    String email;
    String phone;
    String address;
    Date dob;
    CustomerNode next;

    public CustomerNode(int id, String name, String email, String phone, String address, Date dob) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.dob = dob;
        this.next = null;
    }
}
