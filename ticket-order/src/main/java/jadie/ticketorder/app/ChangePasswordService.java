package jadie.ticketorder.app;

import jadie.ticketorder.domain.Member;
import jadie.ticketorder.domain.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ChangePasswordService {
    private final MemberRepository memberRepository;

    public ChangePasswordService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void changePassword(Long memberId, String oldPw, String newPw) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(EntityNotFoundException::new);
        member.changePassword(oldPw, newPw);
    }
}
