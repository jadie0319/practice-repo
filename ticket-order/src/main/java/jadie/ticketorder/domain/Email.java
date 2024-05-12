package jadie.ticketorder.domain;

public class Email {
    private String value;

    public Email(String value) {
        this.value = value;
    }

    public String getAddress() {
        return value;
    }
}
