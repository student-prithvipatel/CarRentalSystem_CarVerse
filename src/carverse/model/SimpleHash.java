package carverse.model;

public class SimpleHash {
    public static int hash(String password) {
        int hash = 0;
        for (int i = 0; i < password.length(); i++) {
            hash = (hash + password.charAt(i) * (i + 1)) % 100000;
        }
        return hash;
    }
}
