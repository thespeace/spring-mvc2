package thespeace.springmvc2.typeconverter.type;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * <h1>사용자 정의 타입 컨버터</h1>
 * 127.0.0.1:8080 과 같은 IP, PORT를 입력하면 IpPort 객체로 변환하는 컨버터를 만들어보자.
 */
@Getter
@EqualsAndHashCode //모든 필드를 사용해서 equals() , hashcode() 를 생성한다. 따라서 모든 필드의 값이 같다면 a.equals(b) 의 결과가 참(true)이 된다.
public class IpPort {
    private String ip;
    private int port;

    public IpPort(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
}
