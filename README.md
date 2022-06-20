# Lolsearcher
롤 전적 검색 사이트 프로젝트
=============
-----------------------------------------
롤 전적 검색 사이트란??
-----------------------------------------

롤(League Of Legend) 게임 서버에서 REST API로 제공되는 유저의 게임 데이터들(랭킹 점수, 게임 횟수, 상세 게임 정보 등)을 수집하고 활용하여 클라이언트에게 제공하는 사이트입니다.

프로젝트 내 적용 기술
-----------------------------------------
> - 백 앤드
>   - 언어 : Java
>   - 프레임 워크 : SpringBoot, Spring MVC
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

프로젝트 코드 내 용어 설명
-----------------------------------------
- **Summoner** : 소환사(게임 내 개개인 유저를 나타내는 용어)   
- **Rank** : 랭크(게임을 얼마나 잘하는가를 나타내는 척도) *랭크 게임은 크게 솔로랭크, 자유랭크로 나뉜다.*   
- **Match** : 실제 게임 단위를 나타냄 (롤 게임은 5:5 매칭 게임)   
- **Member** : 하나의 게임(Match) 내 존재하는 유저 개개인   
- **Champion** : 챔피언(게임 내 캐릭터를 나타내는 용어)   
  - **MostChamp** : 특정 유저의 가장 많이하는 챔피언
- **Ingame** : 현재 진행 중인 게임



프로젝트 제공 서비스
-----------------------------------------
**1. 유저의 랭크 점수, 최근 게임 전적 정보 제공**

