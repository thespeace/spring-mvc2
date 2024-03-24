package thespeace.springmvc2.account.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import thespeace.springmvc2.account.domain.member.Member;
import thespeace.springmvc2.account.domain.member.MemberRepository;

@Slf4j
@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class HomeController {

    private final MemberRepository memberRepository;

//    @GetMapping
    public String home() {
        return "/account/home";
    }

    @GetMapping
    public String homeLogin(@CookieValue(name = "memberId", required = false) Long memberId, Model model) {
        if(memberId == null) {
            return "/account/home";
        }

        //로그인 시
        Member loginMember = memberRepository.findById(memberId);
        if(loginMember == null) {
            return "/account/home";
        }

        model.addAttribute("member", loginMember);
        return "/account/login/loginHome";
    }
}
