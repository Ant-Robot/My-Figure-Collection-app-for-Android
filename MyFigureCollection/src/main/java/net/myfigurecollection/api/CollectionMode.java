
package net.myfigurecollection.api;

import javax.annotation.Generated;

import com.google.api.client.util.Key;
import com.google.gson.annotations.Expose;
import org.apache.commons.lang.builder.ToStringBuilder;

@Generated("com.googlecode.jsonschema2pojo")
public class CollectionMode {

    @Key @Expose
    private String name;
    @Key @Expose
    private String version;
    @Key @Expose
    private Collection collection;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
