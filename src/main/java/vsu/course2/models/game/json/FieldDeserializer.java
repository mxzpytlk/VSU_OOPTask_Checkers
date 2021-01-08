package vsu.course2.models.game.json;

import com.google.gson.*;
import vsu.course2.models.game.Checker;
import vsu.course2.models.game.exceptions.CellNotExistException;
import vsu.course2.models.game.field.Field;
import vsu.course2.services.FieldService;

import java.lang.reflect.Type;

public class FieldDeserializer implements JsonDeserializer {
    @Override
    public Field deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        FieldService fs = new FieldService();

        JsonObject object = json.getAsJsonObject();
        JsonArray vEdjList = object.getAsJsonObject("field").getAsJsonArray("vEdjLists");

        int w = object.get("width").getAsInt();
        int h = object.get("height").getAsInt();
        Field field = fs.createField(w, h);
        for (JsonElement element : vEdjList) {
            JsonObject cellObj = element.getAsJsonArray().get(0).getAsJsonObject();
            if (cellObj.has("curCheck")) {
                Checker check = new Checker(cellObj.getAsJsonObject("curCheck").get("ID").getAsInt());
                if (cellObj.getAsJsonObject("curCheck").get("isKing").getAsBoolean()) {
                    check.becomeKing();
                }

                try {
                    fs.getCell(cellObj.get("letter").getAsInt(), cellObj.get("number").getAsInt(), field)
                            .setCheck(check);
                } catch (CellNotExistException e) {
                    e.printStackTrace();
                }
            }
        }

        return  field;
    }
}
