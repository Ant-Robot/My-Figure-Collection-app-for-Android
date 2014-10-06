
package net.myfigurecollection.api;

import javax.annotation.Generated;

import com.google.api.client.util.Key;
import com.google.gson.annotations.Expose;

import org.apache.commons.lang.builder.ToStringBuilder;

@Generated("com.googlecode.jsonschema2pojo")
public class Resolution {

    @Key @Expose
    private String width;
    @Key @Expose
    private String height;

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
