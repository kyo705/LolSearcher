# LOL SEARCHER

인기 게임 '리그 오브 레전드' 의 게임 전적 데이터를 DB로부터 가져와 클라이언트에게 제공하는 애플리케이션

## 애플리케이션 주요 기능

> 1. 클라이언트가 특정 유저 게임 데이터를 조회 시 Cache, DB로부터 가져옴
> 2. 어뷰저 차단 및 차단된 유저 접속 방지 기능
> 3. OPEN API 서비스를 통해 현재 DB에 있는 데이터를 JSON으로 제공
> 4. OPEN API 서비스를 이용하기 위한 회원가입, 로그인 관련 기능들을 제공


프로젝트 깃 브런치
-----------------------------------------
> - **main** — 실제 메인 브런치(완성본)
> - **develop** — 다음 버전을 위한 개발 브런치(테스트용)

프로젝트 커밋 메시지 카테고리
-----------------------------------------
> - [INITIAL] — repository를 생성하고 최초에 파일을 업로드 할 때
> - [ADD] — 신규 파일 추가
> - [UPDATE] — 코드 변경이 일어날때
> - [REFACTOR] — 코드를 리팩토링 했을때
> - [FIX] — 잘못된 링크 정보 변경, 필요한 모듈 추가 및 삭제
> - [REMOVE] — 파일 제거
> - [STYLE] — 디자인 관련 변경사항


프로젝트 내 적용 기술
-----------------------------------------
> - 백 앤드
>   - 언어 : Java
>   - 프레임 워크 : SpringBoot, Spring MVC, Spring Security
>   - ORM : JPA(Hibernate)
>   - 빌드 관리 툴 : Gradle
> - DevOps
>   - WAS : Tomcat
>   - Cache : Redis
>   - DBMS :
>      - 실제 서버 환경 : MariaDB
>      - 테스트 환경 : h2

## 프로젝트 디렉토리 구조 

<details>
<summary>디렉토리 보기</summary>


````
    lolsearcher
        ├─annotation
        │  └─transaction
        ├─aop
        │  └─timetrace
        ├─api
        │  ├─lolsearcher
        │  └─notification
        ├─config
        │  ├─cache
        │  ├─database
        │  ├─security
        │  │  └─configuer
        │  └─session
        ├─constant
        │  └─enumeration
        ├─controller
        │  ├─opnapi
        │  ├─search
        │  │  ├─match
        │  │  ├─mostchamp
        │  │  ├─rank
        │  │  ├─stats
        │  │  └─summoner
        │  └─user
        ├─exception
        │  ├─exception
        │  │  ├─common
        │  │  ├─search
        │  │  │  ├─champion
        │  │  │  ├─rank
        │  │  │  └─summoner
        │  │  └─user
        │  │      ├─identification
        │  │      ├─join
        │  │      ├─login
        │  │      └─session
        │  └─handler
        │      ├─controller
        │      │  ├─openapi
        │      │  ├─search
        │      │  └─user
        │      │      ├─join
        │      │      └─session
        │      └─filter
        │          ├─servlet
        │          └─springsecurity
        │              ├─authentication
        │              └─authorization
        ├─filter
        │  ├─Authentication
        │  │  ├─join
        │  │  └─login
        │  ├─ban
        │  └─header
        ├─model
        │  ├─entity
        │  │  ├─champion
        │  │  ├─match
        │  │  ├─mostchamp
        │  │  ├─rank
        │  │  ├─summoner
        │  │  └─user
        │  ├─factory
        │  ├─request
        │  │  ├─notification
        │  │  ├─search
        │  │  │  ├─championstats
        │  │  │  ├─ingame
        │  │  │  ├─match
        │  │  │  ├─mostchamp
        │  │  │  ├─rank
        │  │  │  └─summoner
        │  │  └─user
        │  │      ├─identification
        │  │      ├─join
        │  │      ├─login
        │  │      ├─security
        │  │      └─session
        │  └─response
        │      ├─error
        │      ├─front
        │      │  ├─search
        │      │  │  ├─championstats
        │      │  │  ├─match
        │      │  │  ├─mostchamp
        │      │  │  ├─rank
        │      │  │  └─summoner
        │      │  └─user
        │      ├─notification
        │      └─openapi
        ├─repository
        │  ├─openapi
        │  ├─search
        │  │  ├─champstats
        │  │  ├─match
        │  │  ├─mostchamp
        │  │  ├─rank
        │  │  └─summoner
        │  ├─session
        │  └─user
        └─service
            ├─ban
            ├─notification
            ├─openapi
            ├─search
            │  ├─match
            │  ├─mostchamp
            │  ├─rank
            │  ├─stats
            │  └─summoner
            └─user
                ├─identification
                ├─join
                ├─login
                ├─security
                └─session
````

</details>

프로젝트 톰캣 스레드풀 최소 갯수 설정
-------------------------
**커넥션풀 사이즈 공식 : 스레드 수 = 사용 가능한 코어 수 * (1+대기 시간/서비스 시간)**   
[by  "Java Concurrency in Practice"]   

위 공식을 적용하여 성능 측정 결과 최소 36, 최대 72의 스레드 갯수가 나오므로 최소 유지 스레드 갯수를 평균값인 54로 적용함

프로젝트 HikariCP 커넥션풀 최소 갯수 설정
-------------------------
**커넥션풀 사이즈 공식 : connections = ((core_count * 2) + effective_spindle_count)   
[by https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing]**

로컬 CPU 6 core 12 thread, 하드디스크 1개이므로 커넥션풀 사이즈 25로 설정


## [프로젝트 과정에서 겪은 고민들과 해결 과정](https://github.com/kyo705/LolSearcher/wiki/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EA%B0%9C%EB%B0%9C%EC%8B%9C-%EA%B3%A0%EB%AF%BC-%EA%B3%BC%EC%A0%95%EA%B3%BC-%ED%95%B4%EA%B2%B0-%EB%B0%A9%EB%B2%95#issue-posting)


