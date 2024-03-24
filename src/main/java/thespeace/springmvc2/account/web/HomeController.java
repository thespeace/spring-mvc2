package thespeace.springmvc2.account.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/account")
public class HomeController {

    @GetMapping
    public String home() {
        return "/account/home";
    }
}
