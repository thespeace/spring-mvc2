package thespeace.springmvc2.itemservice.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import thespeace.springmvc2.itemservice.domain.item.Item;

import java.util.Set;

/**
 * <h1>Bean Validation</h1>
 * Bean Validation은 특정한 구현체가 아니라 Bean Validation 2.0(JSR-380)이라는 기술 표준이다.<br>
 * 쉽게 이야기해서 검증 애노테이션과 여러 인터페이스의 모음이다. 마치 JPA가 표준 기술이고 그 구현체로 하이버네이트가 있는 것과 같다.<br><br>
 * Bean Validation을 구현한 기술중에 일반적으로 사용하는 구현체는 하이버네이트 Validator이다. 이름이 하이버네이트가 붙어서 그렇지 ORM과는 관련이 없다.<br><br>
 * 검증 기능을 매번 코드로 작성하는 것은 상당히 번거롭다.<br>
 * 특히 특정 필드에 대한 검증 로직은 대부분 빈 값인지 아닌지, 특정 크기를 넘는지 아닌지와 같이 매우 일반적인 로직이다.<br>
 * <blockquote><pre>
 *     //검증 애노테이션
 *     {@code @NotBlank}
 *     {@code @NotNull}
 *     {@code @Range(min = 1000, max = 1000000)}
 *     {@code @Max(9999)}
 * </pre></blockquote>
 * 이런 검증 로직을 모든 프로젝트에 적용할 수 있게 공통화하고, 표준화 한 것이 바로 Bean Validation 이다.<br>
 * Bean Validation을 잘 활용하면, 애노테이션 하나로 검증 로직을 매우 편리하게 적용할 수 있다.<br>
 *
 * @see <a href="http://hibernate.org/validator/">공식 사이트</a>,
 * <a href="https://docs.jboss.org/hibernate/validator/6.2/reference/en-US/html_single/">공식 메뉴얼</a>,
 * <a href="https://docs.jboss.org/hibernate/validator/6.2/reference/en-US/html_single/#validator-defineconstraints-spec">검증 애노테이션 모음</a>
 */
public class BeanValidationTest {

    /**
     * <h2>빈 검증기(Bean Validation)를 직접 사용하는 방법</h2>
     * Item - Bean Validation 애노테이션 적용
     */
    @Test
    void beanValidation() {
        //검증기 생성
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory(); //공장을 꺼내고-
        Validator validator = factory.getValidator(); //검증기 추출-

        Item item = new Item();
        item.setItemName(" "); //공백
        item.setPrice(0);
        item.setQuantity(10000);

        //검증 실행
        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        for (ConstraintViolation<Item> violation : violations) {
            System.out.println("violation = " + violation); //검증 오류가 발생한 객체, 필드, 메시지 정보등 다양한 정보를 확인할 수 있다.
            System.out.println("violation.getMessage() = " + violation.getMessage()); //오류메시지는 하이버네이트에서 기본적으로 제공, 커스텀가능.
        }
    }
}
