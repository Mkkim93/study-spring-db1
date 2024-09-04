package com.dbcon.repository;

import com.dbcon.domain.Member;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static com.dbcon.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class MemberRepositoryTest {

    MemberRepositoryV0 repository;
    private static final String TestMemberId = "M004";

    @BeforeEach
    void beforeEach() {
        // 기본 DriverManager - 항상 새로운 커넥션을 획득
        // DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("MyPool");

        repository = new MemberRepositoryV0(dataSource);
    }

    @Test
    void save() throws SQLException {
        // save
        Member member = new Member(TestMemberId, 20000);
        repository.save(member);
    }

    @Test
    void findById() throws SQLException {
        repository.findById(TestMemberId);
    }

    @Test
    void deleteId() throws SQLException {
        repository.delete(TestMemberId);
    }

    @Test
    @DisplayName("회원 정보 업데이트 테스트")
    void update() throws SQLException {
        Member member = new Member(TestMemberId, 15000);
        // repository.update(TestMemberId, member.getMoney(), member.getMoney());
    }
}