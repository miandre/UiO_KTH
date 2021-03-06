package nu.geeks.uio_kth.Objects;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;


import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Micke on 2016-03-02.
 */
public class User extends Activity {



    private static Map<String,Integer> names = new HashMap<>();


    public static String addName(String name){

        if(names.containsKey(name)){
            names.put(name,names.get(name)+1);
        }else names.put(name,1);


        return getName();


    }

    private static String getName(){

        if(!names.isEmpty()) {
            String retName = Collections.max(names.entrySet(), new Comparator<Map.Entry<String, Integer>>() {

                @Override
                public int compare(Map.Entry<String, Integer> lhs, Map.Entry<String, Integer> rhs) {
                    return lhs.getValue() > rhs.getValue() ? 1 : -1;
                }
            }).getKey();


            return retName;
        }
        else return "";
    }


}


