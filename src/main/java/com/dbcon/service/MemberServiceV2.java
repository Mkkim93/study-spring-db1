package com.dbcon.service;

import com.dbcon.domain.Member;
import com.dbcon.repository.MemberRepositoryV1;
import com.dbcon.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, 풀을(POOL) 을 고려한 종료
 */

@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();
        try {
            con.setAutoCommit(false); // 트랙잭션 시작
            bizLogic(con, fromId, toId, money);
            con.commit(); // 정상로직 수행 후 commit() 수행
        } catch (Exception e) {
            con.rollback(); // 실패시 rollback() 수행
            throw new IllegalStateException(e);
        } finally {
            release(con);
        }
    }

    private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
        // 비즈니스 로직 수행
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);

        memberRepository.update(con, fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(con, toId, toMember.getMoney() + money);
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
