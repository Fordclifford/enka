package org.mifos.mobilebanking.api;

/**
 * @author Vishwajeet
 * @since 09/06/16
 */

public class BaseURL {
    
    //new apis
    
    public static final String API_ENDPOINT = "techsavanna.net:8444";
    public static final String API_PATH = "/uip/api/v1/self/";
    public static final String PROTOCOL_HTTPS = "https://" ;
    public static final String MY_TIME = "status";
    public static final String LIMIT_API = "https://techsavanna.net:8181/mifos/pull_loanlimit.php";
    public static final String TITLE_API = "http://techsavanna.technology/enkacrm/api/?phone_no=";
    public static final String NOTIFICATION_URL = "https://techsavanna.net:8181/enka/notify.php";
    public static final String CRB_URL = "https://techsavanna.net:7000/call";
    public static final String LOAN_URL = "https://techsavanna.net:8181/enka/api/rates.php";
    public static final String SERVER_URLL="https://techsavanna.net:8181/mifos/";

    public static final String CREATE_CLIENT_URL="https://techsavanna.net:8181/enka/api/index.php";

    public static final String CREATE_RESET_CODE="https://techsavanna.net:8181/enka/api/resetCode.php";

    public static final String CREATE_CONFIRM_CODE="https://techsavanna.net:8181/enka/api/confirmCode.php";

    public static final String CREATE_NEW_PASSWORD="https://techsavanna.net:8181/enka/api/newPassword.php";
    
    public static final String CHECK_SUBSCRIPTION="https://techsavanna.net:8181/enka/api/registered.php";

    public static final String SAVE_IMEI="https://techsavanna.net:8181/enka/api/saveimei.php";
    
    public static final String AUTH = "https://techsavanna.net:8181/enka/api/authenticate.php";
    
    private String url;
    
    public String getUrl() {
        if (url == null) {
            return PROTOCOL_HTTPS + API_ENDPOINT + API_PATH;
        }
        return url;
    }
    
    public String getDefaultBaseUrl() {
        return PROTOCOL_HTTPS + API_ENDPOINT;
    }
    
    public String getUrl(String endpoint) {
        return endpoint + API_PATH;
    }
}
