package jadie.ticketorder.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Member {
    @Id
    private Long id;
    private String password;

    public void changePassword(String oldPw, String newPw) {

    }
}
