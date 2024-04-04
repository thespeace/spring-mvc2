package thespeace.springmvc2.converter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.support.DefaultConversionService;
import thespeace.springmvc2.typeconverter.converter.IntegerToStringConverter;
import thespeace.springmvc2.typeconverter.converter.IpPortToStringConverter;
import thespeace.springmvc2.typeconverter.converter.StringToIntegerConverter;
import thespeace.springmvc2.typeconverter.converter.StringToIpPortConverter;
import thespeace.springmvc2.typeconverter.type.IpPort;

import static org.assertj.core.api.Assertions.*;

/**
 * <h1>컨버전 서비스 - ConversionService</h1>
 * 이렇게 타입 컨버터를 하나하나 직접 찾아서 타입 변환에 사용하는 것은 매우 불편하다.<br>
 * 그래서 스프링은 개별 컨버터를 모아두고 그것들을 묶어서 편리하게 사용할 수 있는 기능을 제공하는데,
 * 이것이 바로 컨버전 서비스( ConversionService )이다.
 *
 * <blockquote><pre>{@code
 *     //ConversionService 인터페이스
 *     package org.springframework.core.convert;
 *
 *     import org.springframework.lang.Nullable;
 *
 *     public interface ConversionService {
 *         boolean canConvert(@Nullable Class<?> sourceType, Class<?> targetType);
 *         boolean canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType);
 *         <T> T convert(@Nullable Object source, Class<T> targetType);
 *         Object convert(@Nullable Object source, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType);
 *     }}
 * </pre></blockquote>
 * 컨버전 서비스 인터페이스는 단순히 컨버팅이 가능한지 확인하는 기능과, 컨버팅 기능을 제공한다.
 */
public class ConversionServiceTest {

    /**
     * <h2>컨버전 서비스 테스트</h2>
     * DefaultConversionService 는 ConversionService 인터페이스를 구현했는데, 추가로 컨버터를 등록하는
     * 기능도 제공한다.
     *
     * <ul>-등록과 사용 분리
     *     <li>컨버터를 등록할 때는 StringToIntegerConverter 같은 타입 컨버터를 명확하게 알아야 한다. 반면에 컨버터를
     *         사용하는 입장에서는 타입 컨버터를 전혀 몰라도 된다. 타입 컨버터들은 모두 컨버전 서비스 내부에 숨어서 제공된다.
     *         따라서 타입을 변환을 원하는 사용자는 컨버전 서비스 인터페이스에만 의존하면 된다. 물론 컨버전 서비스를 등록하는
     *         부분과 사용하는 부분을 분리하고 의존관계 주입을 사용해야 한다.</li>
     * </ul>
     * <ul>-인터페이스 분리 원칙 - ISP(Interface Segregation Principle) : 클라이언트가 자신이 이용하지 않는 메서드에 의존하지 않아야 한다.<br>
     *      DefaultConversionService 는 다음 두 인터페이스를 구현했다.
     *     <li>ConversionService : 컨버터 사용에 초점</li>
     *     <li>ConverterRegistry : 컨버터 등록에 초점</li>
     * </ul>
     * 이렇게 인터페이스를 분리하면 컨버터를 사용하는 클라이언트와 컨버터를 등록하고 관리하는 클라이언트의 관심사를
     * 명확하게 분리할 수 있다. 특히 컨버터를 사용하는 클라이언트는 ConversionService 만 의존하면 되므로, 컨버터를
     * 어떻게 등록하고 관리하는지는 전혀 몰라도 된다. 결과적으로 컨버터를 사용하는 클라이언트는 꼭 필요한 메서드만
     * 알게된다. 이렇게 인터페이스를 분리하는 것을 ISP 라 한다
     * @see <a href="https://ko.wikipedia.org/wiki/%EC%9D%B8%ED%84%B0%ED%8E%98%EC%9D%B4%EC%8A%A4_%EB%B6%84%EB%A6%AC_%EC%9B%90%EC%B9%99">ISP 참고 url</a>
     */
    @Test
    void conversionService() {
        //컨버전 서비스 등록
        DefaultConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(new StringToIntegerConverter());
        conversionService.addConverter(new IntegerToStringConverter());
        conversionService.addConverter(new StringToIpPortConverter());
        conversionService.addConverter(new IpPortToStringConverter());

        //컨버전 서비스 사용
        assertThat(conversionService.convert("10", Integer.class)).isEqualTo(10);
        assertThat(conversionService.convert(10, String.class)).isEqualTo("10");

        IpPort result = conversionService.convert("127.0.0.1:8080", IpPort.class);
        assertThat(result).isEqualTo(new IpPort("127.0.0.1", 8080));

        String ipPortString = conversionService.convert(new IpPort("127.0.0.1", 8080), String.class);
        assertThat(ipPortString).isEqualTo("127.0.0.1:8080");

    }
}
