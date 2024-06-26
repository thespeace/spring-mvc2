package thespeace.springmvc2.account;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import thespeace.springmvc2.account.web.argumentresolver.LoginMemberArgumentResolver;
import thespeace.springmvc2.account.web.filter.LogFilter;
import thespeace.springmvc2.account.web.filter.LoginCheckFilter;
import thespeace.springmvc2.account.web.interceptor.LogInterceptor;
import thespeace.springmvc2.account.web.interceptor.LoginCheckInterceptor;
import thespeace.springmvc2.exception.filter.LogExFilter;
import thespeace.springmvc2.exception.interceptor.LogExInterceptor;
import thespeace.springmvc2.exception.resolver.MyHandlerExceptionResolver;
import thespeace.springmvc2.exception.resolver.UserHandlerExceptionResolver;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {


    /**
     * <h2>ArgumentResolvers 등록</h2>
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver());
    }

    /**
     * <h2>인터셉터 등록</h2>
     * WebMvcConfigurer 가 제공하는 addInterceptors() 를 사용해서 인터셉터를 등록할 수 있다.
     * <ul>-스프링의 URL 경로
     *     <li>스프링이 제공하는 URL 경로는 서블릿 기술이 제공하는 URL 경로와 완전히 다르다. 더욱 자세하고, 세밀하게 설정할 수 있다.</li>
     * </ul>
     * 순서 주의, 세밀한 설정 가능!
     * @see <a href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/util/pattern/PathPattern.html">PathPattern 공식 문서</a>
     * @reference : 서블릿 필터와 스프링 인터셉터는 웹과 관련된 공통 관심사를 해결하기 위한 기술이다.
     *              서블릿 필터와 비교해서 스프링 인터셉터가 개발자 입장에서 훨씬 편리하다는 것을 코드로 이해했을 것이다.<br>
     *              특별한 문제가 없다면 인터셉터를 사용하는 것이 좋다.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor()) //인터셉터를 등록.
                .order(1) //인터셉터의 호출 순서를 지정한다. 낮을 수록 먼저 호출.
                .addPathPatterns("/account/**","/items/**","/members/**", "login", "/logout") //인터셉터를 적용할 URL 패턴을 지정한다.
                .excludePathPatterns("/css/**", "/*.ico", "/error/**", "/error-page/**"); //인터셉터에서 제외할 패턴을 지정한다.
        //필터와 비교해보면 인터셉터는 addPathPatterns , excludePathPatterns 로 매우 정밀하게 URL 패턴을 지정 할 수 있다.

        registry.addInterceptor(new LoginCheckInterceptor())
                .order(2)
                .addPathPatterns("/items/**") //인터셉터를 적용.
                .excludePathPatterns("/","/account", "/members/add", "/login", "/logout",
                        "/css/**", "/*.ico", "/error/**", "/error-page/**"); //인터셉터를 적용 X.

        /**
         * -서블릿 예외 처리 - 인터셉터 중복 호출 제거
         *  인터셉터는 서블릿이 제공하는 기능이 아니라 스프링이 제공하는 기능이다. 따라서 DispatcherType 과 무관하게 항상 호출된다.
         *  대신에 인터셉터는 다음과 같이 요청 경로에 따라서 추가하거나 제외하기 쉽게 되어 있기 때문에, 이러한 설정을 사용해서
         *  오류 페이지 경로를 excludePathPatterns 를 사용해서 빼주면 된다.
         *
         * -전체 흐름 정리
         *      /error-ex 오류 요청
         *          필터는 DispatchType 으로 중복 호출 제거 ( dispatchType=REQUEST )
         *          인터셉터는 경로 정보로 중복 호출 제거( excludePathPatterns("/error-page/**") )
         *
         *      1. WAS(/error-ex, dispatchType=REQUEST) -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤러
         *      2. WAS(여기까지 전파) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(예외발생)
         *      3. WAS 오류 페이지 확인
         *      4. WAS(/error-page/500, dispatchType=ERROR) -> 필터(x) -> 서블릿 -> 인터셉터(x) -> 컨트롤러(/error-page/500) -> View
         */
        registry.addInterceptor(new LogExInterceptor())
                .order(3)
                .addPathPatterns("/error/*")
                .excludePathPatterns("/css/**", "/*.ico", "/error","/error-page/**"); //오류 페이지 경로
    }

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
     * @reference : 필터에는 스프링 인터셉터는 제공하지 않는, 아주 강력한 기능이 있는데 chain.doFilter(request, response); 를
     *              호출해서 다음 필터 또는 서블릿을 호출할 때 request ,response 를 다른 객체로 바꿀 수 있다. ServletRequest ,
     *              ServletResponse 를 구현한 다른 객체를 만들어서 넘기면 해당 객체가 다음 필터 또는 서블릿에서 사용된다.
     *              잘 사용하는 기능은 아니니 참고만 해두자.
     */
    //@Bean
    public FilterRegistrationBean logFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LogFilter());
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.addUrlPatterns("/*");

        return filterRegistrationBean;
    }

    //@Bean
    public FilterRegistrationBean loginCheckFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LoginCheckFilter()); //로그인 필터를 등록한다.
        filterRegistrationBean.setOrder(2); //순서를 2번으로 잡았다. 로그 필터 다음에 로그인 필터가 적용된다.
        filterRegistrationBean.addUrlPatterns("/*"); //모든 요청에 로그인 필터를 적용한다.

        return filterRegistrationBean;
    }

    /**
     * <h2>필터와 DispatcherType</h2>
     * <ul>{@code filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR);}
     *     <li>이렇게 두 가지를 모두 넣으면 클라이언트 요청은 물론이고, 오류 페이지 요청에서도 필터가 호출된다.</li>
     *     <li>아무것도 넣지 않으면 기본 값이 DispatcherType.REQUEST 이다. 즉 클라이언트의 요청이 있는 경우에만 필터가 적용된다.
     *         특별히 오류 페이지 경로도 필터를 적용할 것이 아니면, 기본 값을 그대로 사용하면 된다. </li>
     *     <li>물론 오류 페이지 요청 전용 필터를 적용하고 싶으면 DispatcherType.ERROR 만 지정하면 된다.</li>
     * </ul>
     * @see <a href="http://localhost:8080/error/error-ex">test url</a>
     */
    //@Bean
    public FilterRegistrationBean logExFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LogExFilter());
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.addUrlPatterns("/error/*","/error-page/*");
        filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR);

        return filterRegistrationBean;
    }

    /**
     * <h2>WebMvcConfigurer 를 통해 HandlerExceptionResolver 등록</h2>
     * 기본 설정을 유지하면서 추가<br>
     * {@code configureHandlerExceptionResolvers(..)}를 사용하면 스프링이 기본으로 등록하는
     * {@code ExceptionResolver}가 제거되므로 주의, {@code extendHandlerExceptionResolvers}를 사용하자.
     */
    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(new MyHandlerExceptionResolver());
        resolvers.add(new UserHandlerExceptionResolver());
    }
}
