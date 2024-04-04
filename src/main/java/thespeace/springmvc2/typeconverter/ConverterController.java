package thespeace.springmvc2.typeconverter;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h1>스프링 타입 컨버터 소개</h1>
 * 문자를 숫자로 변환하거나, 반대로 숫자를 문자로 변환해야 하는 것 처럼 애플리케이션을 개발하다 보면 타입을
 * 변환해야 하는 경우가 상당히 많다.<p><p>
 *
 * <h2>스프링의 타입 변환 적용 예</h2>
 * <ul>
 *     <li>스프링 MVC 요청 파라미터<ul><li>
 *         {@code @RequestParam , @ModelAttribute , @PathVariable}
 *     </li></ul></li>
 *     <li>@Value 등으로 YML 정보 읽기</li>
 *     <li>XML에 넣은 스프링 빈 정보를 변환</li>
 *     <li>뷰를 렌더링 할 때</li>
 * </ul><p>
 *
 * <h2>스프링과 타입 변환</h2>
 * 이렇게 타입을 변환해야 하는 경우는 상당히 많다. 개발자가 직접 하나하나 타입 변환을 해야 한다면, 생각만 해도
 * 괴로울 것이다.<br>
 * 스프링이 중간에 타입 변환기를 사용해서 타입을 String -> Integer 로 변환해주었기 때문에 개발자는 편리하게
 * 해당 타입을 바로 받을 수 있다. 앞에서는 문자를 숫자로 변경하는 예시를 들었지만, 반대로 숫자를 문자로 변경하는 것도
 * 가능하고, Boolean 타입을 숫자로 변경하는 것도 가능하다. 만약 개발자가 새로운 타입을 만들어서 변환하고 싶으면
 * 어떻게 하면 될까?
 * <blockquote><pre>
 *     //컨버터 인터페이스
 *     package org.springframework.core.convert.converter;
 *
 *     {@code public interface Converter<S, T> (
 *         T convert(S source);
 *     )}
 * </pre></blockquote>
 * 스프링은 확장 가능한 컨버터 인터페이스를 제공한다.<br>
 * 개발자는 스프링에 추가적인 타입 변환이 필요하면 이 컨버터 인터페이스를 구현해서 등록하면 된다.<br>
 * 이 컨버터 인터페이스는 모든 타입에 적용할 수 있다. 필요하면 X -> Y 타입으로 변환하는 컨버터 인터페이스를 만들고,
 * 또 Y -> X 타입으로 변환하는 컨버터 인터페이스를 만들어서 등록하면 된다.<br>
 * 예를 들어서 문자로 "true" 가 오면 Boolean 타입으로 받고 싶으면 String -> Boolean 타입으로 변환되도록
 * 컨버터 인터페이스를 만들어서 등록하고, 반대로 적용하고 싶으면 Boolean -> String 타입으로 변환되도록
 * 컨버터를 추가로 만들어서 등록하면 된다.
 *
 * @reference : 과거에는 PropertyEditor 라는 것으로 타입을 변환했다. PropertyEditor 는 동시성 문제가 있어서
 *              타입을 변환할 때 마다 객체를 계속 생성해야 하는 단점이 있다. 지금은 Converter 의 등장으로 해당 문제들이
 *              해결되었고, 기능 확장이 필요하면 Converter 를 사용하면 된다.
 */
@RestController
@RequestMapping("/converter/intro")
public class ConverterController {

    /**
     * <h2>HTTP 쿼리 스트링(타입 변경)</h2>
     * @see <a href="http://localhost:8080/converter/intro/v1?data=10">test url</a>
     */
    @GetMapping("/v1")
    public String v1(HttpServletRequest request) {
        String data = request.getParameter("data"); //문자 타입 조회
        Integer intValue = Integer.valueOf(data); //숫자 타입으로 변경
        System.out.println("intValue = " + intValue);
        return "ok";
    }

    /**
     * <h2>@RequestParam 사용(타입 변경)</h2>
     * 스프링이 제공하는 @RequestParam 을 사용하면 이 문자 10을 Integer 타입의 숫자 10으로 편리하게 받을 수 있다.<br>
     * `이것은 스프링이 중간에서 타입을 변환해주었기 때문이다.`<p><p>
     * 이러한 예는 {@code @ModelAttribute}, {@code @PathVariable}에서도 확인할 수 있다.
     *
     * <blockquote><pre>
     *     //@ModelAttribute 타입 변환 예시
     *     {@code @ModelAttribute UserData data}
     *
     *     class UserData {
     *         Integer data;
     *     }
     *     //@RequestParam 와 같이, 문자 data=10 을 숫자 10으로 받을 수 있다
     *
     *
     *     //@PathVariable 타입 변환 예시
     *     /users/{userId}
     *     {@code @PathVariable("userId") Integer data}
     *     //URL 경로는 문자다. /users/10 여기서 10도 숫자 10이 아니라 그냥 문자 "10"이다.
     *     //data를 Integer 타입으로 받을 수 있는 것도 스프링이 타입 변환을 해주기 때문이다.
     * </pre></blockquote>
     *
     * @see <a href="http://localhost:8080/converter/intro/v2?data=10">test url</a>
     */
    @GetMapping("/v2")
    public String v2(@RequestParam Integer data) {
        System.out.println("data = " + data);
        return "ok";
    }


}
