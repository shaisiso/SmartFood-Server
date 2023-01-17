package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.Member;
import com.restaurant.smartfood.entities.Person;
import com.restaurant.smartfood.messages.MessageService;
import com.restaurant.smartfood.repostitory.MemberRepository;
import com.restaurant.smartfood.repostitory.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PersonService personService;
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageService messageService;

    @Autowired
    public MemberService(MemberRepository memberRepository, PersonService personService, PersonRepository personRepository, PasswordEncoder passwordEncoder, MessageService messageService) {
        this.memberRepository = memberRepository;
        this.personService = personService;
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
        this.messageService = messageService;
    }

    public Member updateMember(Member member) {
        Optional<Member> opMember = memberRepository.findById(member.getId());
        if (opMember.isPresent()) {
            personService.updatePerson(member);
            memberRepository.updateMember(member.getId(),
                    passwordEncoder.encode(member.getPassword()));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no member with this member id: " + member.getId());
        }
        return member;
    }

    public Member addMember(Member member) {
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        Optional<Person> opPerson = personRepository.findByPhoneNumber(member.getPhoneNumber());
        if (opPerson.isPresent()) {
            Person personFromDB = opPerson.get();
            // person is existed in DB
            if (member.getEmail() != null && !member.getEmail().equals(personFromDB.getEmail())) // email updated
                personService.validateEmail(member);
            member.setId(personFromDB.getId());
            // validate that was not saved already
            memberRepository.findById(member.getId()).ifPresent((p) -> {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Member id existed");
            });
            memberRepository.insertMember(personFromDB.getId(), member.getPassword());
        } else { // person is NOT in DB -> save new member
            personService.validateFields(member);
            Member memberDB = memberRepository.save(member);
            member.setId(memberDB.getId());
        }
        messageService.sendMessages(member, "Member Registration", "Welcome to Smart Food !!" +
                " You are registered as Smart Food member. Our members  are entitled to special discounts. We wish you a good day. ");
        return member;
    }

    public Member getMemberByPhoneNumber(String phoneNumber) {
        return memberRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no member with phone number: " + phoneNumber));
    }

    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no member with id: " + memberId));
    }

    public Boolean isMember(String phoneNumber) {
        return memberRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }
}

