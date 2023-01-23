package com.restaurant.smartfood.service;

import com.restaurant.smartfood.entities.Employee;
import com.restaurant.smartfood.entities.Member;
import com.restaurant.smartfood.exception.ConflictException;
import com.restaurant.smartfood.exception.ResourceNotFoundException;
import com.restaurant.smartfood.repostitory.EmployeeRepository;
import com.restaurant.smartfood.repostitory.MemberRepository;
import com.restaurant.smartfood.repostitory.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
class MemberServiceTest {
    @Autowired
    private MemberService memberService;
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private PersonService personService;
    @MockBean
    private PersonRepository personRepository;
    private Member existedMember;
    @BeforeEach
    void setUp() {
        existedMember = Member.builder()
                .phoneNumber("0522222222")
                .email("a@g.com")
                .id(1L)
                .password("123")
                .name("Dani Cohen")
                .build();
        Mockito.doReturn(Optional.of(existedMember)).when(personRepository).findByPhoneNumber(existedMember.getPhoneNumber());
        Mockito.doReturn(Optional.of(existedMember)).when(memberRepository).findById(existedMember.getId());
        Mockito.doReturn(Optional.empty()).when(memberRepository).findById(2l);
    }
    @Test
    void addMemberTwice() {
        var member= Member.builder()
                .phoneNumber("0522222222")
                .email("a@g.com")
                .id(1L)
                .password("123")
                .name("Dani Cohen")
                .build();
        var expectedException = ConflictException.class;
        assertThrows(expectedException,()->memberService.addMember(member));
    }
    @Test
    void updateMemberNotFound(){
        var member= Member.builder()
                .phoneNumber("0522222222")
                .email("a@g.com")
                .id(2L)
                .name("Dani Cohen")
                .build();
        var expectedException = ResourceNotFoundException.class;
        assertThrows(expectedException,()->memberService.updateMember(member));
    }
}