package net.myfigurecollection.api.adapter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import net.myfigurecollection.api.Item;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import hugo.weaving.DebugLog;

/**
 * Created by climbatize on 11/08/2014.
 */
public class ItemAdapter implements JsonDeserializer<List<Item>> {

    @DebugLog
    public List<Item> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) {
        String string = json.toString();
        string = string.replace("{}","null");
        JsonElement je = (new Gson()).fromJson(string,json.getClass());

        List<Item> vals = new ArrayList<Item>();
        if (je.isJsonArray()) {
            for (JsonElement e : je.getAsJsonArray()) {
                Item item = (Item) ctx.deserialize(e, Item.class);
                vals.add(item);
            }
        } else if (je.isJsonObject()) {
            vals.add((Item) ctx.deserialize(je, Item.class));
        } else {
            throw new RuntimeException("Unexpected JSON type: " + json.getClass());
        }
        return vals;
    }
}