package thespeace.springmvc2.itemservice.domain.item;

/**
 * <h2>BeanValidation groups 기능</h2>
 * groups 기능을 사용해서 등록과 수정시에 각각 다르게 검증을 할 수 있었다.<br>
 * 그런데 groups 기능을 사용하니 {@link Item}은 물론이고, 전반적으로 복잡도가 올라갔다.<br>
 * 사실 groups 기능은 실제 잘 사용되지는 않는데, 그 이유는 실무에서는 등록용 폼 객체와 수정용 폼 객체를 분리해서 사용하기 때문이다.
 */
//저장용 그룹.
public interface SaveCheck {
}
