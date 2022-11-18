package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.Member;
import com.restaurant.smartfood.entities.Person;
import com.restaurant.smartfood.service.MemberService;
import com.restaurant.smartfood.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @PostMapping
    public Member addMember(@RequestBody @Valid Member newMember) {
        return memberService.saveMember(newMember);
    }

    @PutMapping
    public Member updateMember(@RequestBody @Valid Member member) {
        return memberService.saveMember(member);
    }

    @GetMapping("/{phone}")
    public Member getMemberByPhoneNumber(@PathVariable("phone") String phoneNumber){
        return memberService.getMemberByPhoneNumber(phoneNumber);
    }

    @GetMapping
    public List<Member> getAllMembers(){
        return memberService.getAllMembers();
    }
}
