
package net.myfigurecollection.api;

import com.google.api.client.util.Key;
import com.google.gson.annotations.Expose;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.annotation.Generated;

@Generated("com.googlecode.jsonschema2pojo")
public class Collection {

    @Key
    @Expose
    private String link;
    @Key
    @Expose
    private Ordered ordered;
    @Key
    @Expose
    private Wished wished;
    @Key
    @Expose
    private Owned owned;

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

    public Owned getOwned() {
        return owned;
    }

    public void setOwned(Owned owned) {
        this.owned = owned;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
