package thespeace.springmvc2.typeconverter;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import thespeace.springmvc2.typeconverter.converter.IntegerToStringConverter;
import thespeace.springmvc2.typeconverter.converter.IpPortToStringConverter;
import thespeace.springmvc2.typeconverter.converter.StringToIntegerConverter;
import thespeace.springmvc2.typeconverter.converter.StringToIpPortConverter;

/**
 * <h1>스프링에 Converter 적용</h1>
 * 스프링은 내부에서 ConversionService 를 제공한다. 우리는 WebMvcConfigurer 가 제공하는
 * addFormatters() 를 사용해서 추가하고 싶은 컨버터를 등록하면 된다.
 * 이렇게 하면 스프링은 내부에서 사용하는 ConversionService 에 컨버터를 추가해준다.
 */
@Configuration
public class WebConverterConfig implements WebMvcConfigurer {

    /**
     * <h2>컨버터 등록</h2>
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToIntegerConverter());
        registry.addConverter(new IntegerToStringConverter());
        registry.addConverter(new StringToIpPortConverter());
        registry.addConverter(new IpPortToStringConverter());
    }
}
