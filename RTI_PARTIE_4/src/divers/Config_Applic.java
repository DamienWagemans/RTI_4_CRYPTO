package divers;

// @author damien

 public class Config_Applic 
 {
    
    //portabilité !!!  
    public static String rep = System.getProperty("user.dir"); 
    public static String sep = System.getProperty("file.separator");
    public static String pathConfig = (rep + sep + "file" + sep + "config.properties");
    public static String pathLogin = (rep + sep + "file" + sep + "login.properties");
    // cheminconfig = /Users/damien/Dropbox/Java_Salle_Presse/file/config.properties
    //ceci sera valable en fonctio de l'os utilisé grace aux "system.getProperty"
    

    //crypto
    public static String pathKEYstore_Client_Frontiere = (rep + sep + "file" + sep + "client_frontiere_keystore.properties");
    public static String pathKEYstore_Serveur_National = (rep + sep + "file" + sep + "serveur_national_keystore.properties");
    public static String pathKEYstore_Serveur_International = (rep + sep + "file" + sep + "serveur_international_keystore.properties");
    

}
