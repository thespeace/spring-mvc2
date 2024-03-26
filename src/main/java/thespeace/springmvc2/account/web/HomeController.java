package thespeace.springmvc2.account.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import thespeace.springmvc2.account.domain.member.Member;
import thespeace.springmvc2.account.domain.member.MemberRepository;
import thespeace.springmvc2.account.web.session.SessionManager;

@Slf4j
@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class HomeController {

    private final MemberRepository memberRepository;
    private final SessionManager sessionManager;

//    @GetMapping
    public String home() {
        return "/account/home";
    }

//    @GetMapping
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

//    @GetMapping
    public String homeLoginV2(HttpServletRequest request, Model model) {

        //세션 관리자에 저장된 회원 정보 조회
        Member member = (Member) sessionManager.getSession(request);

        //로그인 시
        if(member == null) {
            return "/account/home";
        }

        model.addAttribute("member", member);
        return "/account/login/loginHome";
    }

//    @GetMapping
    public String homeLoginV3(HttpServletRequest request, Model model) {

        HttpSession session = request.getSession(false); //세션을 메모리를 사용하기때문에 꼭 필요할때만 사용.
        if(session == null) {
            return "/account/home";
        }

        Member loginMember = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);

        //세션에 회원 데이터가 없으면 home
        if(loginMember == null) {
            return "/account/home";
        }

        //세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        return "/account/login/loginHome";
    }

    /**
     * <h2>@SessionAttribute</h2>
     * 이미 로그인 된 사용자를 찾을 때 사용된다. 참고로 이 기능은 세션을 생성하지 않는다.
     */
    @GetMapping
    public String homeLoginV3Spring(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember, Model model) {

        //세션에 회원 데이터가 없으면 home
        if(loginMember == null) {
            return "/account/home";
        }

        //세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        return "/account/login/loginHome";
    }
}