
package net.myfigurecollection.api;

import com.google.api.client.util.Key;
import com.google.gson.annotations.Expose;

import javax.annotation.Generated;
import org.apache.commons.lang.builder.ToStringBuilder;

@Generated("com.googlecode.jsonschema2pojo")
public class Item {

    @Key @Expose
    private Root root;
    @Key @Expose
    private Category category;
    @Key @Expose
    private Data data;
    @Key @Expose
    private Mycollection mycollection;

    public Root getRoot() {
        return root;
    }

    public void setRoot(Root root) {
        this.root = root;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Mycollection getMycollection() {
        return mycollection;
    }

    public void setMycollection(Mycollection mycollection) {
        this.mycollection = mycollection;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
