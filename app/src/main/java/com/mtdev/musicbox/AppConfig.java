package com.mtdev.musicbox;

/**
 * Created by Taha on 22/04/2018.
 */

public class AppConfig {

    public static String MY_IP_ADRESS = "192.168.1.18" ;


    public static String URL_LOGIN = "http://"+MY_IP_ADRESS +"/piDev/Login.php";

    // Server user register url
    public static String URL_REGISTER = "http://"+MY_IP_ADRESS +"/piDev/Register.php";

    //update image url and user infos
    public static String URL_UPDATEIMAGE = "http://"+MY_IP_ADRESS +"/piDev/updateUser.php";

    public static String URL_ADDCOMMAND = "http://"+MY_IP_ADRESS +"/piDev/addCommand.php";

    public static String URL_SELECTPRODUCTS = "http://"+MY_IP_ADRESS +"/piDev/getProductList.php";

    public static String URL_SELECT_TYPE_PRODUCTS = "http://"+MY_IP_ADRESS +"/piDev/getTypeProductList.php";

    public static String URL_ADDPRODUCT_COMMAND = "http://"+MY_IP_ADRESS +"/piDev/addProduct_Commande.php";

    public static String URL_GETIMG_PREFIX = "http://"+MY_IP_ADRESS +"/images/";

    // Directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";

    public static final String HOST = "192.168.1.19";

}
