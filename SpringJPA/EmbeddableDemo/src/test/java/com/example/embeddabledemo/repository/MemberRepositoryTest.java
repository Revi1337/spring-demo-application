package com.example.embeddabledemo.repository;

import com.example.embeddabledemo.domain.Address;
import com.example.embeddabledemo.domain.AddressEntity;
import com.example.embeddabledemo.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Commit;

import java.util.List;
import java.util.Set;


@DataJpaTest(showSql = false)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("Embeddable 과 Embedded 테스트")
    public void embeddableTest() {
        Address address = new Address("test_city", "test_zipcode");
        Member member1 = new Member(address);
        Member member2 = new Member(address);
        memberRepository.saveAll(List.of(member1, member2));
    }

    @Commit
    @Test
    @DisplayName("""
        Embeddable 객체는 꼭 불변이어야 한다. 그렇지 않으면 해당 Embeddable 객체를 변경했을때
        해당 Embeddable 를 사용하는 모든 Entity 들이 변경되어 여러개의 Update 쿼리가 나가게 된다.
        
        [해결방법]
        Embeddable 객체를 불변으로 만드는 방법은 생성자만 허용하고 Setter 는 싹다 막으면 된다.
        Embeddable 객체를 변경할려면 새로운 객체를만들어 기존의 값을 가져와서 넣어주어야 한다.
    """)
    public void shareEntityReferenceTest() {
        Address address = new Address("test_city", "test_zipcode");
        Member member1 = new Member(address);
        Member member2 = new Member(address);
        memberRepository.saveAll(List.of(member1, member2));

        // Test Setter (이걸 하면 안된다는 거임)
//        member1.getHomeAddress().setCity("changed_city");

        // 해결방법
        member1.changeHomeAddress(new Address("test_city", "changed_city"));
    }

    @Test
    @DisplayName("""
        값 타입 컬렉션이기 때문에 별도로 insert 쿼리를날리지 않아도
        collection 의 add() 를 통해 테이블에 row 를 추가할 수 있음.
    """)
    public void elementCollectionTest() {
        Address address = new Address("test_city", "test_zipcode");
        Member member1 = new Member(address);
        Member member2 = new Member(address);
        memberRepository.saveAll(List.of(member1, member2));

        member1.getFavoriteFoods().add("치킨");
        member1.getFavoriteFoods().add("족발");
        member1.getFavoriteFoods().add("피자");
        memberRepository.flush();

        memberRepository.delete(member1);
        memberRepository.flush();
    }

    @Test
    @DisplayName("""
        값 타입 컬렉션은 기본으로 지연로딩으로 설정되어 있다.
        값 타입 컬렉션도 내부적으로 값 타입 으로 이루어진 컬렉션이기 때문에
        수정 시 기존의 String, Embedded 와 같은 값 타입을 지운 뒤, 새로 추가해야 한다.
        (값 타입 컬렉션은 지연로딩이 기본 전략이다.)
    """)
    public void elementCollectionLazyTest() {
        Address address = new Address("test_city", "test_zipcode");
        Member member1 = new Member(address);
        Member member2 = new Member(address);
        memberRepository.saveAll(List.of(member1, member2));

        member1.getFavoriteFoods().add("치킨");
        member1.getFavoriteFoods().add("족발");
        member1.getFavoriteFoods().add("피자");
        entityManager.flush();
        entityManager.clear();

        Member member = memberRepository.findById(member1.getId()).get();
        Set<String> favoriteFoods = member.getFavoriteFoods();
        for (String favoriteFood : favoriteFoods) {
            System.out.println("favoriteFood = " + favoriteFood);
        }
    }

    @Test
    @DisplayName("""
        String 값 타입 컬렉션이 아닌, Embedded 값 타입 컬렉션에 변경사항이 발생하면
        주인 엔티티와 연관된 모든 데이터를 삭제하고, 값 타입 컬렉션에 남아있는 현재값을
        모두 다시 저장한다. (이건 사용하면 안된다)
        
        해결방법으로는 OneToMany + cascade + orphanRemoval 로 푼다.
    """)
    public void elementCollectionUpdateTest() {
        Address address = new Address("test_city", "test_zipcode");
        Member member1 = new Member(address);
        Member member2 = new Member(address);
        memberRepository.saveAll(List.of(member1, member2));

        // Member.java 의 @ElementCollection 주석과 해당 부분의 주석을 세트로하여 주석을 스위치해가며 테스트해보자.
//        member1.getAddressHistory().add(new Address("city1", "zipcode1"));
//        member1.getAddressHistory().add(new Address("city2", "zipcode2"));
//        entityManager.flush();
//        entityManager.clear();
//
//        System.out.println("================");
//        Member member = memberRepository.findById(member1.getId()).get();
//        member.getAddressHistory().remove(new Address("city1", "zipcode1"));
//        member.getAddressHistory().add(new Address("new_city1", "new_zipcode1"));
//        entityManager.flush();

        member1.getAddressHistory().add(new AddressEntity("city1", "zipcode1"));
        member1.getAddressHistory().add(new AddressEntity("city2", "zipcode2"));
        entityManager.flush();
        entityManager.clear();

        System.out.println("================");
        Member member = memberRepository.findById(member1.getId()).get();
        member.getAddressHistory().remove(new AddressEntity("city1", "zipcode1"));
        member.getAddressHistory().add(new AddressEntity("new_city1", "new_zipcode1"));
        entityManager.flush();
    }
}