package hellojpa;

import javax.persistence.*;

@Entity
public class User {

    /*
         키본키 매핑
            @Id : 직접할당
            @GeneratedValue : 자동 생성 (@Id와 같이 사용해야함, pk가 아닌 컬럼에서는 사용 불가)
                IDENTITY: db에 위임
                    주로 MySQL, PostgreSQL, SQL Server, DB2에서 사용
                    ex) Mysql의 AUTO_INCREMENT
                    위 전략은 DB에서 pk를 만들어 줘야하는데, persist시 1차 캐시에 저장이 되려면 pk값이 있어야한다.
                    원래의 JPA는 flush할 때 쓰기지연 SQL 저장소에서 쿼리(persist시 생성된)를 불러와 실행을 시킨다(commit)
                    따라서 IDENTITY 전략은 em.persist()시점에 즉시 INSERT SQL을 실행하고 (commit 시점이 아닌), 생성된 pk를 다시 1차캐시(영속성 컨텍스트)에 불러온다.
                SEQUENCE: db 시퀀스 오브젝트 사용, ORACLE
                    @SequenceGenerator(
                        name = "MEMBER_SEQ_GENERATOR",
                        sequenceName = "MEMBER_SEQ" 매핑할 db 시퀀스 이름
                        initialValue = 1,   DDL 생성시에만 사용, 시퀀스 DDL을 생성할 때 처음 시작하는 수를 지정
                        allocationSize = 1) 시퀀스 한 번 호출에 증가하는 수(최적화에 사용됨) 시퀀스 값이 하나씩 증가하도록 설정되어 있으면 이값을 반드시 1로 설정해야한다 (기본값 50)
                            50일시 처음 시퀀스에서 호출 할때
                            1을 가져오면 db상의 다음 값은 51이 된다.
                            실제로는 1~50의 수를 가져오며, 해당 숫자들을 메모리에 올려놓는다.
                            그리고 메모리에 있는 숫자들을 차례대로 pk값으로 부여한다.
                            즉, 50번의 persist를 한다고 쳤을 때, allocationSize = 1 이면 50번을 db 시퀀스에서 그때마다 조회해와야 하지만,
                            allocationSize = 50 이면, 첫 조회시 가져온 1~50개를 메모리상에 올려놓고 사용함으로서 최적화를 시켜줄 수 있다.

                        를 이용해 사용할 시퀀스를 정할 수 있다.
                        위치는 클래스의 어노테이션으로 사용
                        해당 어노테이션 사용시 pk의 @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBER_SEQ_GENERATOR") 로 써주어야함
                        IDENTITY 전략과 같이 시퀀스도 DB에서 생성되기 때문에 persist()하는 즉시 db 시퀀스에서 값을 가져와서 1차캐시에 저장한다.
                        하지만 얘는 commit때 쿼리를 날린다. (버퍼링이 가능)
                    Integer를 쓰거나 Long을 쓴다.
                TABLE: 키 생성 전용 테이블을 하나 만들어서 db 시퀀스를 흉내냄.
                    @TableGenerator 필요
                        name : 식별자 생성기 이름
                        table : 키생성 테이블명 (기본값 hibernate_sequence)
                        pkColumnName : 시퀀스 컬럼명 (기본값 sequence_name)
                        valueColumnName : 시퀀스 값 컬럼명 (기본값 next_val)
                        pkColumnValue : 키로 사용할 값 이름 (기본값 엔티티 이름)
                        initialValue : 초기 값, 마지막으로 생성된 값이 기준이다. (기본값 0)
                        allocationSize : 시퀀스 한 번 호출에 증가하는 수 (기본값 50)
                        catalog, schema : db catalog, schema 이름
                        uniqueConstraint(DDL) : 유니크 제약 조건을 지정
                AUTO: 방언에 따라 자동 지정, 기본값

                권장하는 식별자 전략
                    미래까지 이 조건을 만족하는 자연키는 찾기 어렵다. (주민번호, 폰번호 등)
                    대체키를 사용하자 (db에서 생성해주는 pk)
                    => Long형 + 대체키 + 키 생성전략 사용
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String username;

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
