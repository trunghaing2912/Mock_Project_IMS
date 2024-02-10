package fa.training.fjb04.ims.controller.login;

import jakarta.servlet.http.HttpServletRequest;

public class Utility {

    public static String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(),"");
    }
}
