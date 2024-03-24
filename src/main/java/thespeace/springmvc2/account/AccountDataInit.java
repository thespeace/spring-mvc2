package thespeace.springmvc2.account;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import thespeace.springmvc2.account.domain.member.Member;
import thespeace.springmvc2.account.domain.member.MemberRepository;

@Component
@RequiredArgsConstructor
public class AccountDataInit {

    private final MemberRepository memberRepository;

    /**
     * 테스트용 데이터 추가
     */
    @PostConstruct
    public void init() {
        Member member = new Member();
        member.setLoginId("test");
        member.setPassword("test!");
        member.setName("테스터");

        memberRepository.save(member);
    }
}
