package vsu.course2.models.game.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import vsu.course2.models.game.field.Field;


public class JsonService<T> {
    public String serialize(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public T deserialize(String json, Class<T> type) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Field.class, new FieldDeserializer());
        Gson gson = builder.create();
        return gson.fromJson(json, type);
    }
}
