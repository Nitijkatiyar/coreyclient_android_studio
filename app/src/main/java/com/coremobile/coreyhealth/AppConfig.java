package com.coremobile.coreyhealth;


public class AppConfig {

    /*
     * For CoreySurg package name is com.coremobile.coreyhealth need to change the
     * package name (com.coremobile.coreyhealth) to make it work for CoreyHealth
     * From eclipse import the project , src , right click on package name -
     * refactor - rename - select first three options in Rename package dialog
     * change the package name to com.coremobile.coreyhealth , need to change
     * manually in AndroidManifest.xml file in some place.
     *
     * Note : main thing to make sure authority in provider in manifest file has
     * changed as per package name.
     *
     * Every application must have unique authority name
     *
     * <provider android:name="com.coremobile.coreyhealth.DatabaseProvider"
     * android:authorities="com.coremobile.coreyhealth.databaseprovider" />
     *
     *
     *
     * And cross verify authority name "com.coremobile.coreyhealth.databaseprovider"
     * in DataProvider.java the following string
     *
     * public static final Uri CONTENT_URI = Uri
     * .parse("content://com.coremobile.coreyhealth.databaseprovider/messages");
     * public static final Uri CONTENT_URI_phonecalls = Uri
     * .parse("content://com.coremobile.coreyhealth.databaseprovider/phonecalls");
     */
    /*
     * New procedure by Ramesh
     * Clone Android app to a separate directory
     * git clone  https://rameshm@coremobile.git.cloudforge.com/coreyclient_android.git
     * Checkout to coreysales20 branch
     * copy relevent manifest file from other folder
     * Import project to eclipse
     * Change the app name Coreysales to desired appname -Right click on the name and refactor
     * Change the package name com.coremobile.coreyhealth to com.coremobile.desired package name
     * Replace relevent text in Databaseprovider.java file
     * Correct the errors in activity_add_staff.xml
     * Replace the text in strings.xml
     * Set the flags in this file appropriately
     * Before submitting appstore change the version no.( check playstore for latest no.)
     * set the appname in gcm notitification in GCMIntentService file
     * set single tab for coreyhealth & Coreyhealth
     *
     * ****Additional Changes for periop*****
     * Search and change corey, coremobile references in Strings.xml
     * Search and change corey in all webview activity
     * ************************************************
     * **** Additional Changes for Meritus****
     * Disable the permission for contacts, Location, SD Card
     * Add the following in manifest under application
     * android:debuggable ="false"
     * android:icon="@drawable/merituslogo"
     *
     * ****************************************
     */

    /*
    * Increment the version to prevent crash on update app
    *
    */
    public static int DatabaseVersion = 15;


    /*
    * Increment the version to prevent crash on update app
    *
    */
    public static boolean isSupportPartialMsgLoading = true;

    /*
     * Change the flag to false if AES encryption is not needed for the build
     *
     */
    public static boolean isAESEnabled = true;

    /*
     * Change the flag to false if AES encryption is not needed for the build
     *
     */
    public static boolean isClientCertEnabled = false;


    /*
     * Change the flag to false if in development no need to change the keys and
     * application name for push notification
     */
    public static boolean isInProduction = true;

    /*
     * make the flag true if the application is CoreyHealth
     * and false if not
     */
    public static boolean isAppCoreyHealth = true;

    /*
     *  make the flag true if the application is CoreySurg
     *  and false if not
     */
    public static boolean isAppCoreySurg = false;
    /*
     *  make the flag true if the application is CoreyPatient
     *  and false if not
     */
    public static boolean isAppCoreyPatient = false;
    /*
     *  make the flag true if the application is CoreyEd
     *  and false if not
     */
    public static boolean isAppCoreyEd = false;

    /*
     *  make the flag true if the application is CoreySales
     *  and false if not
     */
    public static boolean isAppCoreySales = false;

    /*
     *  make the flag true if the application is CoreyFinance
     *  and false if not
     */
    public static boolean isAppCoreyFinance = false;

    /*
     *  make the flag true if the application is CoreyFinance
     *  and false if not
     */
    public static boolean isAppCoreyPeriop = false;
    public static boolean isAppCoreyMeritus = false;
    public static boolean isAppCoreyOr = false;
    /*
     * Following GCM sender to be used in MyApplicaion.java , the gcm sender is required
     * for push notification as part of Urbanairship configuration . The gcm
     * sender is the project id  found in Google developer console with the
     * registered project name
     */
    public static String GcmSenderForCoreyHealth = "566949794243";

    public static String GcmSenderForCoreySurg = "350169441553";

    /*
     * Following application key and application secret key to be used in
     * MyApplicaion.java , the keys are required for push notification . The
     * keys are generated from UrbanAirship. We need to add the key generated
     * from Google developer console (after registering application ), in
     * respective UA application under Android / GCM  , under servcies
     * the appkey and appsecret key will be generated
     */

    /*
     * First two are CoreySurg production keys , the app is registered in UA
     * under CoreySurg Production application . The third and fourth are
     * registered in UA under CoreyHealth Production application. The fifth and sixth
     */
    public static String DevelopmentAppSecretKeyForCoreySurgProduction = "WVFQ2JUVTs6j_Ud3oDf2DQ";

    public static String DevelopmentAppKeyForCoreySurgProduction = "3wBXb71EQgydQZkMuMlVLQ";

    public static String DevelopmentAppSecretKeyForCoreyHealthProduction = "2jh0dLdaToW974IBV2-2DQ";

    public static String DevelopmentAppKeyForCoreyHealthProduction = "zy9AlcnbQxSNbJkgomdw4g";

    public static String DevelopmentAppSecretKeyForCoreySurgDev = "yB5uM01hRxKR9j2pBoHxEw";

    public static String DevelopmentAppKeyForCoreySurgDev = "0OhxKJusT0SKCL7ip1KOBA";

    public static String DevelopmentAppKeyForCoreySurgCtx = "mziMIDm3T42s_gnxakSIrA";

    public static String DevelopmentAppSecretKeyForCoreySurgCtx = "-MRs-xL9S-mkdHgUaxBhhg";


    /*
     * The application name are used in SignActivity.java and these are passed
     * to the server
     */

    public static String AppNameCoreySurgDevelopment = "SURG2";

    public static String AppNameCoreySurgProduction = "SURG";

    public static String AppNameCoreyHealthProduction = "HEALTH";


}
