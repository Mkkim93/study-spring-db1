package com.dbcon.service;

/**
 *  트랜잭션 - 트랜잭션 매니저
 */

import com.dbcon.domain.Member;
import com.dbcon.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.SQLException;

import static com.dbcon.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class MemberServiceV3_1Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    private MemberRepositoryV3 memberRepository;
    private MemberServiceV3_1 memberService;

    @BeforeEach // 각 단위별 테스트가 수행되기전 무조건 한번 수행 (=단위 테스트와 1:1 매칭 같은 느낌)
    void before() {
        // 1. DriverManagerDataSource 에서 데이터베이스 커넥션을 위한 정보를 초기화 한다.
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepository = new MemberRepositoryV3(dataSource); // 해당 정보를 repository 에 넣는다. (MemberRepositoryV3)

        // 2. 트랜잭션 동기화를 위한 과정
        // 트랜잭션을 수행하기 위해 transactionManager 객체를 생성하고 해당 인스턴스에 dataSource 를 넣는다
        // transactionManager 에서 dataSource 변수를 통해 커넥션이 생성된 상태이다.
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);

        // memberService 에 커넥션이 완료된 transactionManager 와, repository 의 정보를 초기화 해준다.
        memberService = new MemberServiceV3_1(transactionManager, memberRepository);
    }

    @AfterEach // 모든 테스트가 끝나면 젤 마지막에 한번 수행
    void after() throws SQLException {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {
        // given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        // when
        memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);

        // then
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberB.getMemberId());

        // assertThat(findMemberA.getMoney()).isEqualTo(findMemberB.getMoney());
        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("이체중 예외 발생")
    void accountTransferEx() throws SQLException {
        // given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberEX = new Member(MEMBER_EX, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberEX);

        // when
        assertThatThrownBy(() -> memberService.accountTransfer(memberA.getMemberId(),
                memberEX.getMemberId(), 2000))
                .isInstanceOf(IllegalStateException.class);

        // then
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberEX.getMemberId());

        // assertThat(findMemberA.getMoney()).isEqualTo(findMemberB.getMoney());
        assertThat(findMemberA.getMoney()).isEqualTo(10000);
        assertThat(findMemberB.getMoney()).isEqualTo(10000);
    }
}