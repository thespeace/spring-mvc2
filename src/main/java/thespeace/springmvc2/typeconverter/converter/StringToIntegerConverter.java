package thespeace.springmvc2.typeconverter.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

/**
 * <h1>문자를 숫자로 변환하는 타입 컨버터</h1>
 */
@Slf4j
public class StringToIntegerConverter implements Converter<String, Integer> {


    @Override
    public Integer convert(String source) {
        log.info("convert source={}", source);
        return Integer.valueOf(source);
    }
}
