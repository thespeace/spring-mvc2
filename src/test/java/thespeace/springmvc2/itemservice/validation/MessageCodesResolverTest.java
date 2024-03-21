package thespeace.springmvc2.itemservice.validation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.ObjectError;

import static org.assertj.core.api.Assertions.*;

/**
 * <h2>MessageCodesResolver</h2>
 * 객체명과 필드명을 조합해 메세지가 있는 지 확인 후 없으면 좀 더 범용적인 메세지를 선택하도록 하는 기능.<br>
 * <ul>
 *     <li>검증 오류 코드로 메시지 코드들을 생성한다.</li>
 *     <li>MessageCodesResolver 인터페이스이고 DefaultMessageCodesResolver 는 기본 구현체이다.</li>
 *     <li>주로 다음과 함께 사용 ObjectError , FieldError</li>
 * </ul>
 */
public class MessageCodesResolverTest {

    MessageCodesResolver codesResolver = new DefaultMessageCodesResolver();

    /**
     * <h2>DefaultMessageCodesResolver의 기본 메시지 생성 규칙 : 객체 오류</h2>
     * <ul>-객체 오류의 경우 다음 순서로 2가지 생성
     *     <li>1.: code + "." + object name</li>
     *     <li>2.: code</li>
     * </ul>
     */
    @Test
    void messageCodesResolverObject() {
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item");
        for (String messageCode : messageCodes) {
            System.out.println("messageCode = " + messageCode);
            // 객체 오류 -> 1. required.item, 2.required
        }
        assertThat(messageCodes).containsExactly("required.item", "required");
    }

    /**
     * <h2>DefaultMessageCodesResolver의 기본 메시지 생성 규칙 : 필드 오류</h2>
     * <ul>-필드 오류의 경우 다음 순서로 4가지 메시지 코드 생성
     *     <li>1.: code + "." + object name + "." + field</li>
     *     <li>2.: code + "." + field</li>
     *     <li>3.: code + "." + field type</li>
     *     <li>4.: code</li>
     * </ul>
     *
     * <br>
     *
     * <h3>동작 방식</h3>
     * <ul>
     *     <li>rejectValue() , reject() 는 내부에서 MessageCodesResolver 를 사용한다. 여기에서 메시지 코드들을 생성한다.</li>
     *     <li>FieldError , ObjectError 의 생성자를 보면, 오류 코드를 하나가 아니라 여러 오류 코드를 가질 수 있다.<br>
     *         MessageCodesResolver 를 통해서 생성된 순서대로 오류 코드를 보관한다</li>
     * </ul>
     *
     * <br>
     *
     * <h3>오류 메시지 출력</h3>
     * 타임리프 화면을 렌더링 할 때 th:errors 가 실행된다. 만약 이때 오류가 있다면 생성된 오류 메시지 코드를 순서대로
     * 돌아가면서 메시지를 찾는다. 그리고 없으면 디폴트 메시지를 출력한다.
     */
    @Test
    void messageCodesResolverField() {
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item", "itemName", String.class);
        for (String messageCode : messageCodes) {
            System.out.println("messageCode = " + messageCode);
            // 필드 오류 -> 1.required.item.itemName, 2.required.itemName, 3.required.java.lang.String, 4.required
        }
        assertThat(messageCodes).containsExactly(
                "required.item.itemName",
                "required.itemName",
                "required.java.lang.String",
                "required"
        );
    }
}
