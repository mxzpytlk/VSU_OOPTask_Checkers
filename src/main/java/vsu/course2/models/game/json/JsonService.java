package vsu.course2.models.game.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import vsu.course2.models.game.field.Field;


public class JsonService<T> {
    Gson gson;

    public JsonService() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Field.class, new FieldDeserializer());
        this.gson = builder.create();
    }

    public String serialize(T object) {
        return gson.toJson(object);
    }

    public T deserialize(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }
}
