package thespeace.springmvc2.account.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

/**
 * <h2>모든 요청을 로그로 남기는 필터.</h2>
 */
@Slf4j
public class LogFilter implements Filter { //필터를 사용하려면 필터 인터페이스를 구현해야 한다.

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("log filter init");
    }

    /**
     * <ul>
     *     <li>HTTP 요청이 오면 doFilter 가 호출된다.</li>
     *     <li>ServletRequest request 는 HTTP 요청이 아닌 경우까지 고려해서 만든 인터페이스이다.
     *         HTTP를 사용하면 HttpServletRequest httpRequest = (HttpServletRequest) request; 와 같이
     *         다운 케스팅 하면 된다.</li>
     * </ul>
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("log filter doFilter");

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        //사용자 구분을 위해 요청당 임의의 uuid 를 생성.
        String uuid = UUID.randomUUID().toString();

        try {
            log.info("REQUEST [{}][{}]", uuid, requestURI);
            chain.doFilter(request, response); //다음 Filter가 있으면 호출, 없으면 서블릿 호출.(만약 이 로직을 호출하지 않으면 다음 단계로 진행되지 않는다.)
        } catch (Exception e) {
            throw e;
        } finally {
            log.info("RESPONSE [{}][{}]", uuid, requestURI);
        }

    }

    @Override
    public void destroy() {
        log.info("log filter destroy");
    }
}
