package thespeace.springmvc2.typeconverter.formatter;

import lombok.Data;
import org.springframework.format.Formatter;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.datetime.standard.Jsr310DateTimeFormatAnnotationFormatterFactory;
import org.springframework.format.number.NumberFormatAnnotationFormatterFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;

/**
 * <h1>스프링이 제공하는 기본 포맷터</h1>
 * 스프링은 자바에서 기본으로 제공하는 타입들에 대해 수 많은 포맷터를 기본으로 제공한다.<br>
 * IDE에서 {@link Formatter} 인터페이스의 구현 클래스를 찾아보면 수 많은 날짜나 시간 관련 포맷터가 제공되는 것을
 * 확인 할 수 있다.<br>
 * 그런데 포맷터는 기본 형식이 지정되어 있기 때문에, 객체의 각 필드마다 다른 형식으로 포맷을 지정하기는 어렵다.<p><p>
 *
 * 스프링은 이런 문제를 해결하기 위해 애노테이션 기반으로 원하는 형식을 지정해서 사용할 수 있는 매우 유용한 포맷터
 * 두 가지를 기본으로 제공한다.
 * <ul>
 *     <li>@NumberFormat : 숫자 관련 형식 지정 포맷터 사용, {@link NumberFormatAnnotationFormatterFactory}</li>
 *     <li>@DateTimeFormat : 날짜 관련 형식 지정 포맷터 사용, {@link Jsr310DateTimeFormatAnnotationFormatterFactory}</li>
 * </ul>
 * @see <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#formatCustomFormatAnnotations">@NumberFormat , @DateTimeFormat의 공식 사용 문서</a>
 */
@Controller
public class FormatterController {

    @GetMapping("/formatter/edit")
    public String formatterForm(Model model) {
        Form form = new Form();
        form.setNumber(10000);
        form.setLocalDateTime(LocalDateTime.now());
        model.addAttribute("form", form);
        return "formatter/formatter-form";
    }

    @PostMapping("/formatter/edit")
    public String formatterForm(@ModelAttribute Form form) {
        return "formatter/formatter-view";
    }

    @Data
    static class Form {

        @NumberFormat(pattern = "###,###")
        private Integer number;

        @NumberFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime localDateTime;
    }
}
