package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name  = "orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY) // 여러개의 주문(Many)이 하나의 멤버랑 매핑됨(One)
    @JoinColumn(name = "member_id") // Foreign Key의 이름
    private Member member;
//    private Member member = new ByteBuddyInterceptor(); // 가짜 프록시 객체

    //JPQL select o From order o; -> SQL select * from order n+1(n:첫번째 가져온 쿼리);

//    persist(orderItemA)
//    persist(orderItemB)
//    persist(orderItemC)
//    persist(order)
//    원래는 이렇게 각각 persist를 해줘야한다
//    하지만 cascade를 두면
//    persist(order)만으로 가능하다
    @BatchSize(size = 1000)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    // 스프링부트의 'SpringPhysicalNamingStrategy'에 의해서
    //1. 카멜케이스 -> 언더스코어(memberpoint -> member_point)
    //2. .(점) -> _(언더스코어)
    //3. 대문자 -> 소문자 로 변경이된다
    // order_date
    private LocalDateTime orderDate; // 주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문상태 [ORDER, CANCEL]

    // ==연관관계 편의 메서드(양방향을 원자적으로 묶는 메서드) ==//
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    // 원래는 복잡하다
//    Member member = new Member();
//    Order order = new Order();
//    member.getOrders().add(order);
//    order.setMember(member);

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==//
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비즈니스 로직==//
    /**
     * 주문 취소
     * @author Boki
     */
    public void cancel() {
        if(delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    //==조회 로직==//
    /**
     * 전체 주문 가격 조회
     * @author Boki
     */
    public int getTotalPrice() {
//        int totalPrice = 0;
//        for (OrderItem orderItem : orderItems) {
//            totalPrice += orderItem.getTotalPrice();
//        }
//        return totalPrice;
        return orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();
    }
}
