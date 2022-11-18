package com.restaurant.smartfood.repostitory;

import com.restaurant.smartfood.entities.Member;
import com.restaurant.smartfood.entities.Person;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByPhoneNumber(String phoneNumber);

    @Modifying
    @Query(value = "insert into members (id, password) VALUES (?1, ?2)", nativeQuery = true)
    //@SQLInsert(sql = "insert into commit_activity_link (id, password) VALUES (?1, ?2)")
    void insertMember(Long id, String password);
}