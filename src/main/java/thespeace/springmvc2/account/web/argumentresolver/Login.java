package thespeace.springmvc2.account.web.argumentresolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <h2>@Login 애노테이션 생성</h2>
 * <ul>
 *     <li>@Target(ElementType.PARAMETER) : 파라미터에만 사용</li>
 *     <li>@Retention(RetentionPolicy.RUNTIME) : 리플렉션 등을 활용할 수 있도록 런타임까지
 *         애노테이션 정보가 남아있음</li>
 * </ul>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Login {
}
