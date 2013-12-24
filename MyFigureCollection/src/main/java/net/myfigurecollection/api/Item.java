
package net.myfigurecollection.api;

import com.google.api.client.util.Key;
import com.google.gson.annotations.Expose;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Generated;

@Generated("com.googlecode.jsonschema2pojo")
public class Item implements Comparable<Item> {

    @Key
    @Expose
    private Root root;
    @Key
    @Expose
    private Category category;
    @Key
    @Expose
    private Data data;
    @Key
    @Expose
    private Mycollection mycollection;
    @Key
    @Expose
    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Root getRoot() {
        return root;
    }

    public void setRoot(Root root) {
        this.root = root;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Mycollection getMycollection() {
        return mycollection;
    }

    public void setMycollection(Mycollection mycollection) {
        this.mycollection = mycollection;
    }

    public String getName() {
        final int manuStart = getData().getName().lastIndexOf("(", getData().getName().length() - 1);

        return (getData().getName().substring(0, manuStart));
    }

    public Date getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date d = null;
        try {
            d = sdf.parse(getData().getRelease_date());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;

    }

    public String getCopyright() {
                SimpleDateFormat sdfout = new SimpleDateFormat("yyyy", Locale.getDefault());

        String date = "";
        Date d = getDate();

        if (d != null)
            date = sdfout.format(d);


        StringBuilder sb = new StringBuilder("©");

        sb.append(date).append(d != null ? " " : "").append(getManufacturer());

        if (Integer.parseInt(getData().getPrice()) > 0) sb.append(" - ")
                .append(getData().getPrice()).append(" ¥");

        return sb.toString();
    }

    public String getManufacturer()
    {
        final int manuStart = getData().getName().lastIndexOf("(", getData().getName().length() - 1);
        return getData().getName().substring(manuStart + 1, getData().getName().length() - 1);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int compareTo(Item another) {
        if (another.getCategory().getName().equalsIgnoreCase(getCategory().getName()))
        {
            return this.getName().compareTo(another.getName());
        }
        return this.getCategory().getName().compareTo(another.getCategory().getName());
    }
}
