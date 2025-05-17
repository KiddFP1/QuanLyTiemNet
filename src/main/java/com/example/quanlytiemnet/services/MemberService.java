// MemberService.java
package com.example.quanlytiemnet.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.quanlytiemnet.models.Member;
import com.example.quanlytiemnet.repositories.MemberRepository;

@Service
public class MemberService {
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<Member> getAllMembers() {
		return memberRepository.findAll();
	}

	public Member createMember(Member member) {
		member.setPassword(member.getPassword());
		member.setCreatedDate(LocalDateTime.now());
		member.setBalance(BigDecimal.ZERO);
		member.setPoints(0);
		return memberRepository.save(member);
	}

	public Member updateMember(Member member) {
		Member existingMember = getMemberById(member.getId());
		member.setPassword(existingMember.getPassword());
		member.setBalance(existingMember.getBalance());
		member.setPoints(existingMember.getPoints());
		member.setCreatedDate(existingMember.getCreatedDate());
		return memberRepository.save(member);
	}

	public void deleteMember(Integer id) {
		memberRepository.deleteById(id);
	}

	public Member getMemberById(Integer id) {
		return memberRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy thành viên"));
	}

	public void updateBalance(Integer memberId, BigDecimal amount) {
		Member member = getMemberById(memberId);
		member.setBalance(member.getBalance().add(amount));
		memberRepository.save(member);
	}
}