package carverse.model;

import java.sql.Date;

public class CustomerList {
    private CustomerNode head;
    private int size = 0;

    public void insertCustomer(int customer_id, String name, String email, String phone_no, String address, Date dob) {
        CustomerNode newNode = new CustomerNode(customer_id, name, email, phone_no, address, dob);
        if (head == null) {
            head = newNode;
        } else {
            CustomerNode temp = head;
            while (temp.next != null) temp = temp.next;
            temp.next = newNode;
        }
        size++;
    }

    public void clear() {
        head = null;
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public CustomerNode head() {
        return head;
    }

    public int size() {
        return size;
    }
}
