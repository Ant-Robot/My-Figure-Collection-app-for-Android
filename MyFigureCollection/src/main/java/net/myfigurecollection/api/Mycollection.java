
package net.myfigurecollection.api;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import org.apache.commons.lang.builder.ToStringBuilder;

@Generated("com.googlecode.jsonschema2pojo")
public class Mycollection {

    @Expose
    private String number;
    @Expose
    private String score;
    @Expose
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
