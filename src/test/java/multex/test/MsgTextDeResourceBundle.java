package multex.test;

import java.util.Locale;

/**
 * ResourceBundle with german message texts and german Locale for testing class MsgText.
 * @author Christoph Knabe 2003-05-22
*/
public class MsgTextDeResourceBundle extends MsgTextResourceBundle {
	

    public Locale getLocale(){return Locale.GERMAN;}

    public String getCauseMarker(){return "Ursache: ";}
    
}
