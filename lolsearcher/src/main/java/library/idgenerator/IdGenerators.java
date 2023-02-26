package library.idgenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IdGenerators {

    private final Map<String, IdGenerator> idGeneratorMap;

    public IdGenerators(){
        idGeneratorMap = new HashMap<>(); /* id generator를 변경하지 않기 떄문에 동시성 문제 없음 */
    }

    public boolean addIdGenerator(String name, IdGenerator idGenerator){

        if(idGeneratorMap.containsKey(name)){
            return false;
        }
        idGeneratorMap.put(name, idGenerator);
        return true;
    }

    public IdGenerator getIdGenerator(String name){
        return idGeneratorMap.get(name);
    }

    public IdGenerator getFirstIdGenerator(){
        return new ArrayList<>(idGeneratorMap.values()).get(0);
    }

    public int size() {
        return idGeneratorMap.size();
    }
}
