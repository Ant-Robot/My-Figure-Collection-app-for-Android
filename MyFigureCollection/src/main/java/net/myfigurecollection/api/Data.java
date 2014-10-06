
package net.myfigurecollection.api;

import javax.annotation.Generated;

import com.google.api.client.util.Key;
import com.google.gson.annotations.Expose;
import org.apache.commons.lang.builder.ToStringBuilder;

@Generated("com.googlecode.jsonschema2pojo")
public class Data {

    @Key @Expose
    private String id;
    @Key @Expose
    private String jan;
    @Key @Expose
    private String isbn;
    @Key @Expose
    private String barcode;
    @Key @Expose
    private String catalog;
    @Key @Expose
    private String name;
    @Key @Expose
    private String release_date;
    @Key @Expose
    private String price;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJan() {
        return jan;
    }

    public void setJan(String jan) {
        this.jan = jan;
    }

    public String getIsbn() {
        return isbn;
    }
    public void setBarcode(String jan) {
        this.barcode = jan;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
