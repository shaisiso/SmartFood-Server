package com.restaurant.smartfood.repostitory;

import com.restaurant.smartfood.entities.Member;
import com.restaurant.smartfood.entities.Person;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByPhoneNumber(String phoneNumber);

    @Modifying
    @Query(value = "insert into members (id, password) VALUES (?1, ?2)", nativeQuery = true)
    void insertMember(Long id, String password);

    @Modifying
    @Query(value = "update  members set password=:password where id=:id", nativeQuery = true)
    void updateMember(@Param("id") Long id,@Param("password") String password);
}