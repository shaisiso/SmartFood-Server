package com.restaurant.smartfood.controller;

import com.restaurant.smartfood.entities.Member;
import com.restaurant.smartfood.security.AuthorizeEmployee;
import com.restaurant.smartfood.security.AuthorizeMember;
import com.restaurant.smartfood.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
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
        return memberService.addMember(newMember);
    }

    @PutMapping
    @AuthorizeMember
    public Member updateMember(@RequestBody @Valid Member member) {
        return memberService.updateMember(member);
    }

    @GetMapping("/{phone}")
    public Member getMemberByPhoneNumber(@PathVariable("phone") String phoneNumber){
        return memberService.getMemberByPhoneNumber(phoneNumber);
    }

    @GetMapping
    @AuthorizeEmployee
    public List<Member> getAllMembers(){
        return memberService.getAllMembers();
    }
}
