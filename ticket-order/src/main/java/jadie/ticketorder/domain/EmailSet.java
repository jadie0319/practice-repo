package jadie.ticketorder.domain;

import java.util.HashSet;
import java.util.Set;

public class EmailSet {
    private Set<Email> emails = new HashSet<>();

    public EmailSet(Set<Email> emails) {
        this.emails = emails;
    }

    public Set<Email> getEmails() {
        return emails;
    }
}
