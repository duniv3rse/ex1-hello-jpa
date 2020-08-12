package hellojpa;

import javax.persistence.*;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        /*
            1. Persistence가 persistence.xml을 읽는다
            2. Persistence가 EntityManagerFactory를 생성한다 *EntityManagerFactory는 일종의 싱글톤
            3. EntityManagerFactory가 EntityManager를 만든다. *EntityManager는 한번의 커넥션에서만 사용
         */
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        // 하나의 트랙잭션 별로 EntityManager를 만들어줘야함
        EntityManager em = emf.createEntityManager();
        // JPA는 모든 데이터 변경은 트랜잭션안에서 사용되어야 함
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        /*
            JPA : Entity객체를 중심으로 개발
            JPQL : Entity객체를 대상으로 쿼리
            SQL : 데이터베이스 테이블을 대상으로 쿼리
         */
        try {
//            Member member = em.find(Member.class, 1L);
            /*
                JPQL : 모든 DB데이터를 객체로 변환해서 검색하는 것은 불가능
                    검색쿼리를 짤때도 테이블이 아닌 Entity객체를 대상으로 검색
                    필요한 데이터만 DB에서 불러오려면 결국 검색 조건이 포함된 SQL이 필요.
                    한마디로 객체지향 SQL
             */
            List<Member> result = em.createQuery("select m from Member as m", Member.class)
                    .setFirstResult(1)  // first부터
                    .setMaxResults(10)  // max개만큼 가져와
                    .getResultList();

            for (Member member : result) {
                System.out.println("member.name = " + member.getName());

            }
            /*
                영속성 컨텍스트 : 어플리케이션 - @entityManager@ - DB 사이 : entityManager를 통해 영속성 컨텍스트에 접근(영속성 컨텍스트는 논리적 모델 실제 존재하진 않음)
                영속상태 = 1차캐시

                이점

                1차 캐시 -> pk, Entity, 스냅샷이 존재 (스냅샷 : 1차캐시에 데이터를 처음 읽어온 시점)
                동일성(identity) 보장 -> 같은 해쉬값 : 1차캐시로 반복 가능한 읽기(한 트랙잭션)
                트랜잭션을 지원하는 쓰기 지연(transactional  write-behind) -> entityManager가 crud코드를 실행할때 SQL Query를 생성시켜 쓰기지연 SQL 저장소에 쌓아둔 뒤 commit하는순간 쿼리를 실행(flush -> commit).
                변경감지(dirty checking)
                    -> 1차캐시에 해당 데이터가 존재하고 commit될때 entity와 스냅샷을 비교 후 변경되어있으면
                        쓰기지연 SQL 저장소에 쿼리를 저장(persist를 적지 않아도)하고 commit (자바 collection에 객체 넣는것을 생각해보자)
                        flush() > entity 스냅샷 비교 > update sql 생성(쓰기지연sql저장소) > flush > commit
                        flush : 영속성 컨텍스트의 변경내용을 데이터베이스에 반영(동기화) (flush하여도 1차캐시는 지워지지 않는다.)
                            트랜잭션이라는 작업단위가 중요(커밋 직전에만 동기화 하면됨)
                            persist시에만 발생하겠쥬?
                            flush발생 -> dirtychecking -> 수정된 entity 쓰기 지연 sql 저장소에 등록 -> 해당 쿼리를 db에 전송
                            flush()하는 방법
                            1. em.flush()
                            2. 트랜잭션 커밋
                            3. JPQL쿼리 실행
                                em.persist(memberA);
                                em.persist(memberB);

                                query = em.createQuery("select m from Member m", Member.class);
                                List<Member> members = query.getResultList();
                                ex) persist후 JPQL로 select쿼리를 날렸을 때는 날리기 전 flush를 한다.
                            em.setFlushMode(FlushModeType.AUTO) : 커밋이나 JPQL 날릴때 플러시 (디폴트)
                            FlushModeType.COMMIT    : 커밋할때만 플러시
                지연 로딩(lazy loading)
                DB 한 트랙잭션 안에서만 효과가 있기때문에 성능의 이점이 그렇게 크진 않음.

                한 어플리케이션안에서 공유되는 캐시는 2차캐시라고 부른다.


                준영속 상태

                영속 -> 준영속
                영속상태의 엔티티가 영속성 컨텍스트에서 분리(detached)
                영속성 컨텍스트가 제공하는 기능을 사용 못함

                em.detach(entity) : 특정 엔티티만 준영속 상태로 전환
                em.clear() : 영속성 컨텍스트를 완전히 초기화
                em.close() : 영속성 컨텍스트를 종료

             */
            // 객체를 생성한 상태 (비영속)
            Member member = new Member(); // entity
            // 1차캐시
            member.setId(100L);
            member.setName("HeeloJPA2");

            // 영속상태 - entitymanager를 통해 영속성 컨텍스트(persistence context)에서 관리를 시킴 그말인즉 쿼리를 여기서 날리지 않는다.
            em.persist(member);
            // 조회시 1차캐시에서 조회
            em.find(Member.class, 100L);
            // 1차캐시에서 조회 -> 없으면 DB에서 조회 > 1차캐시에 저장 -> entity 반환
            em.find(Member.class, 200L);

            // 영속성 컨텍스트에서 분리 (준영속 상태)
            em.detach(member);
            // DB에서 해당 데이터를 지운다
            em.remove(member);

            // 쿼리가 실제 날아가는 때
            tx.commit();



        } catch (Exception e) {
            tx.rollback();

        } finally {
            em.close();

        }
        emf.close();

    }
}
