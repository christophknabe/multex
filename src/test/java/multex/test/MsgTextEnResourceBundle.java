package multex.test;

import java.util.Locale;

/**
 * ResourceBundle with message texts and english Locale for testing class MsgText.
 * @author Christoph Knabe 2003-09-09
*/
public class MsgTextEnResourceBundle extends MsgTextResourceBundle {
	

    public Locale getLocale(){return Locale.ENGLISH;}

    public String getCauseMarker(){return "Cause: ";}

}
