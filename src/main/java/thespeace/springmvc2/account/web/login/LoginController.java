package thespeace.springmvc2.account.web.login;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import thespeace.springmvc2.account.domain.login.LoginService;
import thespeace.springmvc2.account.domain.member.Member;
import thespeace.springmvc2.account.web.SessionConst;
import thespeace.springmvc2.account.web.session.SessionManager;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final SessionManager sessionManager;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "account/login/loginForm";
    }

    /**
     * <h2>로그인(쿠키 사용)</h2>
     * 쿠키를 사용해서 로그인ID를 전달해서 로그인을 유지할 수 있다.<br>
     * 그런데 여기에는 심각한 보안 문제가 있다.
     * <ul>
     *     <li>쿠키 값은 임의로 변경할 수 있다.(클라이언트가 개발자모드로 변경가능)</li>
     *     <li>쿠키에 보관된 정보는 훔쳐갈 수 있다.</li>
     *     <li>해커가 쿠키를 한번 훔쳐가면 평생 사용할 수 있다.</li>
     * </ul>
     *
     * <ul>-대안
     *     <li>쿠키에 중요한 값을 노출하지 않고, 사용자 별로 예측 불가능한 임의의 랜덤 값(토큰)을 노출하고, 서버에서
     *         토큰과 사용자 id를 매핑해서 인식한다. 그리고 서버에서 토큰을 관리한다.</li>
     *     <li>토큰은 해커가 임의의 값을 넣어도 찾을 수 없도록 예상 불가능 해야 한다.</li>
     *     <li>해커가 토큰을 털어가도 시간이 지나면 사용할 수 없도록 서버에서 해당 토큰의 만료시간을 짧게(예: 30분)
     *         유지한다. 또는 해킹이 의심되는 경우 서버에서 해당 토큰을 강제로 제거하면 된다.</li>
     * </ul>
     */
    //@PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse response) {
        if(bindingResult.hasErrors()) {
            return "account/login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        if(loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "account/login/loginForm";
        }

        //로그인 성공 처리

        //쿠키에 시간 정보를 주지 않으면 세션 쿠키(브라우저 종료시 모두 종료)
        Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
        response.addCookie(idCookie);

        return "redirect:/account";
    }

    /**
     * <h2>로그인(직접 만든 세션 적용)</h2>
     */
    //@PostMapping("/login")
    public String loginV2(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse response) {
        if(bindingResult.hasErrors()) {
            return "account/login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        if(loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "account/login/loginForm";
        }

        //로그인 성공 처리

        //세션 관리자를 통해 세션을 생성하고, 회원 데이터 보관
        sessionManager.createSession(loginMember, response);

        return "redirect:/account";
    }

    /**
     * <h2>HttpSession 사용</h2>
     * 서블릿이 제공하는 HttpSession 도 결국 직접 만든 SessionManager 와 같은 방식으로 동작한다.<br>
     * 서블릿을 통해 HttpSession 을 생성하면 다음과 같은 쿠키를 생성한다.<br>
     * 쿠키 이름은 `JSESSIONID` 이고, 값은 추정 불가능한 랜덤 값이다.
     */
    //@PostMapping("/login")
    public String loginV3(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletRequest request) {
        if(bindingResult.hasErrors()) {
            return "account/login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        if(loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "account/login/loginForm";
        }

        //로그인 성공 처리

        //request.getSession(true)  : default는 true, 세션이 있으면 기존 세션 반환, 없으면 신규 세션을 생성하여 반환.
        //request.getSession(false) : 세션이 있으면 기존 세션을 반환, 없으면 새로운 세션을 생성하지 않고 `null`을 반환한다.
        HttpSession session = request.getSession(); // 세션 생성 : public HttpSession getSession(boolean create);

        //세션에 로그인 회원 정보 보관(메모리 저장)
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        return "redirect:/account";
    }

    /**
     * <h2>RedirectURL 처리</h2>
     * 로그인에 성공하면 처음 요청한 URL로 이동하는 기능 추가.
     */
    @PostMapping("/login")
    public String loginV4(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult,
                          @RequestParam(defaultValue = "/") String redirectURL,
                          HttpServletRequest request) {
        if(bindingResult.hasErrors()) {
            return "account/login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        if(loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "account/login/loginForm";
        }

        //로그인 성공 처리, //세션이 있으면 있는 세션 반환, 없으면 신규 세션 생성
        HttpSession session = request.getSession();

        //세션에 로그인 회원 정보 보관(메모리 저장)
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        //redirectURL 적용
        return "redirect:" + redirectURL;
    }

    /**
     * <h2>로그아웃(쿠키 사용)</h2>
     * <ul>
     *     <li>세션 쿠키이므로 웹 브라우저 종료시</li>
     *     <li>서버에서 해당 쿠키의 종료 날짜를 0으로 지정</li>
     * </ul>
     * 로그아웃도 응답 쿠키를 생성하는데 `Max-Age=0` 를 확인할 수 있다. 해당 쿠키는 즉시 종료된다.
     */
    //@PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        expireCookie(response, "memberId");
        return "redirect:/account";
    }

    /**
     * <h2>로그아웃(직접 만든 세션 적용)</h2>
     */
    //@PostMapping("/logout")
    public String logoutV2(HttpServletRequest request) {
        sessionManager.expire(request);
        return "redirect:/account";
    }

    /**
     * <h2>로그아웃(Servlet HTTP Session 1)</h2>
     */
    @PostMapping("/logout")
    public String logoutV3(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session != null) {
            session.invalidate(); //세션을 제거
        }
        return "redirect:/account";
    }

    private static void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

}
