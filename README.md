# Lolsearcher
롤 전적 검색 사이트 프로젝트
=============
-----------------------------------------
롤 전적 검색 사이트란??
-----------------------------------------

롤(League Of Legend) 게임 회사 서버에서 *REST API*를 통해 제공되는 유저의 게임 데이터들(랭킹 점수, 게임 횟수, 상세 게임 정보 등)을 가공 및 활용하여 유저들에게 제공하는 사이트입니다.

프로젝트 내 적용 기술
-----------------------------------------
> - 백 앤드
>
>   - 언어 : Java
>   - 프레임 워크 : SpringBoot, Spring Core, Spring MVC
>   -  빌드 관리 툴 : Gradle
>   - REST API통신 : WebClient
>   - ORM : JPA(Hibernate)
>   -  DBMS :
>      - 실제 서버 환경 : MariaDB
>      - 테스트 환경 : h2
> - 프론트 앤드
>   - 템플릿 엔진 : Thymeleaf

프로젝트 서버 구조
-----------------------------------------
![ServerStructure](https://user-images.githubusercontent.com/89891704/157023264-e4c10e2a-0a37-4e50-b181-962c45dcca82.png)

프로젝트 작동 과정 및 원리
-----------------------------------------
1. **index.html** 에서 게임 유저 닉네임을 검색 => 닉네임 및 다양한 파라미터들을 POST 방식으로 서버에 전송

2. 웹 통신을 통해 전달되는 데이터들을 Filter Interface를 구현한 **LolsearcherFilter** 클래스를 통해 character encoding 방식 *UTF-8*로 설정한 후 **SummonerController**로 데이터 전송

3. **SummonerController**에서 요청 파라미터들을 Command 객체로 받고 해당 Command 객체의 값의 특수문자들을 제거 <= XXS 공격을 막기 위해   

```java
String unfilteredname = param.getName();
String regex = "[^\\uAC00-\\uD7A30-9a-zA-Z]"; //문자,숫자 빼고 다 필터링(띄어쓰기 포함)
String filteredname = unfilteredname.replaceAll(regex, "");
param.setName(filteredname);
```

*cf) 스프링에서는 클라이언트로부터 요청된 파라미터들을 한번에 받을 수 있도록 Command 객체 제공해준다. 스프링은 커맨드 객체에 클라이언트로부터 전달되는 파라미터와 이름이 같은 setter 값이 있어야지만 파라미터 값을 커맨드 객체에 넘겨줄 수 있다. 만약 클라이언트로부터 전달된 값이 없을 땐 Command 객체를 생성자를 통해 값을 초기화 할 수 있다.*    
https://github.com/kyo705/LolSearcher/blob/ef036088e2a18935768485ae479e0e81fc39429b/lolsearcher/src/main/java/com/lolsearcher/controller/SummonerController.java#L42

https://github.com/kyo705/LolSearcher/blob/ef036088e2a18935768485ae479e0e81fc39429b/lolsearcher/src/main/java/com/lolsearcher/domain/Dto/command/SummonerParamDto.java#L3




4. 필터된 닉네임을 [**summonerService.findDbSummoner(String name)**](https://github.com/kyo705/LolSearcher/blob/b8b16c687c2e4f60048893b13fafb6b271f198ab/lolsearcher/src/main/java/com/lolsearcher/controller/SummonerController.java#L62) 메소드의 파라미터 값으로 전달해 해당 닉네임에 대한 유저가 DB에 존재하는지 판단 => 유저가 1명인 경우 정상적으로 해당 유저 객체(Summoner.class) 반환, 유저가 없는 경우 NULL을 반환, 유저가 2명 이상인 경우 해당 유저들에 대한 정보를 [**갱신**](https://github.com/kyo705/LolSearcher/blob/b8b16c687c2e4f60048893b13fafb6b271f198ab/lolsearcher/src/main/java/com/lolsearcher/service/SummonerService.java#L73)함   

*cf) DB에 2명 이상의 중복된 닉네임이 존재하게 될 수 있는 상황 설명 : 게임 내에서 닉네임은 중복될 수 없다. 하지만 닉네임은 변경이 가능하기 때문에 DB에 저장된 유저1이 게임 내에서 닉네임1을 닉네임2로 변경하고 변경된 내용이 DB에 갱신이 안된 상황에서 유저2가 닉네임1을 소유한 뒤 DB에 유저2의 정보를 저장하면 DB에 똑같은 닉네임을 가진 유저가 2명이상 될 수 있다. 그래서 해당 중복된 닉네임을 가진 데이터들을 업데이트하는 로직을 수행하면 실제 닉네임 소유 유저는 1명 또는 0명이 되게 된다.*   

