package thespeace.springmvc2.exception.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thespeace.springmvc2.exception.exception.UserException;
import thespeace.springmvc2.exception.exhandler.ErrorResult;

/**
 * <h1>@ExceptionHandler</h1>
 * 웹 브라우저에 HTML화면을 제공할 때는 오류가 발생하면 BasicErrorController를 사용하는게 편하다.<br>
 * 반면에 API는 각 시스템 마다 응답의 모양도 다르고, 스펙도 모두 다르다. 한마디로 매우 세밀한 제어가 필요하다.<br>
 * 본 BasicErrorController 를 사용하거나 HandlerExceptionResolver 를 직접 구현하는 방식으로 제어해도
 * API예외를 다루기는 쉽지 않다.<br><br>
 *
 * <h2>API 예외처리의 어려운 점</h2>
 * <ul>
 *     <li>HandlerExceptionResolver 를 떠올려 보면 ModelAndView 를 반환해야 했다.
 *         이것은 API 응답에는 필요하지 않다.</li>
 *     <li>API 응답을 위해서 HttpServletResponse 에 직접 응답 데이터를 넣어주었다. 이것은 매우 불편하다.
 *         스프링 컨트롤러에 비유하면 마치 과거 서블릿을 사용하던 시절로 돌아간 것 같다.</li>
 *     <li>특정 컨트롤러에서만 발생하는 예외를 별도로 처리하기 어렵다. 예를 들어서 회원을 처리하는 컨트롤러에서
 *         발생하는 RuntimeException 예외와 상품을 관리하는 컨트롤러에서 발생하는 동일한 RuntimeException
 *         예외를 서로 다른 방식으로 처리하고 싶다면 어떻게 해야할까?</li>
 * </ul><br>
 *
 * 스프링은 API 예외 처리 문제를 해결하기 위해 `@ExceptionHandler` 라는 애노테이션을 사용하는 매우 편리한 예외
 * 처리 기능을 제공하는데, 이것이 바로 ExceptionHandlerExceptionResolver 이다.<br>
 * 스프링은 ExceptionHandlerExceptionResolver 를 기본으로 제공하고, 기본으로 제공하는 ExceptionResolver중에
 * 우선순위도 가장 높다. 실무에서 API 예외 처리는 대부분 이 기능을 사용한다.
 */
@Slf4j
@RestController
public class ApiExceptionV2Controller {

