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
