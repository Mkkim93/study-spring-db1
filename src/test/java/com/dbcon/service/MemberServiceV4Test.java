package com.dbcon.service;

/**
 * 예외 누수 문제 해결
 * SQLException 제거
 * MemberRepository 인터페이스에 의존
 */

import com.dbcon.domain.Member;
import com.dbcon.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest // test 를 실행할때 springBoot test 를 인식하고 필요한 spring bean 을 등록한다
class MemberServiceV4Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberServiceV4 memberService;

    @AfterEach // 모든 테스트가 끝나면 젤 마지막에 한번 수행
    void after(){
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }

    @TestConfiguration
    static class TestConfig {

        private final DataSource dataSource;

        public TestConfig(DataSource dataSource) {
            this.dataSource = dataSource;
        }
        @Bean
        MemberRepository memberRepository() {
//            return new MemberRepositoryV4_2(dataSource); //단순 예외 변환
            return new MemberRepositoryV6(dataSource);
        }
        @Bean
        MemberServiceV4 memberServiceV4() {
            return new MemberServiceV4(memberRepository());
        }
    }

    @Test
    void AopCheck() {
        log.info("memberService class={}", memberService.getClass()); // proxy 코드
        log.info("memberRepository class={}", memberRepository.getClass());

        // memberService 와 memberRepository 둘다 프록시 객체인지 확인하는 방법
        // assertThat(AopUtils.isAopProxy(memberService)).isEqualTo(AopUtils.isAopProxy(memberRepository));
        assertThat(AopUtils.isAopProxy(memberService));
        assertThat(AopUtils.isAopProxy(memberRepository));
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() {
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
    void accountTransferEx(){
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