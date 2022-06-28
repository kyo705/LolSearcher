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

테스트 코드 설명서
--------------------------
테스트 환경 프레임 워크 : springboot, mockito, junit5   

 해당 프로젝트의 테스트 코드는 비지니스 로직을 중점으로 크게 두가지(단위 테스트, 통합 테스트)로 작성되었다.   
   
- 단위 테스트 : Service, Repository, RESTClient 계층 별 테스트 실시   
- 통합 테스트 : 해당 단위 테스트 계층들을 하나로 통합하여 테스트 실시

**※통합 테스트 시 주의점** : REST API 통신 계층도 통합하여 테스트하기 때문에 모든 테스트케이스를 한꺼번에 실행하면 REST API 요청 제한 횟수(2분에 100회)를 초과할 수 있음. 다시 말해, 멱등성이 유지되지 않음.

코드 소스 경로 : lolsearcher/src/test/

<details>
    <summary>소스 코드 보는 방법</summary>
    
<div markdown="1">

![image](https://user-images.githubusercontent.com/89891704/174654609-f4759700-d18e-460c-b493-0a9fcc853f0f.png)

위 그림처럼 'Go to file'버튼을 클릭하면 아래와 같은 파일 검색창이 뜬다. 

![image](https://user-images.githubusercontent.com/89891704/174656060-1935b50e-4ae4-49f8-a039-f2289231e089.png)


해당 검색창에 위의 코드 소스 경로를 검색하면 아래와 같이 test 코드들을 확인할 수 있다.

![image](https://user-images.githubusercontent.com/89891704/174655978-03cc93f8-a9ef-4bda-923e-a91e47f57d97.png)
	
</div>
</details>



프로젝트 제공 서비스
-----------------------------------------
**1.특정 유저의 랭크 점수 및 최근 전적 제공** 

<details>
<summary>이미지 보기</summary>
<div markdown="1">

![image](https://user-images.githubusercontent.com/89891704/173005260-b4223420-9d2b-4ed1-a8ca-277cc1c72d95.png)

</div>
</details>

<details>
<summary>작동 과정</summary>
<div markdown="1">

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

</div>
</details>

[**소스 코드로 보기**](https://github.com/kyo705/LolSearcher/blob/f58461b145226443b2b49292407906473116246c/lolsearcher/src/main/java/com/lolsearcher/controller/SummonerController.java#L42)   

**2.특정 유저의 인게임 데이터 제공(현재 게임 중일 경우 해당 매치 데이터 제공)**

<details>
<summary>이미지 보기</summary>
<div markdown="1">


</div>
</details>

<details>
<summary>작동 과정</summary>
<div markdown="1">

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

</div>
</details>

[**소스 코드로 보기**](https://github.com/kyo705/LolSearcher/blob/f58461b145226443b2b49292407906473116246c/lolsearcher/src/main/java/com/lolsearcher/controller/SummonerController.java#L151)

**3. 게임 캐릭터(챔피언) 전체 통계 서비스 제공**

<details>
<summary>작동 과정</summary>
<div markdown="1">

> ① 웹 페이지 상단의 '챔피언 분석' 버튼을 누르면 디폴트 포지션("TOP") 파라미터가 POST 방식으로 서버에 전달된다.
> 
> ② 서버로 전달된 파라미터들은 WAS(톰캣)에 의해 Request객체로 생성된 후 **LolsearcherFilter** 클래스를 거치며 character encoding 방식을 *UTF-8*로 설정한 후 **ChampionController**의 **champions(String position)** 메소드로 전달된다.
>
> ③ 파라미터로 전달된 포지션에 해당하는 챔피언 데이터들을 내부 로직의 우선순위(승률, 픽률 등)에 따라 DB에서 조회한다.
>
> ④ 조회된 데이터는 Model 객체에 셋팅하고 매핑된 Templete(View)에 Model을 전송한다.
>
> ⑤ Templete Engine은 Model 데이터를 통해 템플릿을 가공하여 response 객체를 생성하고 클라이언트로 전송한다.  

</div>
</details>

[**소스 코드로 보기**](https://github.com/kyo705/LolSearcher/blob/f58461b145226443b2b49292407906473116246c/lolsearcher/src/main/java/com/lolsearcher/controller/ChampionController.java#L27)

**4. 게임 캐릭터(챔피언) 세부 통계 서비스 제공**

<details>
<summary>작동 과정</summary>
<div markdown="1">

> ① 챔피언 분석 웹 페이지에서 특정 챔피언을 클릭하면 해당 챔피언 이름의 파라미터가 POST 방식으로 서버에 전달된다.
> 
> ② 서버로 전달된 파라미터들은 WAS(톰캣)에 의해 Request객체로 생성된 후 **LolsearcherFilter** 클래스를 거치며 character encoding 방식을 *UTF-8*로 설정한 후 **ChampionController**의 **championDetail(String champion)** 메소드로 전달된다.
>
> ③ 해당 챔피언의 구체적인 승률, 아이템 승률, 상대하기 쉬운 챔피언 등의 데이터들을 DB에서 조회한다.
>
> ④ 조회된 데이터는 Model 객체에 셋팅하고 매핑된 Templete(View)에 Model을 전송한다.
>
> ⑤ Templete Engine은 Model 데이터를 통해 템플릿을 가공하여 response 객체를 생성하고 클라이언트로 전송한다.  

</div>
</details>

[**소스 코드로 보기**](https://github.com/kyo705/LolSearcher/blob/f58461b145226443b2b49292407906473116246c/lolsearcher/src/main/java/com/lolsearcher/controller/ChampionController.java#L39)

**5. REST API 서비스 제공**


<details>
<summary>이미지 보기</summary>
<div markdown="1">

![image](https://user-images.githubusercontent.com/89891704/173006203-397f4b9d-9e86-48b9-b0ae-3e70ce6deb15.png)

![image](https://user-images.githubusercontent.com/89891704/173006734-d3de6ed3-f822-41ba-baf7-03ba1517f2c6.png)

</div>
</details>

DB 테이블 연관 관계
----------------------------------------
![image](https://user-images.githubusercontent.com/89891704/176092307-c32640d6-c883-4c10-a33e-285975b536c8.png)

![image](https://user-images.githubusercontent.com/89891704/176094789-83380d09-fe55-48d9-a6cd-1eeed82670cc.png)

![image](https://user-images.githubusercontent.com/89891704/176096311-9710fe8a-5401-4b57-bdbd-67580bd05dff.png)

 
----------------------------------------

# 프로젝트 과정에서 겪은 고민들과 해결 과정

----------------------------------------

**프로젝트 설계 시 외부 JSON 데이터를 바로 파싱해서 클라이언트에게 전달하지 않고 DB에 저장한 후 클라이언트에게 데이터를 제공한 이유**   

1. 성능상의 이점 : REST API 통신은 외부와의 I/O 비용이 발생하기 때문에 가져와야하는 데이터가 많으면 많을수록 속도 측면에서 느리다. 그리고 매번 같은 데이터를 받아와 파싱하는 과정도 불필요하기 때문에 DB에 저장하는 방식이 더 적절하다고 판단하였다.   

2. 통계 데이터(승률, 모스트챔프 등..)의 서비스 제공 가능 : 매번 REST API 요청을 파싱해서 클라이언트에게 전달할 경우 통계 관련 데이터를 제공하기 어렵다. 반면, DB에 저장하면 많은 양의 데이터를 통계낼 수 있기 때문에 정확한 서비스를 클라이언트에게 제공할 수 있다. 

**갱신 버튼을 설계하여 해당 버튼을 눌러야 REST API 요청을 통해 데이터를 받아오게한 이유**

1. 성능상의 이점 : 매번 클라이언트가 닉네임을 검색할 때마다 REST 통신을 한다면 외부 서버와의 I/O 비용과 DB 서버와의 I/O 비용이 발생하면서 오히려 'JSON 데이터를 바로 파싱해 클라이언트에게 제공하는 서비스' 보다 더욱 속도가 느리게 된다. 그래서 더욱 빠른 서비스를 제공하기 위해 갱신 요청을 통해서만 REST API 통신이 이루어지도록 설계하였다.   

2. 구조적 문제 : 게임 회사에서 제공되는 REST API 요청 횟수는 2분 동안 최대 100회였다. 그래서 매번 클라이언트가 닉네임을 검색할 때마다 REST 통신을 한다면 많은 유저가 서비스를 이용할 수 없는 구조가 된다.   

**Q. 그렇다면 악의적인 사용자가 계속 갱신 요청을 시도한다면??**   

A. 충분히 발생할 수 있는 상황이다. 그래서 나는 Summoner 객체(유저 정보를 담고있는 테이블과 매핑된 Entity)의 필드 값에 '최신 갱신 요청 시간(lastRenewTimeStamp)'를 추가하여 해당 유저가 최신으로 갱신 요청한 시간이 현재 시각보다 5분 이상일 경우에만 갱신이 가능하도록 설정하였다. 그렇게 하면 브라우저가 다른 곳에서도 특정 유저에 대한 반복적인 요청은 불가능해진다. 갱신 가능 요청 시간을 5분으로 설정한 이유는 보통 한 게임의 최소시간이 5분?(게임이 '다시하기'인 경우)인 것으로 알고있다. 그래서 5분 이전에는 갱신이 반복적으로 이뤄질 필요가 없다고 판단하여서 그렇게 설정하였다.

**Q. 그렇다면 존재하지 않는 닉네임을 계속 요청할 경우라면??**

A. 나도 해당 문제를 계속 고민해보았다. 그래서 존재하지 않는 닉네임을 검색하면 해당 닉네임을 DB에 저장하여 2분동안 REST 통신말고 DB 데이터를 전달하는 방식을 고민하기도 했다. 하지만 매번 다른 닉네임을 요청할 수 있기 때문에 해당 IP를 차단하는 방법이 가장 베스트일 것 같다. 하지만 이 방법도 IP우회를 통하면 막을 수 없기 때문에 더 좋은 방법을 고민해봐야한다.

**※해당 Q&A를 쓰면서 문득 든 생각**   

현재 프로젝트의 서비스는 REST API를 DB에 저장한 후 해당 DB데이터를 가져와 클라이언트에게 제공해준다. 그런데 이 방식을 REST API를 바로 파싱하여 클라이언트에게 제공해주고 DB에 저장하는 로직은 독립적인 스레드를 통해 처리하면 클라이언트가 더욱 빠르게 응답을 받을 수 있을 것이라는 생각이 들었다. 그래서 해당 방식으로 프로젝트를 수정해야겠다.


DB의 인덱스를 공부하면서 인덱스의 장점과 단점을 배웠다. 인덱스는 보통 B-TREE 혹은 B+TREE 자료구조를 사용해 컬럼들을 저장해놓는데 해당 자료구조는 정렬을 해놓기때문에 조회 시에는 빠르게 서치가 가능하지만 데이터를 삽입, 인덱스 컬럼 업데이트, 삭제에서는 큰 비용이 발생하기 때문에 조심해서 사용해야한다. 
현재, 해당 프로젝트에서의 Summoner 테이블에서 primaryId 컬럼을 pk설정을 통해 클러스터드 인덱스로 지정하였고, id, name 컬럼을 넌클러스터드 인덱스로 설정해주었다. 그 이유는 기존 id를 pk로 두었을 때, 게임 서버로부터 Summoner 데이터를 받아올 때, 우선순위가 낮은 순으로 받는 것이 아니기 때문에 데이터를 저장할 때마다 pk값이 자동으로 우선순위 낮은 값으로 생성되어 인덱스 자료구조의 정렬 비용을 줄일 수 있다. 그리고 Summoner 테이블은 데이터(row)가 많이 생성되지 않고 조회가 많아 넌클러스터드 인덱스를 설정해도 된다고 판단하여, 비지니스 로직에서 테이블을 조회할 때 필요한 조건 컬럼(id, name)을 넌클러스터드 인덱스로 설정하였다.   

Member 테이블의 pk를 복합키로 설정하고 해당 복합키의 부분키로 외래키를 설정한 이유(Match-Member 식별관계로 설정) : 두 테이블을 조인할 때, 서로의 matchid 컬럼을 통해 조인하는데 이 때, 둘 다 pk로 설정해두면 db에서 조인 시 nested loop join을 이용해 빠르게 데이터를 조회할 수 있고, 또한 pk로 인덱스 설정이 되었기 때문에(클러스터드 인덱스) random access 부하가 없어서 대용량 데이터 처리에도 좋다. 그래서 현재 Match-Member 테이블 연관 관계 및 pk 설정, 인덱스 설정 등이 최선이라고 생각한다.   

기존 Match-Member 테이블 조회시 패치조인에서 batch-size로 바꾼 이유 : 비지니스 로직에서 전적을 검색할 때, 최대 100개의 Match를 가져오려하였는데 패치 조인 사용 시, 페이징 처리가 불가능하여 해당 문제를 해결하고자 batch size를 설정하여 Match 엔티티들과 Member 엔티티들을 조회하였다. 그리고 batch size를 100으로 설정해서 총 2번의 쿼리로 연관 관계 엔티티들을 다 조회하였다. 이렇게 n+1문제를 해결하였다.   


