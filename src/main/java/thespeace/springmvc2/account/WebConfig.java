package thespeace.springmvc2.account;

import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import thespeace.springmvc2.account.web.filter.LogFilter;

/**
 * <h2>필터 설정</h2>
 * 필터를 등록하는 방법은 여러가지가 있지만, 스프링 부트를 사용한다면 FilterRegistrationBean 을 사용해서
 * 등록하면 된다.
 * <ul>
 *     <li>setFilter(new LogFilter()) : 등록할 필터를 지정한다.</li>
 *     <li>setOrder(1) : 필터는 체인으로 동작한다. 따라서 순서가 필요하다. 낮을 수록 먼저 동작한다.</li>
 *     <li>addUrlPatterns("/*") : 필터를 적용할 URL 패턴을 지정한다. 한번에 여러 패턴을 지정할 수 있다.</li>
 * </ul>
 *
 * @reference : URL 패턴에 대한 룰은 필터도 서블릿과 동일하다. 자세한 내용은 서블릿 URL 패턴으로 검색해보자. <br>
 *              {@code @ServletComponentScan} @WebFilter(filterName = "logFilter", urlPatterns = "/*")로
 *              필터 등록이 가능하지만 필터 순서 조절이 안된다. 따라서 FilterRegistrationBean 을 사용하자.
 * @reference : 실무에서 HTTP 요청시 같은 요청의 로그에 모두 같은 식별자를 자동으로 남기는 방법은 logback mdc로 검색해보자.
 */
@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean logFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LogFilter());
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.addUrlPatterns("/*");

        return filterRegistrationBean;
    }
}
