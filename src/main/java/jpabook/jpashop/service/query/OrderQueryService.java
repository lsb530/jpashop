package jpabook.jpashop.service.query;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryService {

    private final OrderRepository orderRepository;

    public List<OffOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllwithItem();

        List<OffOrderDto> result = orders.stream()
                .map(OffOrderDto::new)
                .collect(toList());
        return result;
    }
}

