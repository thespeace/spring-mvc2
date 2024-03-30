package thespeace.springmvc2.exception;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * <h1>서블릿 예외 처리 - 오류 화면 제공</h1>
 * 서블릿 컨테이너가 제공하는 기본 예외 처리 화면은 고객 친화적이지 않다.
 * 서블릿이 제공하는 오류 화면 기능을 사용해보자.<br><br>
 *
 * 서블릿은 Exception (예외)가 발생해서 서블릿 밖으로 전달되거나 또는 response.sendError() 가
 * 호출 되었을 때 각각의 상황에 맞춘 오류 처리 기능을 제공한다.<br>
 * 이 기능을 사용하면 친절한 오류 처리 화면을 준비해서 고객에게 보여줄 수 있다.<br><br>
 * 스프링 부트가 제공하는 기능을 사용해서 서블릿 오류 페이지를 등록하면 된다.
 */
@Component
public class WebServerCustomizer implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    /**
     * <h2>서블릿 오류 페이지 등록</h2>
     * <ul>
     *     <li>response.sendError(404) : errorPage404 호출</li>
     *     <li>response.sendError(500) : errorPage500 호출</li>
     *     <li>RuntimeException 또는 그 자식 타입의 예외: errorPageEx 호출</li>
     * </ul>
     */
    @Override
    public void customize(ConfigurableWebServerFactory factory) {

        ErrorPage errorPage404 = new ErrorPage(HttpStatus.NOT_FOUND, "/error-page/404");
        ErrorPage errorPage500 = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error-page/500");

        //RuntimeException 은 물론이고 RuntimeException 의 자식도 함께 처리한다.
        ErrorPage errorPageEx = new ErrorPage(RuntimeException.class, "/error-page/500"); //해당 오류를 처리할 컨트롤러가 필요.

        factory.addErrorPages(errorPage404, errorPage500, errorPageEx);
    }
}
