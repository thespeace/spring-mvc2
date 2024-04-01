package thespeace.springmvc2.exception.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

/**
 * <h1>서블릿 예외 처리 - 필터</h1>
 * 예외 처리에 따른 필터와 서블릿이 제공하는 DispatchType를 이해해보자.
 * <ol>-예외 발생과 오류 페이지 요청 흐름
 *     <li>WAS(여기까지 전파) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(예외발생)</li>
 *     <li>WAS `/error-page/500` 다시 요청 -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤러(/error-page/500) -> View</li>
 * </ol>
 * 오류가 발생하면 오류 페이지를 출력하기 위해 WAS 내부에서 다시 한번 호출이 발생한다.<br>
 * 이때 필터/서블릿/인터셉터도 모두 다시 호출되는데, 로그인 인증 체크 같은 경우를 생각해보면,
 * 이미 한번 필터나, 인터셉터에서 로그인 체크를 완료했다.<br>
 * 따라서 서버 내부에서 오류 페이지를 호출한다고 해서 해당 필터나 인터셉트가 한번 더 호출되는 것은 매우 비효율적이다.<br><br>
 * 결국 클라이언트로 부터 발생한 정상 요청인지, 아니면 오류 페이지를 출력하기 위한 내부 요청인지 구분할 수 있어야 한다.<br>
 * 서블릿은 이런 문제를 해결하기 위해 DispatcherType 이라는 추가 정보를 제공한다.<br><br><br>
 *
 * <h2>DispatcherType</h2>
 * 서블릿 스펙은 실제 고객이 요청한 것인지, 서버가 내부에서 오류 페이지를 요청하는 것인지 DispatcherType 으로 구분할 수 있는 방법을 제공한다.
 * <blockquote><pre>
 *     //jakarta.servlet.DispatcherType
 *     public enum DispatcherType {
 *         FORWARD,
 *         INCLUDE,
 *         REQUEST,
 *         ASYNC,
 *         ERROR
 *     }
 * </pre></blockquote>
 * <ul>-DispatcherType
 *     <li>REQUEST : 클라이언트 요청</li>
 *     <li>ERROR : 오류 요청</li>
 *     <li>FORWARD : MVC에서 배웠던 서블릿에서 다른 서블릿이나 JSP를 호출할 때<br>
 *                   {@code RequestDispatcher.forward(request, response);}</li>
 *     <li>INCLUDE : 서블릿에서 다른 서블릿이나 JSP의 결과를 포함할 때<br>
 *                   {@code RequestDispatcher.include(request, response);}</li>
 *     <li>ASYNC : 서블릿 비동기 호출</li>
 * </ul>
 *
 */
@Slf4j
public class LogExFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("log exception filter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        String uuid = UUID.randomUUID().toString();

        try {
            log.info("EXCEPTIOM FILTER REQUEST [{}][{}][{}]", uuid, request.getDispatcherType(), requestURI);
            chain.doFilter(request, response);
        } catch (Exception e) {
            throw e;
        } finally {
            log.info("EXCEPTIOM FILTER RESPONSE [{}][{}][{}]", uuid, request.getDispatcherType(), requestURI);
        }
    }

    @Override
    public void destroy() {
        log.info("log exception filter destroy");
    }
}
