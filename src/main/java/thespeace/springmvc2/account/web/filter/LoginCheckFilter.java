package thespeace.springmvc2.account.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;
import thespeace.springmvc2.account.web.SessionConst;

import java.io.IOException;

/**
 * <h2>서블릿 필터 - 인증 체크</h2>
 *
 */
@Slf4j
public class LoginCheckFilter implements Filter {

    //화이트 리스트 경로는 인증과 무관하게 항상 허용, 화이트 리스트를 제외한 나머지 모든 경로에는 인증 체크 로직을 적용.
    private static final String[] whiteList = {"/","/account", "/members/add", "/login", "/logout", "/css/*"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            log.info("인증 체크 필터 시작 {}", requestURI);

            if(isLoginCheckPath(requestURI)) {
                log.info("인증 체크 로직 실행 {}", requestURI);
                HttpSession session = httpRequest.getSession(false);
                if(session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {

                    log.info("미인증 사용자 요청 {}" , requestURI);

                    //미인증 사용자는 로그인 화면으로 리다이렉트, 인증 사용자는 이전에 접속 실패한 URL로 리다이렉트!
                    httpResponse.sendRedirect("/login?redirectURL=" + requestURI);
                    return; //여기가 중요, 미인증 사용자는 다음으로 진행하지 않고 끝!
                            //이후 필터는 물론 서블릿, 컨트롤러가 더는 호출되지 않는다.
                            //앞서 redirect 를 사용했기 때문에 redirect 가 응답으로 적용되고 요청이 끝난다.
                }
            }

            chain.doFilter(request, response);
        } catch (Exception e) {
            throw e; //예외 로깅 가능 하지만, 톰캣까지 예외를 보내주어야 한다(WAS까지 올려주어야 한다).
        } finally {
            log.info("인증 체크 필터 종료 {} ", requestURI);
        }

    }

    /**
     * <h2>화이트 리스트를 제외한 모든 경우에 인증 체크 로직을 적용</h2>
     */
    private boolean isLoginCheckPath(String requestURI) {
        return !PatternMatchUtils.simpleMatch(whiteList, requestURI);
    }
}
