package thespeace.springmvc2.itemservice.web.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import thespeace.springmvc2.itemservice.domain.item.Item;
import thespeace.springmvc2.itemservice.domain.item.ItemRepository;

import java.util.List;

/**
 * <h1>Bean Validator</h1>
 * <h3>스프링 MVC는 어떻게 Bean Validator를 사용할까?</h3>
 * 스프링 부트가 spring-boot-starter-validation 라이브러리를 넣으면 자동으로 Bean Validator를 인지하고
 * 스프링에 통합한다.<br><br>
 *
 * <h3>스프링 부트는 자동으로 글로벌 Validator로 등록한다.</h3>
 * `LocalValidatorFactoryBean` 을 글로벌 Validator로 등록한다. 이 Validator는 {@code @NotNull} 같은 애노테이션을
 * 보고 검증을 수행한다. 이렇게 글로벌 Validator가 적용되어 있기 때문에, {@code @Valid}, {@code @Validated}만 적용하면 된다.
 * 검증 오류가 발생하면, {@code FieldError}, {@code ObjectError}를 생성해서 {@code BindingResult}에 담아준다.<br><br>
 *
 * <h3>검증 순서</h3>
 * <blockquote><pre>
 * 1. @ModelAttribute 각각의 필드에 타입 변환 시도
 *     1_1. 성공하면 다음으로
 *     1_2. 실패하면 typeMismatch 로 FieldError 추가
 * 2. Validator 적용
 * </pre></blockquote>
 * <b>바인딩에 성공한 필드만 Bean Validation 적용</b><br>
 * BeanValidator는 바인딩에 실패한 필드는 BeanValidation을 적용하지 않는다.<br>
 * 생각해보면 타입 변환에 성공해서 바인딩에 성공한 필드여야 BeanValidation 적용이 의미 있다.<br>
 * (일단 모델 객체에 바인딩 받는 값이 정상으로 들어와야 검증도 의미가 있다.)<br><br>
 * {@code @ModelAttribute} -> 각각의 필드 타입 변환시도 -> 변환에 성공한 필드만 BeanValidation 적용<br>
 * <ul>예)
 *     <li>itemName 에 문자 "A" 입력 -> 타입 변환 성공 -> itemName 필드에 BeanValidation 적용</li>
 *     <li>price 에 문자 "A" 입력 -> "A"를 숫자 타입 변환 시도 실패 -> typeMismatch FieldError 추가 -> price 필드는
 *         BeanValidation 적용 X</li>
 * </ul>
 *
 * @reference : 검증시 @Validated @Valid 둘다 사용가능하다. `javax.validation.@Valid` 를 사용하려면 build.gradle 의존관계 추가가 필요하다.
 *        {@code @Valid}는 자바 표준 검증 애노테이션이다. 둘중 아무거나 사용해도 동일하게 작동하지만, @Validated 는 내부에 groups 라는 기능을 포함하고 있다.
 */
@Slf4j
@Controller
@RequestMapping("/validation/v3/items")
@RequiredArgsConstructor
public class ValidationItemControllerV3 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v3/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable("itemId") long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v3/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v3/addForm";
    }

    @PostMapping("/add")
    public String addItem(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if(bindingResult.hasErrors()) {
            log.info("errors = {} ", bindingResult);
            return "validation/v3/addForm";
        }

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v3/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable("itemId") Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v3/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable("itemId") Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v3/items/{itemId}";
    }

}

