package thespeace.springmvc2.exception.resolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

/**
 * <h1>HandlerExceptionResolver</h1>
 * 스프링 MVC는 컨트롤러(핸들러) 밖으로 예외가 던져진 경우 예외를 해결하고, 동작을 새로 정의할 수 있는 방법을 제공한다.
 * 컨트롤러 밖으로 던져진 예외를 해결하고, 동작 방식을 변경하고 싶으면 HandlerExceptionResolver 를 사용하면 된다.
 * 줄여서 ExceptionResolver 라 한다.<br>
 * <blockquote><pre>
 * public interface HandlerExceptionResolver { //HandlerExceptionResolver - 인터페이스
 *     ModelAndView resolveException( HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex);
 *     //handler : 핸들러(컨트롤러) 정보
 *     //Exception ex : 핸들러(컨트롤러)에서 발생한 발생한 예외
 * }
 * </pre></blockquote>
 *
 * <h2>반환 값에 따른 동작 방식</h2>
 * <ul>-{@code HandlerExceptionResolver}의 반환 값에 따른 {@code DispatcherServlet}의 동작 방식은 다음과 같다.
 *     <li>빈 ModelAndView: new ModelAndView() 처럼 빈 ModelAndView 를 반환하면 뷰를 렌더링 하지 않고,
 *         정상 흐름으로 서블릿이 리턴된다</li>
 *     <li>ModelAndView 지정: ModelAndView 에 View , Model 등의 정보를 지정해서 반환하면 뷰를 렌더링 한다.</li>
 *     <li>null: null 을 반환하면, 다음 ExceptionResolver 를 찾아서 실행한다. 만약 처리할 수 있는
 *         ExceptionResolver 가 없으면 예외 처리가 안되고, 기존에 발생한 예외를 서블릿 밖으로 던진다.</li>
 * </ul>
 * <ul>-{@code ExceptionResolver} 활용
 *     <li>예외 상태 코드 변환
 *         <ul>
 *             <li>예외를 response.sendError(xxx) 호출로 변경해서 서블릿에서 상태 코드에 따른 오류를 처리하도록 위임</li>
 *             <li>이후 WAS는 서블릿 오류 페이지를 찾아서 내부 호출, 예를 들어서 스프링 부트가 기본으로 설정한 /error 가 호출됨</li>
 *         </ul>
 *     </li>
 *     <li>뷰 템플릿 처리
 *         <ul>
 *             <li>ModelAndView 에 값을 채워서 예외에 따른 새로운 오류 화면 뷰 렌더링 해서 고객에게 제공</li>
 *         </ul>
 *     </li>
 *     <li>API 응답 처리
 *         <ul>
 *             <li>response.getWriter().println("hello"); 처럼 HTTP 응답 바디에 직접 데이터를 넣어주는
 *                 것도 가능하다. 여기에 JSON 으로 응답하면 API 응답 처리를 할 수 있다.</li>
 *         </ul>
 *     </li>
 * </ul>
 */
@Slf4j
public class MyHandlerExceptionResolver implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        log.info("call resolver", ex);

        try {
            if (ex instanceof IllegalArgumentException) {
                log.info("IllegalArgumentException resolver to 400");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
                return new ModelAndView();
                //ExceptionResolver 가 ModelAndView 를 반환하는 이유는 마치 try, catch를 하듯이, Exception 을 처리해서
                //정상 흐름 처럼 변경하는 것이 목적이다. 이름 그대로 Exception 을 Resolver(해결)하는 것이 목적이다.
            }

        } catch (IOException e) {
            log.error("resolver ex", e);
        }

        return null;
    }
}
