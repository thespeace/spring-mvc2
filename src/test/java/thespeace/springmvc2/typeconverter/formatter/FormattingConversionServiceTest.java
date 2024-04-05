package thespeace.springmvc2.typeconverter.formatter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.format.support.DefaultFormattingConversionService;
import thespeace.springmvc2.typeconverter.converter.IpPortToStringConverter;
import thespeace.springmvc2.typeconverter.converter.StringToIpPortConverter;
import thespeace.springmvc2.typeconverter.type.IpPort;

import static org.assertj.core.api.Assertions.*;

/**
 * <h1>포맷터를 지원하는 컨버전 서비스</h1>
 * 컨버전 서비스에는 컨버터만 등록할 수 있고, 포맷터를 등록할 수는 없다. 그런데 생각해보면 포맷터는 객체 -> 문자,
 * 문자 -> 객체로 변환하는 특별한 컨버터일 뿐이다.<br>
 * 포맷터를 지원하는 컨버전 서비스를 사용하면 컨버전 서비스에 포맷터를 추가할 수 있다. 내부에서 어댑터 패턴을
 * 사용해서 Formatter 가 Converter 처럼 동작하도록 지원한다.<br><br>
 *
 * FormattingConversionService 는 포맷터를 지원하는 컨버전 서비스이다.<br>
 * DefaultFormattingConversionService 는 FormattingConversionService 에 기본적인 통화, 숫자
 * 관련 몇가지 기본 포맷터를 추가해서 제공한다.<p><p>
 *
 *
 * <h2>DefaultFormattingConversionService 상속 관계</h2>
 * FormattingConversionService 는 ConversionService 관련 기능을 상속받기 때문에 결과적으로 컨버터도
 * 포맷터도 모두 등록할 수 있다. 그리고 사용할 때는 ConversionService 가 제공하는 convert 를 사용하면 된다.<br><br>
 *
 * 추가로 스프링 부트는 DefaultFormattingConversionService 를 상속 받은 WebConversionService 를
 * 내부에서 사용한다.
 */
class FormattingConversionServiceTest {

    @Test
    void formattingConversionService() {
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();

        //컨버터 등록
        conversionService.addConverter(new StringToIpPortConverter());
        conversionService.addConverter(new IpPortToStringConverter());
        //포멧터 등록
        conversionService.addFormatter(new MyNumberFormatter());

        //컨버터 사용
        IpPort ipPort = conversionService.convert("127.0.0.1:8080", IpPort.class);
        assertThat(ipPort).isEqualTo(new IpPort("127.0.0.1", 8080));
        //포멧터 사용
        assertThat(conversionService.convert(1000, String.class)).isEqualTo("1,000");
        assertThat(conversionService.convert("1,000", Long.class)).isEqualTo(1000L);

    }
}
