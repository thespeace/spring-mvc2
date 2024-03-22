package thespeace.springmvc2.itemservice.domain.item;

import jakarta.validation.constraints.Max; // jakarta.validation는 특정 구현에 관계없이 제공되는 표준 인터페이스.
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range; //org.hibernate.validator은 하이버네이트 validator 구현체를 사용할 때만 제공되는 검증 기능, 실무에서도 하이버네이트 validator를 사용하므로 자유롭게 사용해도 된다.

@Data
public class Item {

    private Long id;

    @NotBlank // 빈값 + 공백만 있는 경우를 허용하지 않는다.
    private String itemName;

    @NotNull //`null`을 허용하지 않는다.
    @Range(min = 1000, max = 1000000) //범위 안의 값이어야 한다.
    private Integer price;

    @NotNull
    @Max(9999) //최대 9999까지만 허용.
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
