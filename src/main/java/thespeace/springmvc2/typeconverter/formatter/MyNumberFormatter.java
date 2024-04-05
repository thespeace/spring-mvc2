package thespeace.springmvc2.typeconverter.formatter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.Formatter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * <h1>포맷터 - Formatter</h1>
 * Converter 는 입력과 출력 타입에 제한이 없는, 범용 타입 변환 기능을 제공한다.<br>
 * 반면에 Formatter는 객체를 특정한 포멧에 맞추어 문자로 출력하거나 또는 그 반대의
 * 역할을 하는 것에 특화된 기능이 바로 포맷터( Formatter )이다.
 * 포맷터는 컨버터의 특별한 버전으로 이해하면 된다.
 *
 * <ul>-Converter vs Formatter
 *     <li>Converter 는 범용(객체 -> 객체)</li>
 *     <li>Formatter 는 문자에 특화(객체 -> 문자, 문자 -> 객체) + 현지화(Locale)</li>
 * </ul>
 * <ul>-Locale?
 *     <li>날짜, 숫자의 표현 방법으로 Locale 현지화 정보를 사용할 수 있다.</li>
 * </ul>
 * <ul>-웹 애플리케이션에서 객체를 문자로, 문자를 객체로 변환하는 예
 *     <li>화면에 숫자를 출력해야 하는데, Integer -> String 출력 시점에 숫자 1000 -> 문자 "1,000" 이렇게
 *         1000 단위에 쉼표를 넣어서 출력하거나, 또는 "1,000" 라는 문자를 1000 이라는 숫자로 변경해야 한다.</li>
 *     <li>날짜 객체를 문자인 "2021-01-01 10:50:11" 와 같이 출력하거나 또는 그 반대의 상황</li>
 * </ul>
 *
 * @see <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#format">formatter 공식 문서</a>
 * @reference : 스프링은 용도에 따라 다양한 방식의 포맷터를 제공한다.<br>
 *              Formatter 포맷터<br>
 *              AnnotationFormatterFactory 필드의 타입이나 애노테이션 정보를 활용할 수 있는 포맷터
 */
@Slf4j
public class MyNumberFormatter implements Formatter<Number> { //포맷터( Formatter )는 객체를 문자로 변경하고, 문자를 객체로 변경하는 두 가지 기능을 모두 수행한다.

    /**
     * <h2>문자를 객체로 변경</h2>
     */
    @Override
    public Number parse(String text, Locale locale) throws ParseException {
        log.info("text={}, locale={}", text, locale);

        //"1,000" -> 1000
        //NumberFormat 객체를 사용하면 Locale 정보를 활용해서 나라별로 다른 숫자 포맷을 만들어준다.
        NumberFormat format = NumberFormat.getInstance(locale); //
        return format.parse(text);
    }

    /**
     * <h2>객체를 문자로 변경</h2>
     */
    @Override
    public String print(Number object, Locale locale) {
        log.info("object={}, locale={}", object, locale);
        return NumberFormat.getInstance(locale).format(object);
    }
}
