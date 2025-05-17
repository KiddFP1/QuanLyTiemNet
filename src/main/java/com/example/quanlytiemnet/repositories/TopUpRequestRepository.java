package com.example.quanlytiemnet.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.quanlytiemnet.models.TopUpRequest;
import com.example.quanlytiemnet.models.Member;

public interface TopUpRequestRepository extends JpaRepository<TopUpRequest, Integer> {
    List<TopUpRequest> findByMemberOrderByRequestDateDesc(Member member);

    List<TopUpRequest> findByStatusOrderByRequestDateDesc(String status);
}