package thespeace.springmvc2.exception.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import thespeace.springmvc2.exception.exception.BadRequestException;
import thespeace.springmvc2.exception.exception.UserException;

@Slf4j
@RestController
public class ApiExceptionController {

    /**
     * @see <a href="http://localhost:8080/api/members/spring">postman test url</a>
     * @testBody { "memberId": "spring", "name": "hello spring" }
     */
    @GetMapping("/api/members/{id}")
    public MemberDto getMember(@PathVariable("id") String id) {

        if(id.equals("ex")) { //http://localhost:8080/api/members/ex 예외 발생 호출시, 오류가 발생하면 우리가 미리 만들어둔 HTML이 반환된다. JSON응답을 할 수 있도록 수정해야한다.

            throw new RuntimeException("잘못된 사용자");
        }
        if(id.equals("bad")) { //http://localhost:8080/api/members/bad 호출시 IllegalArgumentException 발생, 상태코드 500 반환. HandlerExceptionResolver로 동작 방식을 변경해보자.
            throw  new IllegalArgumentException("잘못된 입력 값");
        }
        if(id.equals("user-ex")) { //http://localhost:8080/api/members/user-ex 호출시 UserException 이 발생.
            throw new UserException("사용자 오류");
        }

        return new MemberDto(id, "hello " + id);
    }

    /**
     * <h2>ResponseStatusExceptionResolver(HTTP 응답 코드 변경) - @ResponseStatus 사용</h2>
     * @see <a href="http://localhost:8080/api/response-status-ex1">postman test url</a>
     */
    @GetMapping("/api/response-status-ex1")
    public String responseStatusEx1() {
        throw new BadRequestException();
    }

    /**
     * <h2>ResponseStatusExceptionResolver(HTTP 응답 코드 변경) - ResponseStatusException 사용</h2>
     * {@code @ResponseStatus}는 개발자가 직접 변경할 수 없는 예외에는 적용할 수 없다.
     * (애노테이션을 직접 넣어야 하는데, 내가 코드를 수정할 수 없는 라이브러리의 예외 코드 같은 곳에는 적용할 수 없다.)<br>
     * 추가로 애노테이션을 사용하기 때문에 조건에 따라 동적으로 변경하는 것도 어렵다.<br>
     * 이때는 ResponseStatusException 예외를 사용하면 된다.
     * @see <a href="http://localhost:8080/api/response-status-ex2">postman test url</a>
     */
    @GetMapping("/api/response-status-ex2")
    public String responseStatusEx2() {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "error.bad2", new IllegalArgumentException());
    }

    /**
     * <h2>DefaultHandlerExceptionResolver(스프링 내부 예외 처리)</h2>
     * DefaultHandlerExceptionResolver 는 스프링 내부에서 발생하는 스프링 예외를 해결한다.<p>
     * 대표적으로 파라미터 바인딩 시점에 타입이 맞지 않으면 내부에서 TypeMismatchException 이 발생하는데, 이 경우
     * 예외가 발생했기 때문에 그냥 두면 서블릿 컨테이너까지 오류가 올라가고, 결과적으로 500 오류가 발생한다.<p>
     * 그런데 파라미터 바인딩은 대부분 클라이언트가 HTTP 요청 정보를 잘못 호출해서 발생하는 문제이다. HTTP 에서는
     * 이런 경우 HTTP 상태 코드 400을 사용하도록 되어 있다.<p>
     * DefaultHandlerExceptionResolver 는 이것을 500 오류가 아니라 HTTP 상태 코드 400 오류로 변경한다.<br>
     * @see <a href="http://localhost:8080/api/default-handler-ex?data=string">postman test url</a>
     */
    @GetMapping("/api/default-handler-ex")
    public String defaultException(@RequestParam Integer data) {
        return "ok";
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String memberId;
        private String name;
    }
}
