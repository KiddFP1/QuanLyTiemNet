package com.example.quanlytiemnet.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.quanlytiemnet.models.ServiceOrder;
import com.example.quanlytiemnet.models.Member;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Integer> {
    List<ServiceOrder> findByMemberOrderByOrderDateDesc(Member member);

    List<ServiceOrder> findByStatusOrderByOrderDateDesc(String status);
}