package thespeace.springmvc2.itemservice.web.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
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
    //@PostMapping("/add")
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

    /**
     * <h2>FieldError, ObjectError</h2>
     * <ul>-FieldError 생성자
     *     <li>{@code public FieldError(String objectName, String field, String defaultMessage);}</li>
     *     <li>{@code public FieldError(String objectName, String field, @Nullable Object
     *         rejectedValue, boolean bindingFailure, @Nullable String[] codes, @Nullable
     *         Object[] arguments, @Nullable String defaultMessage)}</li>
     *     <ul>
     *         <li>objectName : 오류가 발생한 객체 이름</li>
     *         <li>field : 오류 필드</li>
     *         <li>rejectedValue : 사용자가 입력한 값(거절된 값)을 저장하는 필드</li>
     *         <li>bindingFailure : 타입 오류 같은 바인딩 실패인지, 검증 실패인지 구분 값</li>
     *         <li>codes : 메시지 코드</li>
     *         <li>arguments : 메시지에서 사용하는 인자</li>
     *         <li>defaultMessage : 기본 오류 메시지</li>
     *     </ul>
     * </ul>
     */
//    @PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        //검증 로직
        if(!StringUtils.hasText(item.getItemName())) {
            //'FieldError'는 오류 발생시 사용자 입력 값 유지해주는 기능을 제공한다.
            bindingResult.addError(new FieldError("item","itemName", item.getItemName(), false, null, null, "상품 이름은 필수입니다."));
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item","price", item.getPrice(), false, null, null, "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
        }
        if(item.getQuantity() == null || item.getQuantity() > 9999) {
            bindingResult.addError(new FieldError("item","quantity", item.getQuantity(), false, null, null, "수량은 최대 9,999 까지 허용합니다."));
        }

        //특정 필드의 범위를 넘어서는 복합 룰 검증 로직
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", null, null, "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
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

    /**
     * <h2>FieldError , ObjectError 의 생성자는 codes , arguments 를 제공</h2>
     * 오류 발생시 오류 코드로 메시지를 찾기 위해 사용.
     * <ul>
     *     <li>codes : required.item.itemName 를 사용해서 메시지 코드를 지정한다. 메시지 코드는 하나가 아니라 배
     *                 열로 여러 값을 전달할 수 있는데, 순서대로 매칭해서 처음 매칭되는 메시지가 사용된다.</li>
     *     <li>arguments : Object[]{1000, 1000000} 를 사용해서 코드의 {0} , {1} 로 치환할 값을 전달한다.</li>
     * </ul>
     */
//    @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        //검증 로직
        if(!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item","itemName", item.getItemName(), false, new String[]{"required.item.itemName"}, null, null));
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item","price", item.getPrice(), false, new String[]{"range.item.price"}, new Object[]{1000,1000000}, null));
        }
        if(item.getQuantity() == null || item.getQuantity() > 9999) {
            bindingResult.addError(new FieldError("item","quantity", item.getQuantity(), false, new String[]{"max.item.quantity"}, new Object[]{9999}, null));
        }

        //특정 필드의 범위를 넘어서는 복합 룰 검증 로직
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", new String[]{"totalPriceMin"}, new Object[]{10000, resultPrice}, null));
            }
        }

        //검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()) {
            log.info("errors = {} ", bindingResult);
            return "validation/v2/addForm";
        }

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    /**
     * <h2>rejectValue() , reject()</h2>
     * BindingResult 가 제공하는 rejectValue() , reject() 를 사용하면 FieldError , ObjectError 를 직접
     * 생성하지 않고, 깔끔하게 검증 오류를 다룰 수 있다.
     * <ul>-rejectValue()
     *     <li>{@code void rejectValue(@Nullable String field, String errorCode,@Nullable Object[] errorArgs, @Nullable String defaultMessage);}</li>
     *     <li>field : 오류 필드명</li>
     *     <li>errorCode : 오류 코드(이 오류 코드는 메시지에 등록된 코드가 아니다. messageResolver를
     *                     위한 오류 코드이다.)</li>
     *     <li>errorArgs : 오류 메시지에서 {0} 을 치환하기 위한 값</li>
     *     <li>defaultMessage : 오류 메시지를 찾을 수 없을 때 사용하는 기본 메시지</li>
     * </ul>
     */
    @PostMapping("/add")
    public String addItemV4(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        //BindingResult 는 검증해야 할 객체인 target 바로 다음에 온다. 따라서 BindingResult 는 이미 본인이 검증해야 할 객체인 target 을 알고 있다.
        log.info("objectName={}", bindingResult.getObjectName());
        log.info("target={}", bindingResult.getTarget());

        //검증 로직
        if(!StringUtils.hasText(item.getItemName())) { //ValidationUtils 사용 전-
            //앞에서 BindingResult 는 어떤 객체를 대상으로 검증하는지 target을 이미 알고 있다고 했다. 따라서 target(item)에 대한 정보는 없어도 된다.
            bindingResult.rejectValue("itemName","required");
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "itemName", "required"); //ValidationUtils 사용, 제공하는 기능은 Empty , 공백 같은 단순한 기능만 제공

        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.rejectValue("price", "range", new Object[]{1000,1000000}, null);
        }
        if(item.getQuantity() == null || item.getQuantity() > 9999) {
            bindingResult.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        //특정 필드의 범위를 넘어서는 복합 룰 검증 로직
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        //검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()) {
            log.info("errors = {} ", bindingResult);
            return "validation/v2/addForm";
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

