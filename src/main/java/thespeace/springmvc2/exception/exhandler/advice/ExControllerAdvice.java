package thespeace.springmvc2.exception.exhandler.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import thespeace.springmvc2.exception.exception.UserException;
import thespeace.springmvc2.exception.exhandler.ErrorResult;

/**
 * <h1>@ControllerAdvice</h1>
 * {@code @ExceptionHandler}를 사용해서 예외를 깔끔하게 처리할 수 있게 되었지만, 정상 코드와 예외 처리 코드가 하나의
 * 컨트롤러에 섞여 있다. @ControllerAdvice 또는 @RestControllerAdvice 를 사용하면 둘을 분리할 수 있다.
 * <ul>
 *     <li>@ControllerAdvice 는 대상으로 지정한 여러 컨트롤러에 @ExceptionHandler , @InitBinder 기능을
 *         부여해주는 역할을 한다.</li>
 *     <li>@ControllerAdvice 에 대상을 지정하지 않으면 모든 컨트롤러에 적용된다. (글로벌 적용)</li>
 *     <li>@RestControllerAdvice 는 @ControllerAdvice 와 같고, @ResponseBody 가 추가되어 있다.
 *         {@code @Controller}, @RestController 의 차이와 같다.</li>
 * </ul><p>
 *
 * <h2>대상 컨트롤러 지정 방법</h2>
 * 특정 애노테이션이 있는 컨트롤러를 지정할 수 있고, 특정 패키지를 직접 지정할 수도 있다.<br>
 * 패키지 지정의 경우 해당 패키지와 그 하위에 있는 컨트롤러가 대상이 된다. 그리고 특정 클래스를 지정할 수도 있다.<br>
 * 대상 컨트롤러 지정을 생략하면 모든 컨트롤러에 적용된다.
 * <blockquote><pre>
 * // Target all Controllers annotated with @RestController
 * {@code @ControllerAdvice(annotations = RestController.class)}
 * public class ExampleAdvice1 {}
 *
 * // Target all Controllers within specific packages
 * {@code @ControllerAdvice("org.example.controllers")}
 * public class ExampleAdvice2 {}
 *
 * // Target all Controllers assignable to specific classes
 * {@code @ControllerAdvice(assignableTypes = {ControllerInterface.class, AbstractController.class})}
 * public class ExampleAdvice3 {}
 * </pre></blockquote>
 *
 * 정리하자면, @ExceptionHandler 와 @ControllerAdvice 를 조합하면 예외를 깔끔하게 해결할 수 있다!
 *
 * @see <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-anncontroller-advice">스프링 공식 문서(대상 컨트롤러 지정 방법)</a>
 */
@Slf4j
//@RestControllerAdvice()
public class ExControllerAdvice {


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
     * @see <a href="http://localhost:8080/api2/members/bad">Postman test url</a>
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
     *
     * @see <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-annexceptionhandler-args">공식 메뉴얼</a>
     * @see <a href="http://localhost:8080/api2/members/user-ex">Postman test url</a>
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
     * @see <a href="http://localhost:8080/api2/members/ex">Postman test url</a>
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exHandler(Exception e){ //지정하지 않은 예외들을 모두 잡아 처리, Exception은 예외 중 최상위 부모.
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult("EX", "내부 오류");
    }

}
