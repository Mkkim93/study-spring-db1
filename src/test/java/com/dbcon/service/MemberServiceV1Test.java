package com.dbcon.service;

/**
 *  기본 동작, 트랜잭션이 없어서 문제 발생
 */

import com.dbcon.domain.Member;
import com.dbcon.repository.MemberRepositoryV0;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

import static com.dbcon.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;

@Slf4j
class MemberServiceV1Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    private MemberRepositoryV0 repository;
    private MemberServiceV1 memberService;

    @BeforeEach // 각 단위별 테스트가 수행되기전 무조건 한번 수행 (=단위 테스트와 1:1 매칭 같은 느낌)
    void before() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        repository = new MemberRepositoryV0(dataSource);
        memberService = new MemberServiceV1(repository);
    }

    @AfterEach // 모든 테스트가 끝나면 젤 마지막에 한번 수행
    void after() throws SQLException {
        repository.delete(MEMBER_A);
        repository.delete(MEMBER_B);
        repository.delete(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {
        // given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        repository.save(memberA);
        repository.save(memberB);

        // when
        memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);

        // then
        Member findMemberA = repository.findById(memberA.getMemberId());
        Member findMemberB = repository.findById(memberB.getMemberId());

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
        repository.save(memberA);
        repository.save(memberEX);

        // when
        assertThatThrownBy(() -> memberService.accountTransfer(memberA.getMemberId(),
                memberEX.getMemberId(), 2000)).isInstanceOf(IllegalStateException.class);

        // then
        Member findMemberA = repository.findById(memberA.getMemberId());
        Member findMemberB = repository.findById(memberEX.getMemberId());

        // assertThat(findMemberA.getMoney()).isEqualTo(findMemberB.getMoney());
        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(10000);
    }
}