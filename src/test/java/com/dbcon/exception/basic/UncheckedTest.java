package com.dbcon.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class UncheckedTest {

    @Test
    void unchecked_catch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void unchecked_throw() {
        Service service = new Service();
        Assertions.assertThatThrownBy(service::callThrow)
                .isInstanceOf(MyUncheckedException.class);
    }

    /**
     * RuntimeException 을 상속받은 예외는 언체크 예외가 된다.
     */
    static class MyUncheckedException extends RuntimeException {
        public MyUncheckedException(String message) {
            super(message);
        }
    }

    /**
     * Unchecked 예외는
     * 예외를 잡거나, 던지지 않아도 된다.
     * 예외를 잡지 않으면 자동으로 밖으로 던진다.
     */
    static class Service {
        Repository repository = new Repository();

        /**
         * 필요한 경우 예외를 잡아서 처리하면 된다.
         */
        public void callCatch() {
            try {
                repository.call();
            } catch (MyUncheckedException e) {
                // 예외 처리 로직
                log.info("예외 처리 메세지 message={}", e.getMessage(), e);
            }
        }

        // 예외를 잡기 않아도 된다. 자연스럽게 상위로 넘어간다.
        // 체크 예외와 다르게 아래 throws 생략 가능
        public void callThrow() throws MyUncheckedException{
            repository.call();
        }
    }

    static class Repository {
        public void call() {
            throw new MyUncheckedException("ex"); // RuntimeException 을 상속받은 객체는 예외를 던지거나 잡지 않아도 컴파일 에러가 발생하지 않는다. (생략 가능)
        }
    }
}
