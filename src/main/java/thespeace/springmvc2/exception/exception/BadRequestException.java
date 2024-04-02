package thespeace.springmvc2.exception.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * <h1>스프링이 제공하는 ExceptionResolver 1</h1>
 * 스프링 부트가 기본으로 제공하는 ExceptionResolver 는 다음과 같다.<br>
 * {@code HandlerExceptionResolverComposite}에 다음 순서로 등록
 * <ol>
 *     <li>ExceptionHandlerExceptionResolver</li>
 *     <li>ResponseStatusExceptionResolver</li>
 *     <li>DefaultHandlerExceptionResolver -> 우선 순위가 가장 낮다</li>
 * </ol><br>
 * <h2>ExceptionHandlerExceptionResolver</h2>
 * {@code @ExceptionHandler}을 처리한다. API 예외 처리는 대부분 이 기능으로 해결한다.
 * <p>
 *
 * <h2>ResponseStatusExceptionResolver</h2>
 * HTTP 상태 코드를 지정해준다.<br>
 * 예) @ResponseStatus(value = HttpStatus.NOT_FOUND)
 * <p>
 *
 * <h2>DefaultHandlerExceptionResolver</h2>
 * 스프링 내부 기본 예외를 처리한다.<p>
 *
 * 해당 클래스는 ResponseStatusExceptionResolver를 구현한 클래스이다.
 * <p><p>
 *
 * <h1>ResponseStatusExceptionResolver</h1>
 * ResponseStatusExceptionResolver 는 예외에 따라서 HTTP 상태 코드를 지정해주는 역할을 한다.
 * <ul>다음 두 가지 경우를 처리한다.
 *     <li>@ResponseStatus 가 달려있는 예외</li>
 *     <li>ResponseStatusException 예외</li>
 * </ul>
 */
//@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "잘못된 요청 오류") //@ResponseStatus 애노테이션을 적용하면 HTTP 상태 코드를 변경해준다.
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "error.bad1") //reason을 MessageSource에서 찾는 기능도 제공한다.
public class BadRequestException extends RuntimeException{
    /**
     * `BadRequestException` 예외가 컨트롤러 밖으로 넘어가면 `ResponseStatusExceptionResolver` 예외가
     * 해당 애노테이션을 확인해서 오류 코드를 `HttpStatus.BAD_REQUEST`(400)으로 변경하고, 메시지도 담는다.
     *
     * ResponseStatusExceptionResolver 코드를 확인해보면 결국 response.sendError(statusCode, resolvedReason)를
     * 호출하는 것을 확인할 수 있다. sendError(400) 를 호출했기 때문에 WAS에서 다시 오류 페이지( /error )를 내부 요청한다.
     */
}
