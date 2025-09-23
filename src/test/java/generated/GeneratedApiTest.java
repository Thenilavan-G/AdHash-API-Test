package generated;

import org.testng.annotations.Test;

import base.ApiBase;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;

public class GeneratedApiTest extends ApiBase {

    @Test
    public void pm_AdHash_Website() {
        // Source: AdHash - Website
        given()
            .when()
            .request("GET", "https://adhashtech.com/")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_AutoChecker_Login_UI() {
        // Source: AutoChecker_Login_UI
        given()
            .when()
            .request("GET", "https://manage.autochecker.com/")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_AutoChecker_Admin_Login() {
        // Source: AutoChecker_Admin_Login
        given()
            .body("{\"key\":\"qYkPMqAmQziJ2/ha1YnKS6xxJOJ7gynA4+DtvRPHnPpm3mrqpgjLhZRUg1446ixEOcORY30MpQ8o0h+Kf9cyVqxk/I5LMYc9qhLtyAWZcco=\"}")
            .when()
            .request("POST", "https://manageadminapi.autochecker.com/adminapi/auth/login")
            .then()
                .statusCode(201);
    }


    @Test
    public void pm_MHAI_Doctor_SuperAdmin_Login_UI() {
        // Source: MHAI_Doctor_SuperAdmin_Login_UI
        given()
            .when()
            .request("GET", "https://admin.myhealthai.io/")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_MHAI_Doctor_SuperAdmin_Login() {
        // Source: MHAI_Doctor_SuperAdmin_Login
        given()
            .body("{\"Key\":\"N9GtP6iO2aCc81CiAKuxU2neyE2PEcaDUpjcAhsju93rOShrJvPYn8qBaLybJamYyy/25C1knR3ljl9WqrdMF3xmGUOASX6Hluc6gzLiaW4=\"}")
            .when()
            .request("POST", "https://admin.myhealthai.io/api/login/login")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_MHAI_Doctor_Login_UI() {
        // Source: MHAI_Doctor_Login_UI
        given()
            .when()
            .request("GET", "https://doctor.myhealthai.io/")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_MHAI_Doctor_Login() {
        // Source: MHAI_Doctor_Login
        given()
            .body("{\"Key\":\"TxVnM0rMfKSffcdTzuPXapnc3yEkNOt9oIqJrc6DYNYrIGbXR7pipTUEW5HVC6W9tOgiPEsdGNXpIGOrPDbHx8x1fjmGmEfxbDdfiw1EtCQ=\"}")
            .when()
            .request("POST", "https://admin.myhealthai.io/api/login/physicianlogin")
            .then()
                .statusCode(200);
    }

    @Test
    public void pm_KCAI_Doctor_SuperAdmin_Login_UI() {
        // Source: KCAI_Doctor_SuperAdmin_Login_UI
        given()
            .when()
            .request("GET", "https://admin.kaicare.ai/")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_KCAI_Doctor_SuperAdmin_Login() {
        // Source: KCAI_Doctor_SuperAdmin_Login
        given()
            .body("{\"Key\":\"N9GtP6iO2aCc81CiAKuxU2neyE2PEcaDUpjcAhsju93rOShrJvPYn8qBaLybJamYyy/25C1knR3ljl9WqrdMF3xmGUOASX6Hluc6gzLiaW4=\"}")
            .when()
            .request("POST", "https://adminapi.kaicare.ai/api/login/login")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_KCAI_Doctor_Login_UI() {
        // Source: KCAI_Doctor_Login_UI
        given()
            .when()
            .request("GET", "https://doctor.kaicare.ai/")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_KCAI_Doctor_Login() {
        // Source: KCAI_Doctor_Login
        given()
            .body("{\"Key\":\"TxVnM0rMfKSffcdTzuPXapnc3yEkNOt9oIqJrc6DYNYrIGbXR7pipTUEW5HVC6W9tOgiPEsdGNXpIGOrPDbHx8x1fjmGmEfxbDdfiw1EtCQ=\"}")
            .when()
            .request("POST", "https://adminapi.kaicare.ai/api/login/physicianlogin")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_MHCRM_SuperAdmin_Login_UI() {
        // Source: MHCRM_SuperAdmin_Login_UI
        given()
            .when()
            .request("GET", "https://crm.myhealthccm.com/")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_MHCRM_SuperAdmin_Login() {
        // Source: MHCRM_SuperAdmin_Login
        given()
            .body("{\"Key\":\"yAr5miO2F27FnwE5UyVdp29c1FQEozkzZR8M846rQRaFpmYKP2qa2MJHqYxH2ImnpVddr+7hPnjho54VWARakAAetFTEAzVY8p4SDzQvsds=\"}")
            .when()
            .request("POST", "https://crmapi.myhealthccm.com/api/login/CrmLogin")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_MHCRM_Admin_Login() {
        // Source: MHCRM_Admin_Login
        given()
            .body("{\"Key\":\"TxVnM0rMfKSffcdTzuPXaiNlWsGttrdva0wgp+zwFcBpL/3zNvc8NJa0Jl6w+GseK+zzvQOwfay7dJELZ0jfRqRzsXxPBv8D2ScGWdnEbeY=\"}")
            .when()
            .request("POST", "https://crmapi.myhealthccm.com/api/login/CrmLogin")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_MHCCM_SuperAdmin_Login_UI() {
        // Source: MHCCM_SuperAdmin_Login_UI
        given()
            .when()
            .request("GET", "https://admin.myhealthccm.com/")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_MHCCM_SuperAdmin_Login() {
        // Source: MHCCM_SuperAdmin_Login
        given()
            .body("{\"Key\":\"GDGlSs32Lk4FJ1HngGqCK8oa3klvfpe8M96Pt2z4J9T7CU1SzGhgyiJFvjOc8yGllo3aho68UAxWkVgytpByhMI5atc0RkX0/10g5Dtwu0k=\"}")
            .when()
            .request("POST", "https://admin.myhealthccm.com/api/login/login")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_MHCCM_CPA_Login_UI() {
        // Source: MHCCM_CPA_Login_UI
        given()
            .when()
            .request("GET", "https://cpa.myhealthccm.com/")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_MHCCM_CPA_Login() {
        // Source: MHCCM_CPA_Login
        given()
            .body("{\"Key\":\"MN2pMMfSMQF9kOMUf7ddgfSVyuevc/0CFRX4t66QqkNXXqzYLm2hVgF3jPaHKiR/82vAPDR0duET755v78+0iGCj6rFZSxyYo04pabQqd4o=\"}")
            .when()
            .request("POST", "https://admin.myhealthccm.com/api/login/physicianlogin")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_MHCCM_PAR_Login_UI() {
        // Source: MHCCM_PAR_Login_UI
        given()
            .when()
            .request("GET", "https://participants.myhealthccm.com/")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_MHCCM_PAR_Login() {
        // Source: MHCCM_PAR_Login
        given()
            .body("{\"Key\":\"GDGlSs32Lk4FJ1HngGqCK9wTs1cxYUBU1n5yxGCKOWHpis8Zze0d5qzHEs7H3/9joA/08MhFyd6IR1LRTXHTHPzVBllaGQdW89Bw4YT1Q3qdQdCzYgZk+OmMV80kyYF+\"}")
            .when()
            .request("POST", "https://admin.myhealthccm.com/api/login/physicianlogin")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_MHCCM_Form_UI() {
        // Source: MHCCM_Form_UI
        given()
            .when()
            .request("GET", "https://forms.myhealthccm.com/")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_MHCCM_PAR_CRM_Login_UI() {
        // Source: MHCCM_PAR_CRM_Login_UI
        given()
            .body("{\"Key\":\"GDGlSs32Lk4FJ1HngGqCK9wTs1cxYUBU1n5yxGCKOWHpis8Zze0d5qzHEs7H3/9joA/08MhFyd6IR1LRTXHTHPzVBllaGQdW89Bw4YT1Q3qdQdCzYgZk+OmMV80kyYF+\"}")
            .when()
            .request("GET", "https://participantscrm.myhealthccm.com/")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_MHCCM_PAR_CRM_Login() {
        // Source: MHCCM_PAR_CRM_Login
        given()
            .body("{\"Key\":\"7Yo1tYhqSa9D/Gn5YLP6Z0oYu9QkEVQD/Rp89jdPIB6Ak3hzvqbFOR7kn+rzJhgnwnZOQSrCJC7505wLL4b4ECanShCB4oN79hFwLZrRZJk=\"}")
            .when()
            .request("POST", "https://participantscrmapi.myhealthccm.com/api/login/CrmLogin")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_MHRTM_SuperAdmin_Login_UI() {
        // Source: MHRTM_SuperAdmin_Login_UI
        given()
            .when()
            .request("GET", "https://admin.myhealthrtm.com/")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_MHRTM_SuperAdmin_Login() {
        // Source: MHRTM_SuperAdmin_Login
        given()
            .body("{\"Key\":\"RW32XVi2fmeTnHcTvVeoYX7buVVNidMwsXLrXkiNvA0Fhd/LyqK7Ups13jOi7K7qKy4jy7IsAOCwmWM6Z7gpjlIaUmZ4p28UHL2xjSN1R2Q=\"}")
            .when()
            .request("POST", "https://adminapi.myhealthrtm.com/api/login/physicianlogin")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_MHRTM_CPA_Login_UI() {
        // Source: MHRTM_CPA_Login_UI
        given()
            .when()
            .request("GET", "https://cpa.myhealthrtm.com/")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_MHRTM_CPA_Login() {
        // Source: MHRTM_CPA_Login
        given()
            .body("{\"Key\":\"yYG/1qu16r0OJRo3VqIfIdZuHjCy6OVVvEYwyHQB0zqgt4kGynoFN/M6byn1IxJnXpgALiMbGTnakwVXy8xz1BJ27UhUdiXnC9tyKbQV2Zg=\"}")
            .when()
            .request("POST", "https://adminapi.myhealthrtm.com/api/login/physicianlogin")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_MHRTM_Participant_Login_UI() {
        // Source: MHRTM_Participant_Login_UI
        given()
            .when()
            .request("GET", "https://participant.myhealthrtm.com/")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_MHRTM_Participant_Login() {
        // Source: MHRTM_Participant_Login
        given()
            .body("{\"Key\":\"QVlzRZUxA0wyWnhC5XiuKS94Y7ckPp19Durv2JVJlwgj7bB98LxN1JPmOOxTW7w+KvPrrhH78c7LREsuXUvyniQ8t5ZZKGXDinuq8rfvrguuDKoBESpb+p/MNZOuFpP9\"}")
            .when()
            .request("POST", "https://adminapi.myhealthrtm.com/api/login/physicianlogin")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_MHAI_Patient_App_Login() {
        // Source: MHAI_Patient_App_Login
        given()
            .body("{\"type\" : \"daUHsM4u2R/esmeev1wtyRDPTqx73UltFNRViWkskfdbxXCxVKfm85tkqan8z+i5HeIHKDJktT1pt0n5ZexZmt0Q+gcXMVYRpzKLQpKzUwMxOMnD1mIfEu9n8QjK54nV6dh9O7P15yYZyXhUDT7rxkGAdUUULGYL/QNfLm5kZf4DyHh0Id7CgKZLixMkrdqbSgjhr3GC1KESDSLY4tspFjQTGErz0J5RpU7RdQd/Zumqo/CUtPnXfDMCFsytJ5a8o4lqhim8MeFmk+oiCf4fTvsQAYGO/cq87lJmpVj4XvUenZ00nSjxS0atHfAhLiegLDMDH7z0mO+mqHSnnZfqoALLYJUSOnJnIde/OuHBJ6T3eWa6ZVXvKei4M5g0/qJovs74su9NAEVhjSL9INF9AScyBisDRpc5gjfnLL54Nq/mEwuJbnQi82d9suF5CirUkp663hpMT5koTI2tA6/WLDqQ7BrXxXfHfPM8UPWFwBstcpCZ4ow4EN1vQHF+lJM6hJapD3dz6QVtCx/DtfD278HUtkrCoK9GQibkNPKNEx2IJgFpNZo31mOWxHQDxXYIWxzZnjFmGF+6Afd9BsI4Eg==\"}")
            .when()
            .request("POST", "https://mobileapi.myhealthai.io/api/Login")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_KCAI_Patient_App_Login() {
        // Source: KCAI_Patient_App_Login
        given()
            .body("{\"type\" : \"daUHsM4u2R/esmeev1wtyRDPTqx73UltFNRViWkskfdbxXCxVKfm85tkqan8z+i5HeIHKDJktT1pt0n5ZexZmt0Q+gcXMVYRpzKLQpKzUwMxOMnD1mIfEu9n8QjK54nV6dh9O7P15yYZyXhUDT7rxkGAdUUULGYL/QNfLm5kZf4DyHh0Id7CgKZLixMkrdqbSgjhr3GC1KESDSLY4tspFjQTGErz0J5RpU7RdQd/Zumqo/CUtPnXfDMCFsytJ5a8o4lqhim8MeFmk+oiCf4fTvsQAYGO/cq87lJmpVj4XvUenZ00nSjxS0atHfAhLiegLDMDH7z0mO+mqHSnnZfqoALLYJUSOnJnIde/OuHBJ6T3eWa6ZVXvKei4M5g0/qJovs74su9NAEVhjSL9INF9AScyBisDRpc5gjfnLL54Nq/mEwuJbnQi82d9suF5CirUkp663hpMT5koTI2tA6/WLDqQ7BrXxXfHfPM8UPWFwBstcpCZ4ow4EN1vQHF+lJM6hJapD3dz6QVtCx/DtfD278HUtkrCoK9GQibkNPKNEx2IJgFpNZo31mOWxHQDxXYIWxzZnjFmGF+6Afd9BsI4Eg==\"}")
            .when()
            .request("POST", "https://mobileapi.kaicare.ai/api/Login")
            .then()
                .statusCode(200);
    }

        @Test
    public void pm_AutoChecker_App_Login() {
        // Source: AutoChecker_App_Login
        given()
            .body(" {\"key\":\"o7obaAPl8Rin1yQil/fPLmfZDWV31ERB6O+us/ZdZlwAmt3brpIPLVi8W2ASbZ9dzes3tBTEjYwcayoLQ316BQ\\u003d\\u003d\"}")
            .when()
            .request("POST", "https://managemobileapi.autochecker.com/api/auth/login")
            .then()
                .statusCode(201);
    }


    @Test
    public void pm_Wavedin_App_Login() {
        // Source: Wavedin_App_Login
        given()
            .body("{\"key\":\"Z6bJc+TJLSyk2k2zcjRDzB1b2SUMLqPMDJ7onRlphDDf9wwCF941EGsE928j/4PLQs6wcVXsDfQsyKWO6thxZvIflmVvWtgGAe2UT/c5on37m45Ge5BUDL3PnbKo774KRf4q1kM8GoJxF5nVeqF8C5pBVwwdKkFK5MyLPqTyzlCHof+IKxJOrMRFCe+HNPsuRdkf6rxr56sMhXrPQu8OP7VMlDa89QIrl5D4zYnb1msDUgfKvjl0gVX7AkZnUMZUE5FemJnx7sS1JggiuM5lEQVKivjgElAaiFlLhfSenmUlUdL037feg6oIRCwEhMxkYr3H9CXt7VRpwfB6TBElyvdftVi3cUyNV+l1l/uMzbM1/0gj2n0qntbeWti+vwhJwV7rHKJyxST7vDSkWC4rggTEabs9gkg7x3fMKGb58t1oO4MgCdku8Gou9TPEVyNbSZ7skRONiCE55ZUkwU/tYljSFvT/ljQF6cDLibuKHW6sk4j/ZtP0jN/MtHJUozkOLVEOzt+ZXjip++f7SRokIOHdmJ5jsU/FHgfVNy3h7LuBgdBMtiY8MqmRIxFyueoL9n1FyopW5yluQnUk6ok09Z8/4V5yVsfe7e6HszOYFgScgzmQcuDiegp4pSXDYx25EleDhYtW0SLi5fnXOKPrxo0qBmjlhhOY/XNPNEamlxM\\u003d\"}")
            .when()
            .request("POST", "https://mobileliveapi.wavedin.app/api/auth/login")
            .then()
                .statusCode(201);
    }


    @Test
    public void pm_Sparkme_App_Login() {
        // Source: Sparkme_App_Login
        given()
            .body("{\"key\" : \"z8zY9agO/zG58XoDO6QW+mkPueQVBAGXwPCbsyAIPxmbvDYcouZ/ssu6jpPMct47MGerqC9we10NCiVzoijbNwohTjUh+ozT0NUPmK4s2hpDCXxBlqitk2TpfqwxEL2at+DW8bPJ69BBSnbG/PRM3VItB8USK2N5iBGiHaOG1ngUdOzDFJT3FU//ZQ1ATW8BBEUtJ9rGIQ2+Uqg/0f/tVk9+yGQQyITObqIPXWvIQ+AMxs/0XNELfe90Ou+D5eMuGwH3ZiMe8SpCU9Uo0ZXdXC+me/xjlyEu1uXduyTcYV2ZP9+Qe7pZgSHJrpUHsgLFVEVcZp89DQG9VeVcz8P8zwXLumfIaTacMLPWzl+j1XBWMLD2cBq4gGvVNbPKTl/OwnDMgYz0XYBR5E6VPbDk6zjAT0cns14L/vhuwIAorVmTX2fbfvDWWIQD8zy4A0vNtPe206zYNMLwWRHkonvEvpqxvL4Ri2gfjN1kV7kwxwjYOwQ+vAkfTZpEbH2CHVTVsCyKVNfGphZDdQYECe0UzwlKTdxr/jrVno4V8kRschF6E8jwXlukeNpdryN/zAdRFkRZSGbHIbIwIScwmsmBIKkbYRt9Bzrl4Xn3ksKwVRM6zh66z9YSmksvwkTmk9vQ\"}")
            .when()
            .request("POST", "https://spark.synctag.com/api/auth/login")
            .then()
                .statusCode(201);
    }


    @Test
    public void pm_WavedIn_SuperAdmin_Login_UI() {
        // Source: WavedIn_SuperAdmin_Login_UI
        given()
            .when()
            .request("GET", "https://superadmin.wavedin.app/")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_WavedIn_Market_Web() {
        // Source: WavedIn_Market_Web
        given()
            .when()
            .request("GET", "https://market.wavedin.app/")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_Sparkme_Web_Login_UI() {
        // Source: Sparkme_Web_Login_UI
        given()
            .when()
            .request("GET", "https://sparkanalytics.synctag.com/")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_Sparkme_Web_Login() {
        // Source: Sparkme_Web_Login
        given()
            .body("{\"phone\":9999999999,\"password\":\"Synctag@1\"}")
            .when()
            .request("POST", "https://spark.synctag.com/analyticsapi/auth/login")
            .then()
                .statusCode(201);
    }


    @Test
    public void pm_Algomax_ProSky_Web_Login_UI() {
        // Source: Algomax_ProSky_Web_Login_UI
        given()
            .when()
            .request("GET", "https://prosky.algomax.co")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_Algomax_GoPocket_Web_Login_UI() {
        // Source: Algomax_GoPocket_Web_Login_UI
        given()
            .when()
            .request("GET", "https://gopocket.algomax.co")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_LGM_Login_UI() {
        // Source: LGM_Login_UI
        given()
            .when()
            .request("GET", "https://zapcrm.ai/")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_LGM_Login_PhoneNumber() {
        // Source: LGM_Login_PhoneNumber
        given()
            .urlEncodingEnabled(false)   // <-- important: keep the ":" in the path
            .contentType(ContentType.JSON)
            .queryParam("key", "AIzaSyA02OjHzzCXgEnDBC1vS4zFdOSgs2HJzxg")
            .body("{\"phoneNumber\":\"+19797979797\",\"recaptchaToken\":\"03AFcWeA77vmZhwRUOfb_sH0XJKHW-LcpLj3YtOmuy-v3RbefeQuT3PEJJ8710E4Syv93gUjR79y0vMYBI4Jt0dPUAoSdyhXKKGlbRCP2G8nyt8FKqhWWjJBPEZ-MsFlOLn1thGbxg8OEVKWrEDsgd0ORMvr76NSlTo0JEN5pHUei1wMqXEIfqd21yZEYRW5HazP7GOpGN4yD2YX8RbYBEJCWlgDibFfHdZ-AVkOecz76Kz1r3rkbJhsEc_ppewbPPZdJsedrNBDuDDxiVZCS4q6SkyY58gwMeE3Pn87iBwn7xXtizrIOyljgi0sEeuZzoH6RSgkCsJMM0EM03fz0Ar3AeprDklmc5vuAHYvM3rhHnY77cGWvUJTz0DWpyntcPX1ceLh6fe9EzkOhVDlslJYpep9TOYcDSs-GlI93931_ef7w5gERXnmBpyQdb1IJGvZoDYJA1hW-qSRm5xz0UzhBGjRlILFkELCgSqozpDjlYPSz3cnhxJUTFSF-3DZiWSaiEa2QjLHTe-EYepl1QJ7oFfx4nFuwgYglyq-WQoV1yTeiNbc3asAW786DizxHu5mjBO9gORt6wCjadE9zYSUV-w9yntECJDlrjiCfCUZ99fUCkrFyucwu6NxSR3N5DX-JtADZBv5qX1u4Oy0lYj4PA8q5UeRRZQX2mEIaEBmmpqiT2-xC7PamwLuubnXCGbOVVoApcu-3W5ETl1GrfUvXHTEjiuoPGVM0UfrzVASvGaRoBqYeoDajpdn5x5ykXoS6X0IQw_xBFckNiLUx_GixZCc2PZoB9J3iTrccqBUgKl_T57mQYEKcIRZei7GT3sk01sGavYYf0KJwaIWZOIZ5Hr7834ocuvR8gvD658Szrs0PU0MFhroN4qowAHTEI0wDCsLFKxVpCLwLiucPr5-nF9_4TEUck3A\"}")
            .when()
            .request("POST", "https://identitytoolkit.googleapis.com/v1/accounts:sendVerificationCode?key=AIzaSyA02OjHzzCXgEnDBC1vS4zFdOSgs2HJzxg")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_LGM_Login_OTP() {
        // Source: LGM_Login_OTP
        given()
            .urlEncodingEnabled(false)   // <-- important: keep the ":" in the path
            .contentType(ContentType.JSON)
            .queryParam("key", "AIzaSyA02OjHzzCXgEnDBC1vS4zFdOSgs2HJzxg")
            .body("{\"sessionInfo\":\"AD8T5IsoLZpoB51iVvQEznTTlwOTZrKQGar3DyYBuvtjjrh7I9gwUEQum-6afhbHWPQ9kH_zlMnZUCgS9k3GW1iS8w700AIC-rfQyA5k18Ll3eJf26VrEE4GxZrT9n3HO87Mbt_Xro98\",\"code\":\"123456\"}")
            .when()
            .request("POST", "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPhoneNumber?key=AIzaSyA02OjHzzCXgEnDBC1vS4zFdOSgs2HJzxg")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_AskEPI_Login_UI() {
        // Source: AskEPI_Login_UI
        given()
            .when()
            .request("GET", "https://askepi.ai/")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_AskEPI_Login_PhoneNumber() {
        // Source: AskEPI_Login_PhoneNumber
        given()
            .urlEncodingEnabled(false)   // <-- important: keep the ":" in the path
            .contentType(ContentType.JSON)
            .body("{\"key\":\"fUjZB3a/IJyX6AyCgxVZ4m0d7ldlQbaFKSVspmarCFDZaT5lO/yIVd83upxkcfXG\"}")
            .when()
            .request("POST", "https://eadgapi.myhealthai.io/ai/user/checkUser")
            .then()
                .statusCode(200);
    }


    @Test
    public void pm_AskEPI_Login_OTP() {
        given()
            .urlEncodingEnabled(false)   // <-- important: keep the ":" in the path
            .contentType(ContentType.JSON)
            .queryParam("key", "AIzaSyA02OjHzzCXgEnDBC1vS4zFdOSgs2HJzxg")
            .body("{\"sessionInfo\":\"AD8T5Itr8_jrpfhXAEBpwReQLHTHsicQvRko8Tq4kvfYxAJCqd2LO-74XxCereN6G7o0EKDW1XOA4PxlpndKbEz5ulr--Vxqw2foeLf3NffNYW8pogxERmF8m969flkDc8rmiz0zoGyg\",\"code\":\"123456\"}")
        .when()
            .post("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPhoneNumber")
        .then()
            .statusCode(200);
    }

}
