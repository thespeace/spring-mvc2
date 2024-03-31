package thespeace.springmvc2.exception.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <h1>서블릿 예외 처리 - 오류 페이지 작동 원리</h1>
 * 서블릿은 Exception (예외)가 발생해서 서블릿 밖으로 전달되거나 또는 response.sendError() 가<br>
 * 호출 되었을 때 설정된 오류 페이지를 찾는다.<br>
 *
 * <ul>-예외 발생 흐름
 *     <li>WAS(여기까지 전파) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(예외발생)</li>
 * </ul>
 * <ul>-sendError 흐름
 *     <li>WAS(sendError 호출 기록 확인) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(response.sendError())</li>
 * </ul>
 * WAS는 해당 예외를 처리하는 오류 페이지 정보를 확인한다.<br>
 * {@code new ErrorPage(RuntimeException.class, "/error-page/500")}<br><br>
 *
 * 예를 들어서 RuntimeException 예외가 WAS까지 전달되면, WAS는 오류 페이지 정보를 확인한다. 확인해보니
 * RuntimeException 의 오류 페이지로 /error-page/500 이 지정되어 있다. WAS는 오류 페이지를 출력하기 위
 * 해 /error-page/500 를 다시 요청한다.<br>
 *
 * <ul>-오류 페이지 요청 흐름
 *     <li>WAS `/error-page/500` 다시 요청 -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤러(/error-page/500) -> View</li>
 * </ul>
 * <ol>-예외 발생과 오류 페이지 요청 흐름
 *     <li>WAS(여기까지 전파) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(예외발생)</li>
 *     <li>WAS `/error-page/500` 다시 요청 -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤러(/error-page/500) -> View</li>
 * </ol>
 *
 * <h3>중요한 점은 웹 브라우저(클라이언트)는 서버 내부에서 이런 일이 일어나는지 전혀 모른다는 점이다.<br>
 *     오직 서버 내부에서 오류 페이지를 찾기 위해 추가적인 호출을 한다.</h3><br>
 *
 * <ol>-정리하면 다음과 같다.
 *     <li>예외가 발생해서 WAS까지 전파된다.</li>
 *     <li>WAS는 오류 페이지 경로를 찾아서 내부에서 오류 페이지를 호출한다.<br>
 *         이때 오류 페이지 경로로 필터, 서블릿, 인터셉터, 컨트롤러가 모두 다시 호출된다./li>
 * </ol>
 */
@Slf4j
@Controller
public class ErrorPageController { //오류가 발생했을 때 처리할 수 있는 컨트롤러

    //RequestDispatcher 상수로 정의되어 있음
    public static final String ERROR_EXCEPTION = "jakarta.servlet.error.exception";
    public static final String ERROR_EXCEPTION_TYPE = "jakarta.servlet.error.exception_type";
    public static final String ERROR_MESSAGE = "jakarta.servlet.error.message";
    public static final String ERROR_REQUEST_URI = "jakarta.servlet.error.request_uri";
    public static final String ERROR_SERVLET_NAME = "jakarta.servlet.error.servlet_name";
    public static final String ERROR_STATUS_CODE = "jakarta.servlet.error.status_code";

    /**
     * @see <a href="http://localhost:8080/error/error-404">test url</a>
     */
    @RequestMapping("/error-page/404")
    public String errorPage404(HttpServletRequest request, HttpServletResponse response) {
        log.info("errorPage 404");
        printErrorInfo(request);
        return "error-page/404";
    }

    /**
     * @see <a href="http://localhost:8080/error/error-500">test url</a>
     */
    @RequestMapping("/error-page/500")
    public String errorPage500(HttpServletRequest request, HttpServletResponse response) {
        log.info("errorPage 500");
        printErrorInfo(request);
        return "error-page/500";
    }

    /**
     * <h2>오류 정보 추가</h2>
     * WAS는 오류 페이지를 단순히 다시 요청만 하는 것이 아니라, 오류 정보를 request 의 attribute 에 추가해서 넘겨준다.<br>
     * 필요하면 오류 페이지에서 이렇게 전달된 오류 정보를 사용할 수 있다.<br>
     * <ul>-request.attribute에 서버가 담아준 정보
     *     <li>javax.servlet.error.exception : 예외</li>
     *     <li>javax.servlet.error.exception_type : 예외 타입</li>
     *     <li>javax.servlet.error.message : 오류 메시지</li>
     *     <li>javax.servlet.error.request_uri : 클라이언트 요청 URI</li>
     *     <li>javax.servlet.error.servlet_name : 오류가 발생한 서블릿 이름</li>
     *     <li>javax.servlet.error.status_code : HTTP 상태 코드</li>
     * </ul>
     */
    private void printErrorInfo(HttpServletRequest request) {
        log.info("ERROR_EXCEPTION: {}", request.getAttribute(ERROR_EXCEPTION));
        log.info("ERROR_EXCEPTION_TYPE: {}",request.getAttribute(ERROR_EXCEPTION_TYPE));
        log.info("ERROR_MESSAGE: {}", request.getAttribute(ERROR_MESSAGE)); //ex의 경우 NestedServletException 스프링이 한번 감싸서 반환
        log.info("ERROR_REQUEST_URI: {}", request.getAttribute(ERROR_REQUEST_URI));
        log.info("ERROR_SERVLET_NAME: {}", request.getAttribute(ERROR_SERVLET_NAME));
        log.info("ERROR_STATUS_CODE: {}", request.getAttribute(ERROR_STATUS_CODE));
        log.info("dispatchType={}", request.getDispatcherType());
    }

}
