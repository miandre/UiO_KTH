package nu.geeks.uio_kth.Objects;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Micke on 2016-03-02.
 */
public class User {

private static Map<String,Integer> names = new HashMap<>();

    public static void addName(String name){

        if(names.containsKey(name)){
            names.put(name,names.get(name)+1);
        }else names.put(name,1);
    }

    public static String getName(){

        String retName = Collections.max(names.entrySet(),new Comparator<Map.Entry<String,Integer>>(){

            @Override
            public int compare(Map.Entry<String, Integer> lhs, Map.Entry<String, Integer> rhs) {
                return lhs.getValue()>rhs.getValue()? 1:-1;
            }
        }).getKey();

        return retName;
    }


}


