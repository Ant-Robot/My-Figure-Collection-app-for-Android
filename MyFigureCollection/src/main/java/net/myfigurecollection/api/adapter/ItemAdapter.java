package net.myfigurecollection.api.adapter;

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
        List<Item> vals = new ArrayList<Item>();
        if (json.isJsonArray()) {
            for (JsonElement e : json.getAsJsonArray()) {
                Item item = (Item) ctx.deserialize(e, Item.class);
                vals.add(item);
            }
        } else if (json.isJsonObject()) {
            vals.add((Item) ctx.deserialize(json, Item.class));
        } else {
            throw new RuntimeException("Unexpected JSON type: " + json.getClass());
        }
        return vals;
    }
}