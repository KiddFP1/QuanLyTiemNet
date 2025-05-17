package com.example.quanlytiemnet.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.quanlytiemnet.models.Member;
import com.example.quanlytiemnet.models.TopUpRequest;
import com.example.quanlytiemnet.repositories.TopUpRequestRepository;

@Service
@Transactional
public class TopUpRequestService {
    @Autowired
    private TopUpRequestRepository topUpRequestRepository;

    @Autowired
    private MemberService memberService;

    public TopUpRequest createRequest(TopUpRequest request) {
        return topUpRequestRepository.save(request);
    }

    @Transactional(readOnly = true)
    public List<TopUpRequest> getMemberRequests(Member member) {
        return topUpRequestRepository.findByMemberOrderByRequestDateDesc(member);
    }

    @Transactional(readOnly = true)
    public List<TopUpRequest> getPendingRequests() {
        return topUpRequestRepository.findByStatusOrderByRequestDateDesc("PENDING");
    }

    public TopUpRequest approveRequest(TopUpRequest request, String note) {
        request.setStatus("APPROVED");
        request.setNote(note);
        memberService.updateBalance(request.getMember().getId(), request.getAmount());
        return topUpRequestRepository.save(request);
    }

    public TopUpRequest rejectRequest(TopUpRequest request, String note) {
        request.setStatus("REJECTED");
        request.setNote(note);
        return topUpRequestRepository.save(request);
    }
}