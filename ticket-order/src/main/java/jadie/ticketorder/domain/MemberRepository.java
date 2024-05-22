package jadie.ticketorder.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
