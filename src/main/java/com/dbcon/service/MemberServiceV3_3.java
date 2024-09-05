package com.dbcon.service;

import com.dbcon.domain.Member;
import com.dbcon.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - @Transactional AOP
 */

@Slf4j
public class MemberServiceV3_3 {
    private final MemberRepositoryV3 memberRepository;

    // DI : MemberRepositoryV3와 PlatformTransactionManager 는 의존성 주입을 통해 외부에서 주입받습니다.
    // TransactionTemplate 은 외부에서 주입받지 않고, 생성자 내부에서 직접 생성되므로 이는 의존성 주입이 아닌 내부에서 객체를 생성하는 방식입니다.
    public MemberServiceV3_3(MemberRepositoryV3 memberRepository) {
        this.memberRepository = memberRepository;
    }

    // @Transactional : (중요!!!!) 프록시 생성
    @Transactional // @Transactional 을 적용하면 executeWithOutResult 와 같은 기능을 한다 (해당 메서드가 실행되면 트랜잭션을 시작하고, commit, rollback 을 수행)
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        // 비즈니스 로직
        bizLogic(fromId, toId, money);
    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        // 비즈니스 로직 수행
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    // IllegalStateException : 부적절한 인수를 메서드에 건네줬을 때 나타내는 예외
    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
