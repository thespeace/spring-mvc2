package thespeace.springmvc2.account.domain.login;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import thespeace.springmvc2.account.domain.member.Member;
import thespeace.springmvc2.account.domain.member.MemberRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;

    /**
     * @return null이면 로그인 실패
     */
    public Member login(String loginId, String password) {
        /*Optional<Member> findMemberOptional = memberRepository.findByLoginId(loginId);
        Member member = findMemberOptional.get();
        if(member.getPassword().equals(password)) {
            return member;
        } else {
            return null;
        }*/

        return memberRepository.findByLoginId(loginId)
                .filter(m -> m.getPassword().equals(password))
                .orElse(null);
    }
}