***자세한 내용은 [테스트 케이스](https://github.com/kyo705/LolSearcher/blob/f117f2ff76a8a488b051130264b16fdcbf3e82e3/lolsearcher/src/test/java/com/lolsearcher/Service/SummonerServiceUnitTest.java#L56)들을 보면 쉽게 알 수 있다***

5. [DB에 해당 닉네임 유저가 존재하고 업데이트 요청이 들어오지 않은 경우](https://github.com/kyo705/LolSearcher/blob/b8b16c687c2e4f60048893b13fafb6b271f198ab/lolsearcher/src/main/java/com/lolsearcher/controller/SummonerController.java#L121) => DB에 해당 유저에 대한 정보들(랭킹, 최근 전적 등)을 조회   
 [DB에 해당 닉네임 유저가 존재하지 않는 경우 OR 해당 닉네임 유저의 업데이트 요청이 들어오는 경우](https://github.com/kyo705/LolSearcher/blob/b8b16c687c2e4f60048893b13fafb6b271f198ab/lolsearcher/src/main/java/com/lolsearcher/controller/SummonerController.java#L72)  => 게임 회사의 데이터 제공 사이트와의 **REST API** 통신을 통해 Entity 객체에 데이터를 받아 DB에 저장 or 갱신함   

*cf) Entity 객체를 통해 DB에 데이터를 저장,조회,삭제,갱신할 때 JPA를 이용한다. JPA의 핵심기능은 EntityManager을 이용해서 영속성 컨텍스트에 entity를 다루는 것이다. 
영속성 컨텍스트에는 1차 캐시, 같은 key값에 대한 동일성 보장, 쓰기 지연, 변경감지 등의 다양한 기능들을 제공해준다. 이러한 특징 덕분에 DB와의 I/O 비용도 줄일 수 있고, DB에서의 트랜잭션의 ISOLATION 단계를 기본값(READ_COMMITTED)으로 설정해도 REPEATABLE READ가 가능해 병렬 처리에 대한 성능도 향상시킬 수 있게 된다.* 

6. DB에서 유저의 다양한 데이터들을 조회하여 Model에 담아 매핑된 Templete(View)에 Model을 전송

7. Templete Engine은 해당 템플릿을 가공하여 html을 작성한 후 데이터를 클라이언트 쪽으로 전송   

Entity 매핑 및 DB 테이블 연관 관계
----------------------------------------
![image](https://user-images.githubusercontent.com/89891704/157093397-cab00a54-01cd-405f-a870-3d33c7cad1ea.png)


업데이트 설명
--------------------------
**2022-04-10**   
기존 클라이언트의 갱신 요청 버튼을 통해 게임 서버의 REST 통신이 이루어졌는데 해당 요청을 무한정 요청할 수 있었기에 클라이언트가 5분마다 한 번씩 갱신 요청을 하도록 Summoner 객체에 'lastRenewTimeStamp' 필드 값을 추가하고 해당 필드 값을 통해 프론트 쪽에서 자바스크립트를 통해 5분 이하면 갱신 버튼을 비활성화 시키고 5분 이상이 되어야 갱신버튼을 활성화 시키는 방향으로 수정하였다.   

![image](https://user-images.githubusercontent.com/89891704/163554119-5dbedd3b-02f2-4eef-b2dd-620fd7ddf0d5.png)   
**전적 갱신 버튼 누르기 전/후**


**2022-04-14**   
SummonerService 클래스의 트랜잭션 처리를 스프링이 제공해주는 @transactional로 처리하였다. 이 때, 고립단계를 기본값인 1단계(READ_COMMITTED)를 사용하였다. 그래서 멀티스레드 환경에서 데이터를 입력할 때 중복 삽입이 될 수 있다는 생각에 테스트 코드(SummonerServiceTest)를 작성하여 멀티스레드로 동시에 SummonerService 메소드인 setSummoner() 메소드로 데이터 삽입을 해본 결과, 중복 삽입 예외가 발생되는 것을 확인하였다.
```
Hibernate: select summoner0_.id as id1_3_0_, summoner0_.account_id as account_2_3_0_, summoner0_.last_renew_time_stamp as last_ren3_3_0_, summoner0_.lastmatchid as lastmatc4_3_0_, summoner0_.name as name5_3_0_, summoner0_.profile_icon_id as profile_6_3_0_, summoner0_.puuid as puuid7_3_0_, summoner0_.revision_date as revision8_3_0_, summoner0_.summoner_level as summoner9_3_0_ from summoner summoner0_ where summoner0_.id=?
Hibernate: select summoner0_.id as id1_3_0_, summoner0_.account_id as account_2_3_0_, summoner0_.last_renew_time_stamp as last_ren3_3_0_, summoner0_.lastmatchid as lastmatc4_3_0_, summoner0_.name as name5_3_0_, summoner0_.profile_icon_id as profile_6_3_0_, summoner0_.puuid as puuid7_3_0_, summoner0_.revision_date as revision8_3_0_, summoner0_.summoner_level as summoner9_3_0_ from summoner summoner0_ where summoner0_.id=?
Hibernate: insert into summoner (account_id, last_renew_time_stamp, lastmatchid, name, profile_icon_id, puuid, revision_date, summoner_level, id) values (?, ?, ?, ?, ?, ?, ?, ?, ?)
Hibernate: insert into summoner (account_id, last_renew_time_stamp, lastmatchid, name, profile_icon_id, puuid, revision_date, summoner_level, id) values (?, ?, ?, ?, ?, ?, ?, ?, ?)
2022-04-14 16:10:50.028  WARN 19428 --- [      Thread-14] o.h.engine.jdbc.spi.SqlExceptionHelper   : SQL Error: 1062, SQLState: 23000
2022-04-14 16:10:50.028 ERROR 19428 --- [      Thread-14] o.h.engine.jdbc.spi.SqlExceptionHelper   : (conn=932) Duplicate entry 'fwo2HwBhQTd5NU0Z7t3cVnRP5tfrUMAI-6DkPKDwDsXL80M' for key 'PRIMARY'
2022-04-14 16:10:50.029  INFO 19428 --- [      Thread-14] o.h.e.j.b.internal.AbstractBatchImpl     : HHH000010: On release of batch it still contained JDBC statements
```
그래서 중복 삽입 예외처리를 Controller에서 처리하였다.
```java
try {
	summonerdto = summonerservice.setSummoner(param.getName());    //riot 서버로부터 정보 받아옴
}catch(DataIntegrityViolationException e) { 			       //멀티스레드에 의해 중복 삽입 발생 시 예외처리
	summonerdto = summonerservice.findSummoner(param.getName()); 
}
```
