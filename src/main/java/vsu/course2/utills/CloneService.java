package vsu.course2.utills;

import vsu.course2.services.json.JsonService;

public class CloneService<T> {
    public T makeClone(T o) {
        JsonService<T> js = new JsonService<>();
        return js.deserialize(js.serialize(o), (Class<T>) o.getClass());
    }
}
