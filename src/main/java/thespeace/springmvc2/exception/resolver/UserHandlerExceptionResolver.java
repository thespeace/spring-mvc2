package thespeace.springmvc2.exception.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import thespeace.springmvc2.exception.exception.UserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <h1>API 예외 처리 - HandlerExceptionResolver 활용</h1>
 * <h2>예외를 여기서 마무리하기</h2>
 * 예외가 발생하면 WAS까지 예외가 던져지고, WAS에서 오류 페이지 정보를 찾아서 다시 `/error`를 호출하는 과정은
 * 생각해보면 너무 복잡하다. ExceptionResolver 를 활용하면 예외가 발생했을 때 이런 복잡한 과정 없이 여기에서
 * 문제를 깔끔하게 해결할 수 있다.<br><br>
 *
 * <h2>정리</h2>
 * ExceptionResolver를 사용하면 컨트롤러에서 예외가 발생해도 ExceptionResolver에서 예외를 처리해버린다.<br>
 * 따라서 예외가 발생해도 서블릿 컨테이너까지 예외가 전달되지 않고, 스프링 MVC에서 예외 처리는 끝이 난다.
 * 결과적으로 WAS 입장에서는 정상 처리가 된 것이다. 이렇게 `예외를 이곳에서 모두 처리할 수 있다는 것이 핵심`이다.<br><br>
 *
 * 서블릿 컨테이너까지 예외가 올라가면 복잡하고 지저분하게 추가 프로세스가 실행된다. 반면에 ExceptionResolver를
 * 사용하면 예외처리가 상당히 깔끔해진다.<br><br>
 *
 * 직접 ExceptionResolver 를 구현해 보았는데, 실무에서는 스프링이 제공하는 ExceptionResolver을 사용하자!
 */
@Slf4j
public class UserHandlerExceptionResolver implements HandlerExceptionResolver {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {

            if(ex instanceof UserException) {
                log.info("UserException resolver to 400");
                String acceptHeader = request.getHeader("accept");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                if("application/json".equals(acceptHeader)) { //HTTP 요청 해더의 ACCEPT 값이 application/json 이면 JSON으로 오류 반환.
                    Map<String, Object> errorResult = new HashMap<>();
                    errorResult.put("ex", ex.getClass());
                    errorResult.put("message", ex.getMessage());

                    String result = objectMapper.writeValueAsString(errorResult);

                    response.setContentType("application/json");
                    response.setCharacterEncoding("utf-8");
                    response.getWriter().write(result);
                    return new ModelAndView(); //예외를 먹어버리고 정상적으로 반환.
                } else { // 그 외 경우에는 `error/500`에 있는 HTML 오류 페이지를 반환.
                    // TEXT/HTML
                    return new ModelAndView("error/500");
                }
            }

        } catch (IOException e) {
            log.error("resolver ex", e);
        }

        return null;
    }
}
