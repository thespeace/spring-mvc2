package thespeace.springmvc2.itemservice.web.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import thespeace.springmvc2.itemservice.domain.item.Item;
import thespeace.springmvc2.itemservice.domain.item.ItemRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable("itemId") long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

    /**
     * <h2>BindingResult</h2>
     * 스프링이 제공하는 검증 오류를 보관하는 객체로 검증 오류가 발생하면 여기에 보관하여 사용된다.<br>
     * {@code BindingResult}가 있으면 {@code @ModelAttribute}에 데이터 바인딩 시 오류가 발생해도 컨트롤러가 호출된다!<br>
     * {@code BindingResult bindingResult}파라미터의 위치는 {@code @ModelAttribute Item item} 다음에 와야 한다.<br><br>
     * <ul>
     *     <li>{@code @ModelAttribute}에 바인딩 시 타입 오류가 발생하면? {@code BindingResult}가 없으면 - 400 오류가 발생하면서
     *          컨트롤러가 호출되지 않고,오류 페이지로 이동</li>
     *     <li>{@code BindingResult}가 있으면 - 오류 정보( FieldError )를 {@code BindingResult}에 담아서 컨트롤러를 정상 호출한다.</li>
     * </ul>
     *
     * <br>
     *
     * <h3>BindingResult에 검증 오류를 적용하는 3가지 방법</h3>
     * <ul>
     *     <li>{@code @ModelAttribute}의 객체에 타입 오류 등으로 바인딩이 실패하는 경우 스프링이 {@code FieldError}생성해서
     *         {@code BindingResult}에 넣어준다</li>
     *     <li>개발자가 직접 넣어준다.</li>
     *     <li>{@code Validator} 사용</li>
     * </ul>
     *
     * <br>
     *
     * <h2>BindingResult와 Errors</h2>
     * <ul>
     *     <li>org.springframework.validation.Errors</li>
     *     <li>org.springframework.validation.BindingResult</li>
     * </ul>
     * BindingResult 는 인터페이스이고, Errors 인터페이스를 상속받고 있다.<br>
     * 실제 넘어오는 구현체는 BeanPropertyBindingResult 라는 것인데, 둘다 구현하고 있으므로 BindingResult
     * 대신에 Errors 를 사용해도 된다. Errors 인터페이스는 단순한 오류 저장과 조회 기능을 제공한다.<br>
     * BindingResult 는 여기에 더해서 추가적인 기능들을 제공한다. addError() 도 BindingResult 가 제공하므로
     * 여기서는 BindingResult 를 사용하자. 주로 관례상 BindingResult 를 많이 사용한다.<br>
     *
     * <br><br>
     * <ul>
     *     <li>FieldError 생성자 요약
     *         <ul>
     *             <li>{@code public FieldError(String objectName, String field, String defaultMessage) {}}</li>
     *             <li>필드에 오류가 있으면 {@code FieldError} 객체를 생성해서 {@code bindingResult}에 담아두면 된다.</li>
     *             <li>{@code objectName} : {@code @ModelAttribute} 이름</li>
     *             <li>{@code field} : 오류가 발생한 필드 이름</li>
     *             <li>{@code defaultMessage} : 오류 기본 메시지</li>
     *         </ul>
     *     </li>
     * </ul>
     * <ul>
     *     <li>ObjectError 생성자 요약
     *         <ul>
     *             <li>{@code public ObjectError(String objectName, String defaultMessage) {}}</li>
     *             <li>특정 필드를 넘어서는 오류가 있으면 {@code ObjectError} 객체를 생성해서 {@code bindingResult}에 담아두면 된다.</li>
     *             <li>{@code objectName} : {@code @ModelAttribute}의 이름</li>
     *             <li>{@code defaultMessage} : 오류 기본 메시지</li>
     *         </ul>
     *     </li>
     * </ul>
     */
    @PostMapping("/add")
    public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        //검증 로직
        if(!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item","itemName","상품 이름은 필수입니다."));
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item","price","가격은 1,000 ~ 1,000,000 까지 허용합니다."));
        }
        if(item.getQuantity() == null || item.getQuantity() > 9999) {
            bindingResult.addError(new FieldError("item","quantity","수량은 최대 9,999 까지 허용합니다."));
        }

        //특정 필드의 범위를 넘어서는 복합 룰 검증 로직
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item","가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }

        //검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()) {
            log.info("errors = {} ", bindingResult);
            return "validation/v2/addForm"; //BindingResult는 자동으로 model에 담긴다.
        }

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable("itemId") Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable("itemId") Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

}

