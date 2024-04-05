package thespeace.springmvc2.typeconverter.converter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import thespeace.springmvc2.typeconverter.converter.IntegerToStringConverter;
import thespeace.springmvc2.typeconverter.converter.IpPortToStringConverter;
import thespeace.springmvc2.typeconverter.converter.StringToIntegerConverter;
import thespeace.springmvc2.typeconverter.converter.StringToIpPortConverter;
import thespeace.springmvc2.typeconverter.type.IpPort;

import static org.assertj.core.api.Assertions.*;

/**
 * <h1>직접 만든 타입 컨버터 테스트</h1>
 * 이렇게 타입 컨버터를 하나하나 직접 사용하면, 개발자가 직접 컨버팅 하는 것과 큰 차이가 없다.<br>
 * 타입 컨버터를 등록하고 관리하면서 편리하게 변환 기능을 제공하는 역할을 하는 무언가가 필요하다.<br>
 * <ul>-스프링은 용도에 따라 다양한 방식의 타입 컨버터를 제공한다.
 *     <li>Converter -> 기본 타입 컨버터</li>
 *     <li>ConverterFactory -> 전체 클래스 계층 구조가 필요할 때</li>
 *     <li>GenericConverter -> 정교한 구현, 대상 필드의 애노테이션 정보 사용 가능</li>
 *     <li>ConditionalGenericConverter -> 특정 조건이 참인 경우에만 실행</li>
 * </ul>
 * @see <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#core-convert">타입 컨버터 공식 문서</a>
 */
public class ConverterTest {

    @Test
    void stringToInteger() {
        StringToIntegerConverter converter = new StringToIntegerConverter();
        Integer result = converter.convert("10");
        assertThat(result).isEqualTo(10);
    }

    @Test
    void IntegerToString() {
        IntegerToStringConverter converter = new IntegerToStringConverter();
        String result = converter.convert(10);
        assertThat(result).isEqualTo("10");
    }

    @Test
    void StringToIpPort() {
        IpPortToStringConverter converter = new IpPortToStringConverter();
        IpPort source = new IpPort("127.0.0.1", 8080);
        String result = converter.convert(source);
        assertThat(result).isEqualTo("127.0.0.1:8080");
    }

    @Test
    void ipPortToString() {
        StringToIpPortConverter converter = new StringToIpPortConverter();
        String source = "127.0.0.1:8080";
        IpPort result = converter.convert(source);
        assertThat(result).isEqualTo(new IpPort("127.0.0.1",8080)); //객체의 @EqualsAndHashCode 덕분에 가능.
    }
}
