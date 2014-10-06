
package net.myfigurecollection.api;

import com.google.api.client.util.Key;
import com.google.gson.annotations.Expose;

import javax.annotation.Generated;
import org.apache.commons.lang.builder.ToStringBuilder;

@Generated("com.googlecode.jsonschema2pojo")
public class Mycollection {

    @Key @Expose
    private String number;
    @Key @Expose
    private String score;
    @Key @Expose
    private String wishability;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getWishability() {
        return wishability;
    }

    public void setWishability(String wishability) {
        this.wishability = wishability;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
