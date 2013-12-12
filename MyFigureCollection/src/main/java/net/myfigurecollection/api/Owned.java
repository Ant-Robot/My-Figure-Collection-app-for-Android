
package net.myfigurecollection.api;

import com.google.api.client.util.Key;
import com.google.gson.annotations.Expose;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("com.googlecode.jsonschema2pojo")
public class Owned {

    @Key @Expose
    private String link;
    @Key @Expose
    private String num_items;
    @Key @Expose
    private String num_pages;
    @Key @Expose
    private List<Item> item = new ArrayList<Item>();

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getNum_items() {
        return num_items;
    }

    public void setNum_items(String num_items) {
        this.num_items = num_items;
    }

    public String getNum_pages() {
        return num_pages;
    }

    public void setNum_pages(String num_pages) {
        this.num_pages = num_pages;
    }

    public List<Item> getItem() {
        return item;
    }

    public void setItem(List<Item> item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
