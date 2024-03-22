package thespeace.springmvc2.itemservice.domain.item;

import jakarta.validation.constraints.Max; // jakarta.validation는 특정 구현에 관계없이 제공되는 표준 인터페이스.
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range; //org.hibernate.validator은 하이버네이트 validator 구현체를 사용할 때만 제공되는 검증 기능, 실무에서도 하이버네이트 validator를 사용하므로 자유롭게 사용해도 된다.
import org.hibernate.validator.constraints.ScriptAssert;

@Data
//@ScriptAssert(lang = "javascript", script = "_this.price * _this.quantity >= 10000", message = "총합이 10000원 넘게 입력해주세요.")
//ObjectError 처리 수행, 하지만 제약이 많고 복잡하여 실무 검증 기능이 해당 객체의 범위를 넘어서는 경우들도 종종 등장하는데, 그런 경우 즉각 대응이 어렵다.
//자바 코드로 작성 권장!
public class Item {

    @NotNull(groups = UpdateCheck.class) //수정 요구사항 추가
    private Long id;

    @NotBlank(message = "공백X", groups = {SaveCheck.class, UpdateCheck.class}) // 빈값 + 공백만 있는 경우를 허용하지 않는다.
    private String itemName;

    @NotNull(groups = {SaveCheck.class, UpdateCheck.class}) //`null`을 허용하지 않는다.
    @Range(min = 1000, max = 1000000, groups = {SaveCheck.class, UpdateCheck.class}) //범위 안의 값이어야 한다.
    private Integer price;

    @NotNull(groups = {SaveCheck.class, UpdateCheck.class})
    @Max(value = 9999, groups = SaveCheck.class) //최대 9999까지만 허용, 수정 요구사항은 무제한.
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
