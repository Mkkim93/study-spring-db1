package com.dbcon.service;

import com.dbcon.domain.Member;
import com.dbcon.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 템플릿
 */

@Slf4j
public class MemberServiceV3_2 {

//    private final DataSource dataSource;
//    private final PlatformTransactionManager transactionManager;
    private final TransactionTemplate txTemplate;
    private final MemberRepositoryV3 memberRepository;

    // DI : MemberRepositoryV3와 PlatformTransactionManager 는 의존성 주입을 통해 외부에서 주입받습니다.
    // TransactionTemplate 은 외부에서 주입받지 않고, 생성자 내부에서 직접 생성되므로 이는 의존성 주입이 아닌 내부에서 객체를 생성하는 방식입니다.
    public MemberServiceV3_2(PlatformTransactionManager transactionManager,
                             MemberRepositoryV3 memberRepository) {
        this.txTemplate = new TransactionTemplate(transactionManager);
        this.memberRepository = memberRepository;
    }

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        txTemplate.executeWithoutResult((status) -> { // 언체크예외일때 rollback 을 수행한다.
            // 비즈니스 로직
            try {
                bizLogic(fromId, toId, money);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        // 비즈니스 로직 수행
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }


    /**
     * @param con
     * 커넥션 풀을 고려한 메서드 (+주요 기능)
     * 1. Autocommit(true)
     * 2. con.close();
     */
    private static void release(Connection con) {
        if (con != null) {
            try {
                con.setAutoCommit(true); // 커넥션 풀 고려 (항상 true 이기떄문에) 그러나 트랜잭션 로직 수행 시 false 로 변경했으니 다시 default 로 변경 해줘야함
                con.close();
            } catch (Exception e) {
                log.error("error", e);
            }
        }
    }

    // IllegalStateException : 부적절한 인수를 메서드에 건네줬을 때 나타내는 예외
    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
