package net.myfigurecollection.android.data.objects;

/**
 * A Figure category
 * 
 * @author Climbatize
 * 
 */
public class Category {
    public static final String COLOR              = "color";
    public static final String DEFAULT_SORT_ORDER = "name";
    private int                id;
    private String             name;
    private String             color;

    public Category(int id) {
        setId(id);
        setName("undef");
        setColor("#000000");
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the color
     */
    public String getColor() {
        return color;
    }

    /**
     * @param color
     *            the color to set
     */
    public void setColor(String color) {
        this.color = color;
    }

}
