package com.restaurant.smartfood.repostitory;

import com.restaurant.smartfood.entities.Member;
import com.restaurant.smartfood.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByPhoneNumber(String phoneNumber);
}