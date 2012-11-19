
package Utils;

public interface Constants
{
	public final static String	ACTION							= "action";

	public final static String	ACTION_DELETE					= "delete";
	public final static String	ACTION_ORDER					= "order";
	public final static String	ACTION_OWN						= "own";
	public final static String	ACTION_WISH						= "wish";
	/**
	 * e.g: http://myfigurecollection.net/api.php?mode=collection&username=
	 * climbatize
	 */
	public static final String	API_MODE_COLLECTION				= "http://myfigurecollection.net/api.php?mode=collection&username=";

	/**
	 * e.g:
	 * http://myfigurecollection.net/api.php?mode=gallery&type=json&item=5769
	 */
	public static final String	API_MODE_FIGURE_GALLERY_JSON	= "http://myfigurecollection.net/api.php?mode=gallery&type=json&item=";
	/**
	 * e.g:
	 * http://myfigurecollection.net/api.php?mode=gallery&type=json&username
	 * =climbatize
	 */
	public static final String	API_MODE_PROFILE_GALLERY_JSON	= "http://myfigurecollection.net/api.php?mode=gallery&type=json&username=";
	/**
	 * http://myfigurecollection.net/api.php?mode=search&keywords=5769
	 */
	public static final String	API_MODE_SEARCH					= "http://myfigurecollection.net/api.php?mode=search&keywords=";
	public final static String	CAT_ORDERED						= "1";

	public final static String	CAT_OWNED						= "2";
	public final static String	CAT_WISHED						= "0";
	// Figure_List
	public static final int		CONTEXTMENU_DELETEITEM			= 9;
	public static final int		CONTEXTMENU_GOTOMFC				= 0;
	public static final int		CONTEXTMENU_ORDERITEM			= 2;
	public static final int		CONTEXTMENU_OWNITEM				= 3;

	public static final int		CONTEXTMENU_SCANITEM			= 4;
	public static final int		CONTEXTMENU_WISHITEM			= 1;
	public final static String	CURCAT							= "curcat";

	public static final String	DATABASE_NAME					= "myfigurecollection";
	public static final String	DATABASE_SUGGEST				= "suggest";
	public static final String	DATABASE_TABLE_FIGURES			= "figures";

	public static final int		DATABASE_VERSION				= 1;
	public static final boolean	DEBUGMODE						= true;
	public static final String	FIGUREURL						= "http://myfigurecollection.net/item/";
	public final static String	ID								= "id";

	public static final String	PROJECT_TAG						= "Tsuki";
	public static final String	TAB_ORDERED						= "Ordered";
	public final static int		TAB_ORDERED_POSITION			= 1;
	// Home
	public static final String	TAB_OWNED						= "Owned";

	public final static int		TAB_OWNED_POSITION				= 0;
	public static final String	TAB_SEARCH						= "Search";
	public final static int		TAB_SEARCH_POSITION				= -1;

	public static final String	TAB_STATISTICS					= "statistics";
	public static final String	TAB_WISHED						= "Wished";

	public final static int		TAB_WISHED_POSITION				= 2;

	public static final String	THUMBROOT						= "http://myfigurecollection.net/pics/figure/";

}