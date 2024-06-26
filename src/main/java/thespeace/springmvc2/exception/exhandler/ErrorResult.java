package thespeace.springmvc2.exception.exhandler;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <h2>예외가 발생했을 때 API 응답으로 사용하는 객체를 정의</h2>
 */
@Data
@AllArgsConstructor
public class ErrorResult {
    private String code;
    private String message;
}
