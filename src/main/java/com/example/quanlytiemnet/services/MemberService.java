// MemberService.java
package com.example.quanlytiemnet.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.quanlytiemnet.models.Member;
import com.example.quanlytiemnet.models.TopUpHistory;
import com.example.quanlytiemnet.repositories.MemberRepository;
import com.example.quanlytiemnet.repositories.TopUpHistoryRepository;

@Service
@Transactional
public class MemberService {
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private TopUpHistoryRepository topUpHistoryRepository;

	@Transactional(readOnly = true)
	public List<Member> getAllMembers() {
		return memberRepository.findAll();
	}

	public Member createMember(Member member) {
		member.setPassword(passwordEncoder.encode(member.getPassword()));
		member.setCreatedDate(LocalDateTime.now());
		member.setBalance(BigDecimal.ZERO);
		member.setPoints(0);
		return memberRepository.save(member);
	}

	public Member updateMember(Member member) {
		Member existingMember = getMemberById(member.getId());

		if (StringUtils.hasText(member.getPassword())) {
			member.setPassword(passwordEncoder.encode(member.getPassword()));
		} else {
			member.setPassword(existingMember.getPassword());
		}

		if (member.getBalance() == null) {
			member.setBalance(existingMember.getBalance());
		}

		member.setPoints(existingMember.getPoints());
		member.setCreatedDate(existingMember.getCreatedDate());

		return memberRepository.save(member);
	}

	public void deleteMember(Integer id) {
		memberRepository.deleteById(id);
	}

	@Transactional(readOnly = true)
	public Member getMemberById(Integer id) {
		return memberRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy thành viên"));
	}

	public void updateBalance(Integer memberId, BigDecimal amount) {
		Member member = getMemberById(memberId);
		member.setBalance(member.getBalance().add(amount));
		memberRepository.save(member);
	}

	public List<TopUpHistory> getTopUpHistoryBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
		return topUpHistoryRepository.findByTopUpDateBetweenOrderByTopUpDateDesc(startDate, endDate);
	}

	@Transactional(readOnly = true)
	public Member getMemberByUsername(String username) {
		return memberRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("Member not found with username: " + username));
	}
}