package nu.geeks.uio_kth.Objects;

import android.support.annotation.NonNull;

import java.util.Collection;
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

private static Map<String,Integer> names = new TreeMap<>();

    public static void addName(String name){
        int count = 0;

        if(names.containsKey(name)){
            names.put(name,names.get(name)+1);
        }else names.put(name,1);
    }


}