![image](https://user-images.githubusercontent.com/89891704/173005260-b4223420-9d2b-4ed1-a8ca-277cc1c72d95.png)



**2. 유저의 인게임 정보 제공(현재 게임 중일 경우 해당 매치 데이터 제공)**

**3. 게임 캐릭터(챔피언) 관련 통계 정보 제공**

**4. REST API 서비스 제공**
![image](https://user-images.githubusercontent.com/89891704/173006203-397f4b9d-9e86-48b9-b0ae-3e70ce6deb15.png)
**REST DOCS 파일 제공**
![image](https://user-images.githubusercontent.com/89891704/173006734-d3de6ed3-f822-41ba-baf7-03ba1517f2c6.png)


제공되는 서비스 별 작동 과정 설명서
-----------------------------------------
**1.특정 유저의 랭크 점수 및 최근 전적 제공 서비스 작동 과정 => [소스 코드로 보기](https://github.com/kyo705/LolSearcher/blob/f58461b145226443b2b49292407906473116246c/lolsearcher/src/main/java/com/lolsearcher/controller/SummonerController.java#L42)**   
> 
> ① 닉네임 검색창을 통해 특정 유저의 닉네임을 검색하게 되면 해당 닉네임 및 디폴트 설정이 된 파라미터들이 POST 방식으로 서버로 전송된다.
> 
> ② 서버로 전달된 파라미터들은 WAS(톰캣)에 의해 Request객체로 생성된 후 **LolsearcherFilter** 클래스를 거치며 character encoding 방식을 *UTF-8*로 설정한 후 **SummonerController**로 전달된다.
> 
> ③ **SummonerController**에 전달된 객체를 Command 객체로 받고, XXS 공격을 막기 위해 해당 값의 특수문자들을 제거한다.   
> 
```java
//특수 문자 제거 로직
String unfilteredname = param.getName();
String regex = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]"; //문자,숫자 빼고 다 필터링(띄어쓰기 포함)
String filteredname = unfilteredname.replaceAll(regex, "");
param.setName(filteredname);
```
>
> *cf) 스프링에서는 클라이언트로부터 요청된 파라미터들을 한번에 받을 수 있도록 Command 객체 제공해준다. 스프링은 커맨드 객체에 클라이언트로부터 전달되는 파라미터와 이름이 같은 setter 값이 있어야지만 파라미터 값을 커맨드 객체에 넘겨줄 수 있다. 만약 클라이언트로부터 전달된 값이 없을 땐 Command 객체의 생성자를 통해 초기값 설정이 가능하다.*    
>
> ④ 필터된 닉네임에 해당되는 유저가 DB에 존재하는지 확인한다. => DB에 해당 닉네임을 가지는 유저가 1명인 경우 정상적으로 해당 유저 객체(Summoner.class) 반환, 유저가 없는 경우 NULL을 반환, 유저가 2명 이상인 경우 해당 유저들의 데이터를 갱신한 후, 필터된 닉네임과 일치하는 유저를 반환한다.    ***자세한 케이스들은 [테스트 케이스](https://github.com/kyo705/LolSearcher/blob/f117f2ff76a8a488b051130264b16fdcbf3e82e3/lolsearcher/src/test/java/com/lolsearcher/Service/SummonerServiceUnitTest.java#L56)에 나와있다.***
> 
> *cf) DB에 2명 이상의 유저가 중복된 닉네임을 가지게 된 상황 설명 : 게임 내에서 닉네임은 중복될 수 없다. 하지만 게임에서 닉네임은 변경이 가능하기 때문에 웹 서버의 DB에 저장된 유저1이 게임 내에서 닉네임1을 닉네임2로 변경하고 변경된 내용이 DB에 갱신이 안된 상황에서 유저2가 닉네임1을 소유한 뒤 DB에 유저2의 정보를 저장하면 DB에 똑같은 닉네임을 가진 유저가 2명이상 될 수 있다. 그래서 해당 중복된 닉네임을 가진 데이터들을 업데이트하는 로직을 수행하면 실제 닉네임 소유 유저는 1명 또는 0명이 되게 된다.*   
>
> ⑤ DB에 해당 닉네임 유저가 존재하지 않는 경우 OR 해당 닉네임 유저의 업데이트 요청이 들어오는 경우  => 게임 데이터 제공 사이트와의 **REST API** 통신으로 유저 데이터들을 받아 DB에 저장하거나 갱신한다.   
  DB에 해당 닉네임 유저가 존재하고 업데이트 요청이 들어오지 않은 경우 => 다음 단계로 이동한다.   
>
> ⑥ DB로부터 유저의 다양한 데이터들을 조회하여 Model에 담아 매핑된 Templete(View)에 Model을 전송한다.
>
> ⑦ Templete Engine은 Model 데이터를 통해 템플릿을 가공하여 response 객체를 생성하고 클라이언트로 전송한다.   

**2.특정 유저의 인게임 데이터 제공 서비스 작동 과정 => [소스 코드로 보기](https://github.com/kyo705/LolSearcher/blob/f58461b145226443b2b49292407906473116246c/lolsearcher/src/main/java/com/lolsearcher/controller/SummonerController.java#L151)**

> ① 특정 유저 페이지의 '인게임 정보' 버튼을 누르면 해당 유저의 닉네임을 파라미터로 서버에 전달된다.
>
> ② 서버로 전달된 파라미터들은 WAS(톰캣)에 의해 Request객체로 생성된 후 **LolsearcherFilter** 클래스를 거치며 character encoding 방식을 *UTF-8*로 설정한 후 **SummonerController**의 **inGame(String name)** 메소드로 전달된다.
>
> ③ 파라미터로 전달된 닉네임은 GET 요청으로 URL로 전달할 수 있기 때문에, XXS 공격을 막기 위해 전달받은 파라미터의 특수문자를 제거한다.
>
> ④ 필터된 닉네임에 해당하는 유저가 DB에 존재하는지 확인한다. 이 때, DB에 존재하지 않는다면 해당 요청은 UI를 통한 요청이 아닌 URL로의 요청(잘못된 요청)이기 때문에 해당 값을 400 ERROR를 클라이언트에게 전달한다.
>
> ⑤ DB에서 유저를 조회하는데 성공한 경우 아래와 같은 로직이 실행된다.   
최근 요청 조회 시간이 2분 이하인 경우 => DB에서 인게임 데이터를 조회하고 값이 없으면 null을 반환, 1개 이상일 경우 최근 인게임 데이터를 반환한다.   
최근 요청 조회 시간이 2분 이상인 경우 => 게임 데이터 제공 서버와 REST API 통신으로 데이터를 조회하고 DB에 저장한다. 현재 진행 중인 게임이 없다면 error 페이지를 클라이언트에게 전달한다.
>
> ⑥ 최신 인게임 데이터를 제외한 다른 데이터들은 삭제한다.
>
> ⑦ Model에 인게임 데이터를 담아 매핑된 Templete(View)에 Model을 전송한다.
>
> ⑧ Templete Engine은 Model 데이터를 통해 템플릿을 가공하여 response 객체를 생성하고 클라이언트로 전송한다.  

**3. 게임 캐릭터(챔피언) 관련 통계 데이터 제공 서비스 작동 과정1 => [소스 코드로 보기](https://github.com/kyo705/LolSearcher/blob/f58461b145226443b2b49292407906473116246c/lolsearcher/src/main/java/com/lolsearcher/controller/ChampionController.java#L27)**

> ① 웹 페이지 상단의 '챔피언 분석' 버튼을 누르면 디폴트 포지션("TOP") 파라미터가 POST 방식으로 서버에 전달된다.
> 
> ② 서버로 전달된 파라미터들은 WAS(톰캣)에 의해 Request객체로 생성된 후 **LolsearcherFilter** 클래스를 거치며 character encoding 방식을 *UTF-8*로 설정한 후 **ChampionController**의 **champions(String position)** 메소드로 전달된다.
>
> ③ 파라미터로 전달된 포지션에 해당하는 챔피언 데이터들을 내부 로직의 우선순위(승률, 픽률 등)에 따라 DB에서 조회한다.
>
> ④ 조회된 데이터는 Model 객체에 셋팅하고 매핑된 Templete(View)에 Model을 전송한다.
>
> ⑤ Templete Engine은 Model 데이터를 통해 템플릿을 가공하여 response 객체를 생성하고 클라이언트로 전송한다.  

**4. 게임 캐릭터(챔피언) 관련 통계 데이터 제공 서비스 작동 과정2 => [소스 코드로 보기](https://github.com/kyo705/LolSearcher/blob/f58461b145226443b2b49292407906473116246c/lolsearcher/src/main/java/com/lolsearcher/controller/ChampionController.java#L39)**

> ① 챔피언 분석 웹 페이지에서 특정 챔피언을 클릭하면 해당 챔피언 이름의 파라미터가 POST 방식으로 서버에 전달된다.
> 
> ② 서버로 전달된 파라미터들은 WAS(톰캣)에 의해 Request객체로 생성된 후 **LolsearcherFilter** 클래스를 거치며 character encoding 방식을 *UTF-8*로 설정한 후 **ChampionController**의 **championDetail(String champion)** 메소드로 전달된다.
>
> ③ 해당 챔피언의 구체적인 승률, 아이템 승률, 상대하기 쉬운 챔피언 등의 데이터들을 DB에서 조회한다.
>
> ④ 조회된 데이터는 Model 객체에 셋팅하고 매핑된 Templete(View)에 Model을 전송한다.
>
> ⑤ Templete Engine은 Model 데이터를 통해 템플릿을 가공하여 response 객체를 생성하고 클라이언트로 전송한다.  

DB 테이블 연관 관계
----------------------------------------
![image](https://user-images.githubusercontent.com/89891704/173000894-97aa1f85-40b8-4ae3-a4c4-fac2d137cc17.png)


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
