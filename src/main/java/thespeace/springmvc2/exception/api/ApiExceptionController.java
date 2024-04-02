package thespeace.springmvc2.exception.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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

        return new MemberDto(id, "hello " + id);
    }


    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String memberId;
        private String name;
    }
}
