package thespeace.springmvc2.account.domain.item;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AccountItemRepository {

    private static final Map<Long, AccountItem> store = new HashMap<>(); //static
    private static long sequence = 0L; //static

    public AccountItem save(AccountItem item) {
        item.setId(++sequence);
        store.put(item.getId(), item);
        return item;
    }

    public AccountItem findById(Long id) {
        return store.get(id);
    }

    public List<AccountItem> findAll() {
        return new ArrayList<>(store.values());
    }

    public void update(Long itemId, AccountItem updateParam) {
        AccountItem findItem = findById(itemId);
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
    }

    public void clearStore() {
        store.clear();
    }
}
