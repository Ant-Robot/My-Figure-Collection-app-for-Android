
package net.myfigurecollection.api;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import org.apache.commons.lang.builder.ToStringBuilder;

@Generated("com.googlecode.jsonschema2pojo")
public class Gallery {

    @Expose
    private String num_pictures;
    @Expose
    private String num_pages;
    @Expose
    private List<Picture> picture = new ArrayList<Picture>();

    public String getNum_pictures() {
        return num_pictures;
    }

    public void setNum_pictures(String num_pictures) {
        this.num_pictures = num_pictures;
    }

    public String getNum_pages() {
        return num_pages;
    }

    public void setNum_pages(String num_pages) {
        this.num_pages = num_pages;
    }

    public List<Picture> getPicture() {
        return picture;
    }

    public void setPicture(List<Picture> picture) {
        this.picture = picture;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
