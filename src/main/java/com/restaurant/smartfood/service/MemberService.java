package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.Member;
import com.restaurant.smartfood.entities.Person;
import com.restaurant.smartfood.repostitory.MemberRepository;
import com.restaurant.smartfood.repostitory.PersonRepository;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Transactional
@Service
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PersonService personService;
    @Autowired
    private PersonRepository personRepository;

    public Member saveMember(Member member) { //TODO: add member that already person fails
        personRepository.findByPhoneNumber(member.getPhoneNumber()).ifPresentOrElse(
                personFromDB -> {
                    // person is existed in DB
                    if (member.getEmail() != null && !member.getEmail().equals(personFromDB.getEmail())) // email updated
                        personService.validateEmail(member);
                    member.setId(personFromDB.getId());
                    // validate that was not saved already
                    memberRepository.findById(member.getId()).ifPresent((p) -> {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Member id existed");
                    });
                    memberRepository.insertMember(personFromDB.getId(), member.getPassword());
                },
                // person is NOT in DB -> save new member
                () -> {
                    personService.validateFields(member);
                    var memberDB = memberRepository.save(member);
                    member.setId(memberDB.getId());
                }
        );
        return member;
    }

    public Member getMemberByPhoneNumber(String phoneNumber) {
        return memberRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no member with phone number: " + phoneNumber));
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }
}
