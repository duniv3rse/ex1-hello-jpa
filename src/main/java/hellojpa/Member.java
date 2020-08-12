package hellojpa;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "Member") // JPA에서 사용 할 엔티티 이름을 지정. 기본값은 클래스이름
// @Table(name = "MBR")  // 엔티티와 매핑할 테이블 지정 -> SQL생성시 MBR테이블에 매핑한다.
public class Member {

    /*
        객체와 테이블 매핑 : @Entity, @Table
        필드와 컬럼 매핑 : @Column
        기본 키 매핑 : @Id
        연관관계 매핑 : @ManyToOne, @JoinColumn

        @Entity
            JPA가 관리
            JPA를 사용해서 테이블과 매핑할 클래스는 @Entity 필수
            기본 생성자 필수(public 또는 protected)
            final 클래스, enum, interface, inner 클래스 사용 금지
            저장할 필드에 final 사용 금지

        데이터베이스 스키마 자동 생성
            DDL을 애플리케이션 실행 시점에 자동 생성 (CREATE TABLE...)
                엔티티가 있는 테이블들중에 기존에 DB에 테이블이 있으면 먼저 DROP후 새로 생성한다.
            테이블 중심 -> 객체 중심
            데이터베이스 방언을 활용해서 데이터베이스에 맞는 적절한 DDL 생성
            이렇게 생성된 DDL은 개발 장비에서만 사용
            생선된 DDL은 운영서버에서는 사용하지 않거나, 적절히 다듬은 후 사용
            hibernate.hbm2ddl.auto value="create"

            주의
            운영 장비에는 절대 create, create-drop, update 사용하면 안된다.
            개발 초기 create 또는 update
            테스트 서버 update 또는 validate
            스테이징과 운영서버 validate 또는 none

            DDL 생성기능
            컬럼에 제약조건을 추가
            DDL을 자동으로 생성할 때만 사용되고 JPA의 실행 로직에는 영향을 주지 않는다.
            @Column(unique = true, length = 10, nullable = false)

        매핑 어노테이션
            @Column : 컬럼매핑
                속성
                name : 필드와 매핑할 테이블의 컬럼 이름
                insertable, updatable : 등록 변경 가능 여부 (해당 컬럼이 변경되었을때 반영여부 디폴트 true)
                nullable(DDL) : null 값의 허용 여부를 결정. 디폴트시 DDL 생성 시에 not null 제약이 붙음
                unique(DDL) : @Table의 uniqueConstraints와 같지만 한 컬럼에 간단히 유니크 제약조건을 걸 때 사용함.
                length(DDL) : 문자길이제약 String타입에만 사용됨 기본값 255
                columnDefinition(DDL) : DB 컬럼 정보를 직접 작성 ex) varchar(100) default 'EMPTY'
                precision(DDL) : BigDecimal, BigInteger 타입에서 사용.
                    precision : 소수점을 포함한 전체 자릿수   (디폴트 19)
                    scale : 소수의 자리수 (디폴트 2)
                    double, float 타입에서는 적용되지 않는다. 아주 큰 숫자나 정밀한 소수를 다루어야 할때만 사용
            @Enumerated : enum 타입 매핑
                ORDINAL 사용 X 하지만 기본값이 ORDINAL이다. (추후 enum들의 순서가 변경되었을때를 방지)
                EnumType.ORDINAL : enum 에 정의된 순서(int)를 db에 저장
                EnumType.STRING : enum 이름을 db에 저장
            @Temporal : 날짜 타입 매핑
                날짜 타입(java.util.Date, java.util.Calendar)를 매핑할 때 사용
                LocalDate, LocalDateTime을 사용할 때는 생략 가능(하이버네이트 지원)
                TemporalType.DATE : 날짜, db의 date타입
                TemporalType.TIME : 시간, db의 time타입
                TemporalType.TIMESTAMP : 날짜와시간, db의 timestamp
            @Lob : BLOB, CLOB 매핑
                CLOB : String, char[], java.sql.CLOB
                BLOB : byte[], java.sql.BLOB
            @Transient : 특정 필드를 매핑하고 싶지 않을때
     */

    @Id
    private Long id;

    @Column(name = "name")
    private String username;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Temporal(TemporalType.TIMESTAMP)   // 자바는 Date지만 DB에서는 TIME, TIMESTAMP, DATE 3가지 타입이 있기에 지정해주어야함
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    @Lob
    private String description;

    @Transient
    private int temp;

    public Member() {
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }
}
