package library.idgenerator.enumeration;

public enum IdGenerationMode {

    /**
    *   하나의 IdGenerator 생성으로 모든 테이블에서 사용하는 방식
    **/
    SINGLETON,

    /**
     *   테이블당 하나의 IdGenerator 생성해 사용하는 방식
     **/
    INDIVIDUAL
}