    /**
     * <h2>@ExceptionHandler 예외 처리 방법</h2>
     * {@code @ExceptionHandler} 애노테이션을 선언하고, 해당 컨트롤러에서 처리하고 싶은 예외를 지정해주면 된다.
     * `해당 컨트롤러`에서 예외가 발생하면 이 메서드가 호출된다. 참고로 지정한 예외 또는 그 예외의 자식 클래스는 모두 잡을 수 있다.
     * {@code @RestController} 애노테이션을 확인 후 @ExceptionHandler에 의해 json형태로 반환.<p>
     *
     * <h2>실행 흐름</h2>
     * <ul>
     *     <li>컨트롤러를 호출한 결과 IllegalArgumentException 예외가 컨트롤러 밖으로 던져진다</li>
     *     <li>예외가 발생했으로 ExceptionResolver 가 작동한다.
     *         가장 우선순위가 높은 ExceptionHandlerExceptionResolver 가 실행된다.</li>
     *     <li>ExceptionHandlerExceptionResolver 는 해당 컨트롤러에 IllegalArgumentException 을 처리
     *         할 수 있는 @ExceptionHandler 가 있는지 확인한다.</li>
     *     <li>illegalExHandle() 를 실행한다. @RestController 이므로 illegalExHandle() 에도
     *         {@code @ResponseBody}가 적용된다. 따라서 HTTP 컨버터가 사용되고, 응답이 다음과 같은 JSON으로 반환된다.</li>
     *     <li>@ResponseStatus(HttpStatus.BAD_REQUEST) 를 지정했으므로 HTTP 상태 코드 400으로 응답한다.</li>
     * </ul>
     *
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST) //HttpStatus를 지정해주지 않으면 정상 처리하여 200반환.
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResult illegalExHandler(IllegalArgumentException e) {
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult("BAD", e.getMessage());
    }

    /**
     * <h2>우선순위</h2>
     * 스프링의 우선순위는 항상 자세한 것이 우선권을 가진다. 예를 들어서 부모, 자식 클래스가 있으면 다음과 같이 예외가 처리된다.
     * <blockquote><pre>
     *     {@code @ExceptionHandler(부모예외.class)}
     *     public String 부모예외처리()(부모예외 e) {}
     *
     *     {@code @ExceptionHandler(자식예외.class)}
     *     public String 자식예외처리()(자식예외 e) {}
     * </pre></blockquote>
     * {@code @ExceptionHandler}에 지정한 부모 클래스는 자식 클래스까지 처리할 수 있다. 따라서 자식예외 가 발생하면 부모
     * 예외처리() , 자식예외처리() 둘다 호출 대상이 된다. 그런데 둘 중 더 자세한 것이 우선권을 가지므로 자식예외처리()
     * 가 호출된다. 물론 부모예외 가 호출되면 부모예외처리() 만 호출 대상이 되므로 부모예외처리() 가 호출된다.<p>
     *
     * <h2>다양한 예외</h2>
     * 다음과 같이 다양한 예외를 한번에 처리할 수 있다.
     * <blockquote><pre>
     *     {@code @ExceptionHandler({AException.class, BException.class})}
     *     public String ex(Exception e) {
     *         log.info("exception e", e);
     *     }
     * </pre></blockquote><p>
     *
     * <h2>예외 생략</h2>
     * {@code @ExceptionHandler}에 예외를 생략할 수 있다. 생략하면 메서드 파라미터의 예외가 지정된다.
     * <blockquote><pre>
     *     {@code @ExceptionHandler}
     *     public ResponseEntity<ErrorResult> userExHandle(UserException e) {}
     * </pre></blockquote><p>
     *
     * <h2>파리미터와 응답</h2>
     * {@code @ExceptionHandler}에는 마치 스프링의 컨트롤러의 파라미터 응답처럼 다양한 파라미터와 응답을 지정할 수 있다.<br>
     * 자세한 파라미터와 응답은 다음 공식 메뉴얼을 참고하자.
     * @see <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-annexceptionhandler-args">공식 메뉴얼</a>
     *
     * <p>
     *
     * <h2>UserException 처리</h2>
     * <ul>
     *     <li>@ExceptionHandler 에 예외를 지정하지 않으면 해당 메서드 파라미터 예외를 사용한다. 여기서는
     *         UserException 을 사용한다</li>
     *     <li>ResponseEntity 를 사용해서 HTTP 메시지 바디에 직접 응답한다. 물론 HTTP 컨버터가 사용된다.
     *         ResponseEntity 를 사용하면 HTTP 응답 코드를 프로그래밍해서 동적으로 변경할 수 있다. 앞서 살펴본
     *         {@code @ResponseStatus}는 애노테이션이므로 HTTP 응답 코드를 동적으로 변경할 수 없다</li>
     * </ul>
     */
    @ExceptionHandler //UserException.class 생략가능
    public ResponseEntity<ErrorResult> userExHandler(UserException e) {
        log.error("[exceptionHandler] ex", e);
        ErrorResult errorResult = new ErrorResult("USER-EX", e.getMessage());
        return new ResponseEntity(errorResult, HttpStatus.BAD_REQUEST);
    }

    /**
     * <h2>Exception 처리</h2>
     * <ul>
     *     <li>throw new RuntimeException("잘못된 사용자") 이 코드가 실행되면서, 컨트롤러 밖으로
     *         RuntimeException 이 던져진다.</li>
     *     <li>RuntimeException 은 Exception 의 자식 클래스이다. 따라서 이 메서드가 호출된다.</li>
     *     <li>@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) 로 HTTP 상태 코드를 500으로 응답한다.</li>
     * </ul>
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exHandler(Exception e){ //지정하지 않은 예외들을 모두 잡아 처리, Exception은 예외 중 최상위 부모.
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult("EX", "내부 오류");
    }

    @GetMapping("/api2/members/{id}")
    public MemberDto getMember(@PathVariable("id") String id) {

        if(id.equals("ex")) {

            throw new RuntimeException("잘못된 사용자");
        }
        if(id.equals("bad")) {
            throw  new IllegalArgumentException("잘못된 입력 값");
        }
        if(id.equals("user-ex")) {
            throw new UserException("사용자 오류");
        }

        return new MemberDto(id, "hello " + id);
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String memberId;
        private String name;
    }

}
