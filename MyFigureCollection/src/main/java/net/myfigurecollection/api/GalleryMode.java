
package net.myfigurecollection.api;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import org.apache.commons.lang.builder.ToStringBuilder;

@Generated("com.googlecode.jsonschema2pojo")
public class GalleryMode {

    @Expose
    private String name;
    @Expose
    private String version;
    @Expose
    private Gallery gallery;

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

    public Gallery getGallery() {
        return gallery;
    }

    public void setGallery(Gallery gallery) {
        this.gallery = gallery;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
