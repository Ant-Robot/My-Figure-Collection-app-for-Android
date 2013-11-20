
package net.myfigurecollection.api;

import javax.annotation.Generated;

import com.google.api.client.util.Key;
import com.google.gson.annotations.Expose;

import org.apache.commons.lang.builder.ToStringBuilder;

@Generated("com.googlecode.jsonschema2pojo")
public class User {

    @Key
    @Expose
    private String id;
    @Key
    @Expose
    private String name;
    @Key
    @Expose
    private String picture;
    @Key
    @Expose
    private String homepage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
