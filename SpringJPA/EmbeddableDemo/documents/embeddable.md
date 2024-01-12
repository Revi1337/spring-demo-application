## @Embeddable 과 @Embedded

1. @Embeddable 를 달아준 클래스에 기본 생성자가 필요하다. (public 혹은 protected)
2. @Embeddable 를 사용하는 Entity 클래스에서는 @Embedded 를 선언해주면된다.
3. 임베디드 타입 값이 null 이면 당연히 Embedded 에 속한 Column 들은 모두 null 이 된다.

> @Embeddable 과 @Embedded 둘 중 하나만 달아주면 된다. 하지만 명시적으로 붙여주는게 좋다.

## @Embeddable 과 @Embedded 장점

1. 강한 응집력
2. 재사용 가능
3. 객체와 테이블을 굉장히 세밀하게 매핑하는것이 가능하다.

## @Embeddable 과 @Embedded 주의할 점

### 1. 항상 불변객체로 만들어야 한다.

`Embedded` 값 타입은 수정할 수 없게 `꼭 불변객체(Immutable)` 로 만들어야 한다.
왜냐하면 Embedded 값 타입은 여러 객체에서 공유할 수 있기 때문이다. 이로 인해 `공유 참조` 가 발생하여 Side Effect 가 발생하게 된다.
예를 들면 Embedded 객체의 값을 바꾸게 되면 바뀐 Embedded 타입을 사용하는 Entity 의 값이 모두 바뀌게 된다.

> 해결방법으로는 생성자로만 값을 설정하고 수정자(Setter) 를 만들지 않으면 된다.
값을 변경할때는 새로운인스턴스를 생성해서 기존 값을가져와 대입하여 사용해야 한다.

### 2. 동등성(equals()) 으로 임베디드 값을 비교해야 한다.

`Embedded` 값 타입을 비교할때는 동일성(==) 말고 `동등성(equals())` 로 비교해야 한다.
이는 즉, equals() 를 재정의해하여 equals() 의 기본동작인 == 로 비교하지 않고, 필드를 비교하게끔 만들어야 한다.

> 뒤에 값 타입 컬렉션 @ElementCollection 에서 remove() 를 위해 꼭 필요하다.

## @Embeddable 과 @Embedded 에서 일어날 수 있는 Exception

`JpaSystemException: Unable to locate constructor for embeddable`

- @Embeddable 클래스에 기본 생성자를 달아주지 않은 상태에서 Entity 를 만들고 영속화시켰을 때 발생한다.
- 해결방법으로는 간단. @Embeddable 클래스에 기본 생성자를 달아주면 된다.

`org.hibernate.MappingException: Column 'zip_code' is duplicated in mapping for entity`

- 하나의 Entity 에서 같은 Embedded 타입 객체를 사용할때 발생
- 결정적으로 Embedded 에서 사용되는 column 명이 겹쳐서 발생한다.
- 해결방법으로는 간단. `@AttributeOverrides` 를 사용하면 된다.

## @AttributeOverrides 의 사용

하나의 Entity 클래스에서 같은 Embedded 객체의 사용이 필요할 때 사용된다.
이 때,`@AttributeOverrides` 와 `@AttributeOverride` 로 column 명을 재지정해주어야 한다.

## 값 타입 컬렉션 사용 (@ElementCollection)

`String`, `Embedded` 와 같은 값 타입을 컬렉션으로 사용하기 위해서는
`@ElementCollection` 을 통해 별도로 테이블을 생성해야 한다.

- 값 타입`컬렉션` 이기 때문에 별도로 `insert` 쿼리를날리지 않아도 `add()` 를 통해
테이블에 row 를 추가할 수 있음.
- 또한 값 타입 컬렉션 은 모든 생명주기를 소속된 Entity 와 함께한다.
- 값 타입 컬렉션도 `지연 로딩` 을 사용한다.

### 값 타입 컬렉션 제약조건

- 값 타입 컬렉션은 Entity 와 다르게 식별자 개념이 없다. 따라서 값이 변경되면 추적이 굉장히 어렵다.
- `String 값 타입 컬렉션`이 아닌, `Embedded 값 타입 컬렉션`에 변경사항이 발생하면, 주인 엔티티와 연관된 모든 데이터를 삭제하고, 값 타입 컬렉션에 남아있는 현재값을 모두 다시 저장한다.
- 값 타입 컬렉션을 매핑하는 테이블을 모든 컬럼을 묶어서 기본키를 구성해야 한다. (null 입력 x, 중복 저장 x)

> @OrderColumn 으로 해결할수는 있지만 무척이나 복잡해지기때문에 사용하지 않는것이 좋다. 아니 사용하면 안된다.
실무에서는 Embedded 값 타입 컬렉션 대신에 일대다 관계로 승격하고, cascade + orphanRemoval 을 사용하여 값 타입 컬렉션처럼 사용한다.
