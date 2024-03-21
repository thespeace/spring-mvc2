package thespeace.springmvc2.itemservice.web.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import thespeace.springmvc2.itemservice.domain.item.Item;

/**
 * 스프링은 검증을 체계적으로 제공하기 위해 다음 인터페이스를 제공한다.
 * <blockquote><pre>
 *  public interface Validator {
 *     boolean supports(Class<?> clazz);
 *     void validate(Object target, Errors errors);
 *  }
 * </pre></blockquote>
 * <ul>
 *     <li>supports() {} : 해당 검증기를 지원하는 여부 확인</li>
 *     <li>validate(Object target, Errors errors) : 검증 대상 객체와 BindingResult</li>
 * </ul>
 */
@Component
public class ItemValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Item.class.isAssignableFrom(clazz); // isAssignableFrom : item == clazz, item == subItem, 자식 클래스까지 다 커버.
    }

    @Override
    public void validate(Object target, Errors errors) { //Errors : BindingResult의 부모클래스.
        Item item = (Item) target;

        //검증 로직
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "itemName", "required"); //ValidationUtils 사용, 제공하는 기능은 Empty , 공백 같은 단순한 기능만 제공

        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            errors.rejectValue("price", "range", new Object[]{1000,1000000}, null);
        }
        if(item.getQuantity() == null || item.getQuantity() > 9999) {
            errors.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        //특정 필드의 범위를 넘어서는 복합 룰 검증 로직
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
                errors.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }
    }
}
