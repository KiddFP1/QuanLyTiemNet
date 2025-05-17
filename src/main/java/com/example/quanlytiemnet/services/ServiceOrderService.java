package com.example.quanlytiemnet.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.quanlytiemnet.models.Member;
import com.example.quanlytiemnet.models.ServiceOrder;
import com.example.quanlytiemnet.repositories.ServiceOrderRepository;

@Service
@Transactional
public class ServiceOrderService {
    @Autowired
    private ServiceOrderRepository serviceOrderRepository;

    public ServiceOrder createOrder(ServiceOrder order) {
        return serviceOrderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<ServiceOrder> getMemberOrders(Member member) {
        return serviceOrderRepository.findByMemberOrderByOrderDateDesc(member);
    }

    @Transactional(readOnly = true)
    public List<ServiceOrder> getPendingOrders() {
        return serviceOrderRepository.findByStatusOrderByOrderDateDesc("PENDING");
    }

    public ServiceOrder updateOrder(ServiceOrder order) {
        return serviceOrderRepository.save(order);
    }
}