package net.myfigurecollection.android.data.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Profile {

    private String                   url;
    private String                   nickname;
    private String                   ownedlink;
    private String                   orderedlink;
    private String                   wishedlink;
    private HashMap<Integer, Figure> figures_owned;
    private HashMap<Integer, Figure> figures_wished;
    private HashMap<Integer, Figure> figures_ordered;

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @param nickname
     *            the nickname to set
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * @return the ownedlink
     */
    public String getOwnedlink() {
        return ownedlink;
    }

    /**
     * @param ownedlink
     *            the ownedlink to set
     */
    public void setOwnedlink(String ownedlink) {
        this.ownedlink = ownedlink;
    }

    /**
     * @return the orderedlink
     */
    public String getOrderedlink() {
        return orderedlink;
    }

    /**
     * @param orderedlink
     *            the orderedlink to set
     */
    public void setOrderedlink(String orderedlink) {
        this.orderedlink = orderedlink;
    }

    /**
     * @return the wishedlink
     */
    public String getWishedlink() {
        return wishedlink;
    }

    /**
     * @param wishedlink
     *            the wishedlink to set
     */
    public void setWishedlink(String wishedlink) {
        this.wishedlink = wishedlink;
    }

    /**
     * @return the figures_owned
     */
    public HashMap<Integer, Figure> getFigures_owned() {
        if (figures_owned == null) {
            figures_owned = new HashMap<Integer, Figure>();
        }
        return figures_owned;
    }

    /**
     * @param figuresOwned
     *            the figures_owned to set
     */
    public void setFigures_owned(HashMap<Integer, Figure> figuresOwned) {
        figures_owned = figuresOwned;
    }

    /**
     * @return the figures_wished
     */
    public HashMap<Integer, Figure> getFigures_wished() {
        if (figures_wished == null) {
            figures_wished = new HashMap<Integer, Figure>();
        }

        return figures_wished;
    }

    /**
     * @param figuresWished
     *            the figures_wished to set
     */
    public void setFigures_wished(HashMap<Integer, Figure> figuresWished) {
        figures_wished = figuresWished;
    }

    /**
     * @return the figures_ordered
     */
    public HashMap<Integer, Figure> getFigures_ordered() {
        if (figures_ordered == null) {
            figures_ordered = new HashMap<Integer, Figure>();
        }
        return figures_ordered;
    }

    public ArrayList<Figure> getFigures_ordered_arraylist() {
        if (figures_ordered == null) {
            figures_ordered = new HashMap<Integer, Figure>();
        }

        ArrayList<Figure> figures = new ArrayList<Figure>(figures_ordered.values());
        Collections.sort(figures);
        return figures;
    }

    public ArrayList<Figure> getFigures_wished_arraylist() {
        if (figures_wished == null) {
            figures_wished = new HashMap<Integer, Figure>();
        }

        ArrayList<Figure> figures = new ArrayList<Figure>(figures_wished.values());
        Collections.sort(figures);
        return figures;
    }

    public ArrayList<Figure> getFigures_owned_arraylist() {
        if (figures_owned == null) {
            figures_owned = new HashMap<Integer, Figure>();
        }

        ArrayList<Figure> figures = new ArrayList<Figure>(figures_owned.values());
        Collections.sort(figures);
        return figures;
    }

    /**
     * @param figuresOrdered
     *            the figures_ordered to set
     */
    public void setFigures_ordered(HashMap<Integer, Figure> figuresOrdered) {
        figures_ordered = figuresOrdered;
    }

    public void setFigureWished(Figure f) {

        getFigures_ordered().remove(f.getId());
        getFigures_owned().remove(f.getId());
        getFigures_wished().put(f.getId(), f);

    }

    public void setFigureOrdered(Figure f) {

        getFigures_wished().remove(f.getId());
        getFigures_owned().remove(f.getId());
        getFigures_ordered().put(f.getId(), f);

    }

    public void setFigureOwned(Figure f) {

        getFigures_ordered().remove(f.getId());
        getFigures_wished().remove(f.getId());
        getFigures_owned().put(f.getId(), f);

    }

    public void removeFigure(Figure f) {

        getFigures_ordered().remove(f.getId());
        getFigures_wished().remove(f.getId());
        getFigures_owned().remove(f.getId());

    }

    @Override
    public String toString() {
        return getNickname() + "\nLink: " + getUrl() + "\nNumber of figures owned: " + getFigures_owned().size() + "\nNumber of figures ordered: "
               + getFigures_ordered().size() + "\nNumber of figures wished: " + getFigures_wished().size();
    }

}
