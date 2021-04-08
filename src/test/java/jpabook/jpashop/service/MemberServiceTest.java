package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest // SpringBoot가 올라간 상태에서 테스트를 실행한다.(이게 없으면 Autowired가 다 안됨)
@Transactional //Rollback을 해버림(Insert문 같은동작을 못봄)
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    /**
     * 회원가입
     * @author Boki
     * @since 2021-04-03 오후 1:37
     */
    @Test
//    @Rollback(false) // insert와 같은 동작을 볼 수 있음
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("kim");

        //when
        Long savedId = memberService.join(member);

        //then
//        em.flush();
        assertEquals(member, memberRepository.findById(savedId));
    }

    /**
     * 중복회원예외
     * @author Boki
     * @since 2021-04-03 오후 1:38
     */
    @Test(expected = IllegalStateException.class)
    public void 중복회원예외() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        //when
        memberService.join(member1);
        memberService.join(member2); //예외가 발생해야 한다!!

        //then
        fail("예외가 발생해야 한다."); // 위에줄에서 Exception이 떠서 메서드 밖으로 나갔기때문에 fail이 뜨면 안된다.
    }
}