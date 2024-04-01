package thespeace.springmvc2.exception.servlet;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

/**
 * <h1>서블릿 예외 처리 - 시작</h1>
 * 스프링이 아닌 순수 서블릿 컨테이너는 예외를 어떻게 처리하는지 알아보자.
 * <ol>-서블릿은 다음 2가지 방식으로 예외 처리를 지원한다.
 *     <li>Exception(예외)</li>
 *     <li>response.sendError(HTTP 상태 코드, 오류 메시지)</li>
 * </ol>
 */
@Slf4j
@Controller
@RequestMapping("/error")
public class ServletExController {

    /**
     * <h2>Exception(예외)</h2>
     *
     * <h3>자바 직접 실행</h3>
     * 자바의 메인 메서드를 직접 실행하는 경우 main 이라는 이름의 쓰레드가 실행된다.<br>
     * 실행 도중에 예외를 잡지 못하고 처음 실행한 main() 메서드를 넘어서 예외가 던져지면,
     * 예외 정보를 남기고 해당 쓰레드는 종료된다.<br><br>
     *
     * <br>
     *
     * <h2>웹 애플리케이션</h2>
     * 웹 애플리케이션은 사용자 요청별로 별도의 쓰레드가 할당되고, 서블릿 컨테이너 안에서 실행된다.<br>
     * 애플리케이션에서 예외가 발생했는데, 어디선가 try ~ catch로 예외를 잡아서 처리하면 아무런 문제가 없다.<br>
     * 그런데 만약에 애플리케이션에서 예외를 잡지 못하고, 서블릿 밖으로 까지 예외가 전달되면 어떻게 동작할까?<br>
     * {@code WAS(여기까지 전파) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(예외발생)}<br><br>
     *
     * <h2>WAS 예외 전달 테스트</h2>
     * test url을 실행해보면 다음처럼 tomcat이 기본으로 제공하는 오류 화면을 볼 수 있다.
     * Exception 의 경우 서버 내부에서 처리할 수 없는 오류가 발생한 것으로 생각해서 HTTP 상태 코드 500을 반환한다.
     * @see <a href="http://localhost:8080/error/error-ex">test url</a>
     */
    @GetMapping("/error-ex")
    public void errorEx() {
        throw new RuntimeException("예외 발생!");
    }

    /**
     * <h2>response.sendError(HTTP 상태 코드)</h2>
     * 오류가 발생했을 때 HttpServletResponse 가 제공하는 sendError 라는 메서드를 사용해도 된다.<br>
     * 이것을 호출한다고 당장 예외가 발생하는 것은 아니지만, 서블릿 컨테이너에게 오류가 발생했다는 점을 전달할 수 있다.<br>
     * 이 메서드를 사용하면 HTTP 상태 코드와 오류 메시지도 추가할 수 있다.<br><br>
     *
     * <ul>-sendError 흐름
     *     <li>WAS(sendError 호출 기록 확인) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(response.sendError())</li>
     *     <li>response.sendError() 를 호출하면 response 내부에는 오류가 발생했다는 상태를 저장해둔다.<br>
     *         그리고 서블릿 컨테이너는 고객에게 응답 전에 response 에 sendError() 가 호출되었는지 확인한다.
     *         그리고 호출되었다면 설정한 오류 코드에 맞추어 기본 오류 페이지를 보여준다.</li>
     * </ul>
     * @see <a href="http://localhost:8080/error/error-404">test url</a>
     */
    @GetMapping("/error-404")
    public void error404(HttpServletResponse response) throws IOException {
        response.sendError(404, "404 오류!");
    }

    /**
     * <h2>response.sendError(HTTP 상태 코드, 오류 메시지)</h2>
     * @see <a href="http://localhost:8080/error/error-500">test url</a>
     */
    @GetMapping("/error-500")
    public void error500(HttpServletResponse response) throws IOException {
        response.sendError(500);
    }

    @GetMapping("/error-400")
    public void error400(HttpServletResponse response) throws IOException {
        response.sendError(400);
    }

}
