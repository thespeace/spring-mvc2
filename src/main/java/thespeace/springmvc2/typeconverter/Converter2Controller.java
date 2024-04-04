package thespeace.springmvc2.typeconverter;

import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import thespeace.springmvc2.typeconverter.type.IpPort;

/**
 * <h1>뷰 템플릿에 컨버터 적용하기</h1>
 * 타임리프는 렌더링 시에 컨버터를 적용해서 렌더링 하는 방법을 편리하게 지원한다.<br>
 *
 */
@Controller
@RequestMapping("/converter")
public class Converter2Controller {

    /**
     * <h2>객체를 문자로 변환</h2>
     * Model 에 숫자 10000 와 ipPort 객체를 담아서 뷰 템플릿에 전달.
     * @see <a href="http://localhost:8080/converter-view">test url</a>
     */
    @GetMapping("/converter-view")
    public String converterView(Model model) {
        model.addAttribute("number",10000);
        model.addAttribute("ipPort", new IpPort("127.0.0.1", 8080));
        return "converter/converter-view";
    }

    /**
     * <h2>폼에 적용하기</h2>
     * IpPort 를 뷰 템플릿 폼에 출력<br>
     * th:field 가 자동으로 컨버전 서비스를 적용해주어서 ${{ipPort}} 처럼 적용이 되었다.<br>
     * 따라서 IpPort -> String 으로 변환된다
     */
    @GetMapping("/converter-edit")
    public String converterForm(Model model) {
        IpPort ipPort = new IpPort("127.0.0.1", 8080);
        Form form = new Form(ipPort);
        model.addAttribute("form", form);
        return "converter/converter-form";
    }

    /**
     * <h2>폼에 적용하기</h2>
     * 뷰 템플릿 폼의 IpPort 정보를 받아서 출력<br>
     * {@code @ModelAttribute} 를 사용해서 String IpPort 로 변환된다.
     */
    @PostMapping("/converter-edit")
    public String converterEdit(@ModelAttribute Form form, Model model) {
        IpPort ipPort = form.getIpPort();
        model.addAttribute("ipPort", ipPort);
        return "converter/converter-view";
    }

    @Data
    static class Form {
        private IpPort ipPort;

        public Form(IpPort ipPort) {
            this.ipPort = ipPort;
        }
    }
}
