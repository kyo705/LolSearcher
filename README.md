# Lolsearcher
롤 전적 검색 사이트 프로잭트
=============
-----------------------------------------
롤 전적 검색 사이트란??
-----------------------------------------

롤(League Of Legend) 게임 회사 서버에서 *REST API*를 통해 제공되는 유저의 게임 데이터들(랭킹 점수, 게임 횟수, 상세 게임 정보 등)을 가공 및 활용하여 유저들에게 제공하는 사이트입니다.

프로잭트 내 적용 기술
-----------------------------------------
> - 백 앤드
>
>   - 언어 : Java
>   - 프레임 워크 : SpringBoot, Spring Core, Spring MVC
>   -  빌드 관리 툴 : Gradle
>   - RESAPI통신 : WebClient
>   - ORM : JPA(Hibernate)
>   -  DBMS : MariaDB
> - 프론트 앤드
>   - 템플릿 엔진 : Thymeleaf

프로잭트 서버 구조
-----------------------------------------
![ServerStructure](https://user-images.githubusercontent.com/89891704/157023264-e4c10e2a-0a37-4e50-b181-962c45dcca82.png)

프로잭트 작동 과정 및 원리
-----------------------------------------
1. **index.html** 에서 게임 유저 닉네임을 검색 => 닉네임 및 다양한 파라미터들을 POST 방식으로 서버에 전송

2. 웹 통신을 통해 전달되는 데이터들을 Filter Interface를 overriding한 **LolsearcherFilter** 클래스를 통해 character encoding 방식 *UTF-8*로 설정한 후 **SummonerController**로 데이터 전송

3. **SummonerController**에서 요청 파라미터들을 Command 객체로 받고 해당 Command 객체의 값의 특수문자들을 제거 <= XXS 공격을 막기 위해   
*cf) 스프링에서는 클라이언트로부터 요청된 파라미터들을 한번에 받을 수 있도록 Command 객체 제공해준다. 만약 클라이언트로부터 전달된 값이 없을 땐 Command 객체를 생성자를 통해 값을 초기화 할 수 있다.* 
```java
String unfilteredname = param.getName();
String regex = "[^\\uAC00-\\uD7A30-9a-zA-Z]"; //문자,숫자 빼고 다 필터링(띄어쓰기 포함)
String filteredname = unfilteredname.replaceAll(regex, "");
param.setName(filteredname);
```
4. 필터된 닉네임으로 **Summonerservice** 클래스의 **findSummoner** 메소드를 통해 해당 닉네임에 대한 유저가 존재하는지를 판단 => 유저가 없다면(WebClientResponseException = 404 error) **error_name.html**을 클라이언트에게 전송

5. 유저가 존재 한다면 DB에 해당 유저에 대한 정보들을 조회 => DB에 데이터들이 존재하지 않는다면 혹은 클라이언트의 전적 갱신 요청이 들어온 경우 **REST API** 통신을 통해 Entity 객체에 데이터를 받고 해당 Entity들을 통해 DB에 저장 or 갱신함   
*cf) Entity 객체를 통해 DB에 데이터를 저장,조회,삭제,갱신할 때 JPA를 이용한다. JPA의 핵심기능은 EntityManager을 이용해서 영속성 컨텍스트에 entity를 다루는 것이다. 
영속성 컨텍스트에는 1차 캐시, 같은 key값에 대한 동일성 보장, 쓰기 지연, 변경감지 등의 다양한 기능들을 제공해준다. 이러한 특징을 이용하여 DB의 값을 갱신할 땐 JPQL의 UPDATE 쿼리를 직접 만들지 않고 DB값(이전)을 영속성 컨텍스트의 1차 캐시에 저장 후  API 값(최신)을 이용하여 Entity값을 변경해주면 Commit시 UPDATE 쿼리를 자동으로 생성해줌* 
```java
public void updatesummoner(Summoner apisummoner, Summoner dbsummoner) {
		dbsummoner.setAccountId(apisummoner.getAccountId());
		dbsummoner.setName(apisummoner.getName());
		dbsummoner.setProfileIconId(apisummoner.getProfileIconId());
		dbsummoner.setPuuid(apisummoner.getPuuid());
		dbsummoner.setRevisionDate(apisummoner.getRevisionDate());
		dbsummoner.setSummonerLevel(apisummoner.getSummonerLevel());
	}
```

6. DB에서 유저의 다양한 데이터들을 조회하여 Model에 담아 매핑된 Templete(View)에 Model을 전송. 이 때, 데이터들을 조회할 때 조회 메소드 간에 연관성이 없기 때문에 멀티스레드를 만들어 더 빠르게 데이터를 조회할 수도 있음.

7. Templete Engine은 해당 템플릿을 가공하여 html을 작성한 후 데이터를 클라이언트 쪽으로 전송   

Entity 매핑 및 DB 테이블 연관 관계
----------------------------------------
![image](https://user-images.githubusercontent.com/89891704/157093397-cab00a54-01cd-405f-a870-3d33c7cad1ea.png)


업데이트 설명
--------------------------
**2022-04-10**   
기존 클라이언트의 갱신 요청 버튼을 통해 게임 서버의 REST 통신이 이루어졌는데 해당 요청을 무한정 요청할 수 있었기에 클라이언트가 5분마다 한 번씩 갱신 요청을 하도록 Summoner 객체에 'lastRenewTimeStamp' 필드 값을 추가하고 해당 필드 값을 통해 프론트 쪽에서 자바스크립트를 통해 5분 이하면 갱신 버튼을 비활성화 시키고 5분 이상이 되어야 갱신버튼을 활성화 시키는 방향으로 수정하였다.   

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
