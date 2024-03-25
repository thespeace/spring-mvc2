package thespeace.springmvc2.account.web.session;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <h1>세션 관리</h1>
 * 세션을 사용해서 서버에서 중요한 정보를 관리하면 다음과 같은 보안 문제들을 해결할 수 있다.
 * <ul>
 *     <li>쿠키 값을 변조 가능 -> 예상 불가능한 복잡한 세션Id를 사용한다.</li>
 *     <li>쿠키에 보관하는 정보는 클라이언트 해킹시 털릴 가능성이 있다 -> 세션Id가 털려도 여기에는
 *         중요한 정보가 없다.</li>
 *     <li>쿠키 탈취 후 사용 해커가 토큰을 털어가도 시간이 지나면 사용할 수 없도록 서버에서 세션의
 *         만료시간을 짧게(예: 30분) 유지한다. 또는 해킹이 의심되는 경우 서버에서 해당 세션을 강제로
 *         제거하면 된다.</li>
 * </ul>
 *
 */
@Component
public class SessionManager {

    public static final String SESSION_COOKIE_NAME = "mySessionId";
    private Map<String, Object> sessionStore = new ConcurrentHashMap<>(); //HashMap 은 동시 요청에 안전하지 않다. 동시 요청에 안전한 ConcurrentHashMap 를 사용!

    /**
     * <h2>세션 생성</h2>
     * <ul>
     *     <li>sessionId 생성 (임의의 추정 불가능한 랜덤 값)</li>
     *     <li>세션 저장소에 sessionId와 보관할 값 저장</li>
     *     <li>sessionId로 응답 쿠키를 생성해서 클라이언트에 전달</li>
     * </ul>
     */
    public void createSession(Object value, HttpServletResponse response) {

        //세션 id를 생성하고, 값을 세션에 저장
        String sessionId = UUID.randomUUID().toString(); //UUID는 추정이 불가능, 중복확률이 극히 낮다.
        sessionStore.put(sessionId, value);

        //쿠키 생성
        Cookie mySessionCookie = new Cookie(SESSION_COOKIE_NAME, sessionId);
        response.addCookie(mySessionCookie);
    }

    /**
     * <h2>세션 조회</h2>
     * 클라이언트가 요청한 sessionId 쿠키의 값으로, 세션 저장소에 보관한 값 조회
     */
    public Object getSession(HttpServletRequest request) {
        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);
        if(sessionCookie == null) {
            return null;
        }
        return sessionStore.get(sessionCookie.getValue());
    }

    /**
     * <h2>세션 만료</h2>
     * 클라이언트가 요청한 sessionId 쿠키의 값으로, 세션 저장소에 보관한 sessionId와 값 제거
     */
    public void expire(HttpServletRequest request) {
        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);
        if(sessionCookie != null) {
            sessionStore.remove(sessionCookie.getValue());
        }
    }


    public Cookie findCookie(HttpServletRequest request, String cookieName) {
        if(request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findAny()
                .orElse(null);
    }

}
