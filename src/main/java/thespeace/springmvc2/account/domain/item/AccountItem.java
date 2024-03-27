package thespeace.springmvc2.account.domain.item;

import lombok.Data;

@Data
public class AccountItem {

    private Long id;
    private String itemName;
    private Integer price;
    private Integer quantity;

    public AccountItem() {
    }

    public AccountItem(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
