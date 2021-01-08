package vsu.course2.utills;

import com.google.gson.Gson;

public class Cloner {
    public static Object makeClone(Object o) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(o), o.getClass());
    }
}
