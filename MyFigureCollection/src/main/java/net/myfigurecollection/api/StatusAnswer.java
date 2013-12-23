package net.myfigurecollection.api;

import com.google.api.client.util.Key;
import com.google.gson.annotations.Expose;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.annotation.Generated;

/**
 * @author climbatize.reload@gmail.com
 * @version 1.0
 * @since 22/12/2013
 */
@Generated ("com.googlecode.jsonschema2pojo")
public class StatusAnswer {

    @Key @Expose
    private String label;
    @Key @Expose
    private String data;
    @Key @Expose
    private Boolean reload;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public StatusAnswer withLabel(String label) {
        this.label = label;
        return this;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public StatusAnswer withData(String data) {
        this.data = data;
        return this;
    }

    public Boolean getReload() {
        return reload;
    }

    public void setReload(Boolean reload) {
        this.reload = reload;
    }

    public StatusAnswer withReload(Boolean reload) {
        this.reload = reload;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}

