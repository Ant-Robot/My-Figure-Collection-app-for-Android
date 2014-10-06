
package net.myfigurecollection.api;

import com.google.api.client.util.Key;
import com.google.gson.annotations.Expose;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.annotation.Generated;

@Generated("com.googlecode.jsonschema2pojo")
public class Picture implements Comparable<Picture>{

    @Key
    @Expose
    private String id;
    @Key
    @Expose
    private String src;
    @Key
    @Expose
    private String author;
    @Key
    @Expose
    private String date;
    @Key
    @Expose
    private Category category;
    @Key
    @Expose
    private Resolution resolution;
    @Key
    @Expose
    private String size;
    @Key
    @Expose
    private String title;
    @Key
    @Expose
    private String nsfw;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Resolution getResolution() {
        return resolution;
    }

    public void setResolution(Resolution resolution) {
        this.resolution = resolution;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNsfw() {
        return nsfw;
    }

    public void setNsfw(String nsfw) {
        this.nsfw = nsfw;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int compareTo(Picture another) {
        if (another.getCategory().getName().equalsIgnoreCase(getCategory().getName()))
        {
            return this.getDate().compareTo(another.getDate());
        }
        return this.getCategory().getId().compareTo(another.getCategory().getId());
    }
}
