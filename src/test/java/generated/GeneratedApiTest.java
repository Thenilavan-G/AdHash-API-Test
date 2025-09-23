package generated;

import org.testng.annotations.Test;
import base.ApiBase;
import utils.HttpClient;

public class GeneratedApiTest extends ApiBase {

    @Test
    public void pm_AdHash_Website() {
        // Source: AdHash - Website
        HttpClient.get("https://adhashtech.com/", 200);
    }


    @Test
    public void pm_AutoChecker_Login_UI() {
        // Source: AutoChecker_Login_UI
        HttpClient.get("https://manage.autochecker.com/", 200);
    }


    @Test
    public void pm_AutoChecker_Admin_Login() {
        // Source: AutoChecker_Admin_Login
        HttpClient.post("https://manageadminapi.autochecker.com/adminapi/auth/login",
                       "{\"key\":\"qYkPMqAmQziJ2/ha1YnKS6xxJOJ7gynA4+DtvRPHnPpm3mrqpgjLhZRUg1446ixEOcORY30MpQ8o0h+Kf9cyVqxk/I5LMYc9qhLtyAWZcco=\"}",
                       201);
    }


    @Test
    public void pm_MHAI_Doctor_SuperAdmin_Login_UI() {
        // Source: MHAI_Doctor_SuperAdmin_Login_UI
        HttpClient.get("https://admin.myhealthai.io/", 200);
    }


    @Test
    public void pm_MHAI_Doctor_SuperAdmin_Login() {
        // Source: MHAI_Doctor_SuperAdmin_Login
        HttpClient.get("https://participantscrm.myhealthccm.com/", 200);
    }


    @Test
    public void pm_MHCCM_PAR_CRM_Login() {
        // Source: MHCCM_PAR_CRM_Login
        HttpClient.get("https://admin.autoevantage.com/", 200);
    }


    @Test
    public void pm_AutoeVantage_Analytics_Login_PhoneNumber() {
        // Source: AutoeVantage_Analytics_Login_PhoneNumber
        HttpClient.post("https://identitytoolkit.googleapis.com/v1/accounts:sendVerificationCode?key=AIzaSyBWHkUDpvKpigYPy2cpdfMHWOWiUl0CF2g", "", 200);
    }


    @Test
    public void pm_AutoeVantage_Analytics_Login_OTP() {
        // Source: AutoeVantage_Analytics_Login_OTP
        HttpClient.post("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPhoneNumber?key=AIzaSyBWHkUDpvKpigYPy2cpdfMHWOWiUl0CF2g", "", 200);
    }


    @Test
    public void pm_Metcalf_UI() {
        // Source: Metcalf_UI
        HttpClient.get("https://metcalf.ai/", 200);
    }

}
