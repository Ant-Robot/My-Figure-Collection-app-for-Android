
package net.myfigurecollection.api;

import com.google.api.client.util.Key;
import com.google.gson.annotations.Expose;

import javax.annotation.Generated;
import org.apache.commons.lang.builder.ToStringBuilder;

@Generated("com.googlecode.jsonschema2pojo")
public class Collection {

    @Key @Expose
    private String link;
    @Key @Expose
    private Ordered ordered;
    @Key @Expose
    private Wished wished;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Ordered getOrdered() {
        return ordered;
    }

    public void setOrdered(Ordered ordered) {
        this.ordered = ordered;
    }

    public Wished getWished() {
        return wished;
    }

    public void setWished(Wished wished) {
        this.wished = wished;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
