package org.wso2.carbon.identity.sso.saml.servlet;


import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.sso.saml.javascript.flow.WaitStatusResponse;
import org.wso2.carbon.identity.sso.saml.model.WaitStatus;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 * Component class for implementing the Biometric endpoint.
 */
public class SAMLBiometricServlet extends HttpServlet {
    private static final Log log = LogFactory.getLog(SAMLBiometricServlet.class);


    boolean value1 = false;

    private HashMap<String, String> updateStatus = new HashMap<>();


    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

        WaitStatusResponse waitResponse = null;
        if (request.getParameterMap().containsKey("t")) {
            String para1 = request.getParameter("t");

            waitResponse = new WaitStatusResponse();

            if (para1.equals("mobile")) {

                if ((request.getParameterMap().containsKey("SDKMobile")) &&
                        request.getParameterMap().containsKey("CHALLENGEMobile")) {
                    String sdkmobile = request.getParameter("SDKMobile");
                    String challengeMobile = request.getParameter("CHALLENGEMobile");
                    String consentMobile = request.getQueryString();

                    log.info("challenge mobile parameter : " + challengeMobile);
                    log.info("sdk mobile parameter : " + sdkmobile);
                    log.info("consent mobile  is : " + consentMobile);
                    updateStatus.put(sdkmobile, challengeMobile);
                    log.info("table is: " + updateStatus);
                    response.setContentType("text/html");
                    response.setStatus(200);
                    PrintWriter out = response.getWriter();
                    log.info("<h3>recieved the mobile SDK and challenge !</h3>");

                    return;
                }
            } else if (para1.equals("web")) {


                if (request.getParameterMap().containsKey("SDKWeb")) {
                    String sdkweb = request.getParameter("SDKWeb");
                    String challengeExtracted = updateStatus.get(sdkweb);
                    if (challengeExtracted != null && updateStatus.containsKey(sdkweb)) {

                        response.setContentType("text/html");
                        response.setCharacterEncoding("utf-8");
                        response.setStatus(200);
                        request.setAttribute("signedChallenge", challengeExtracted);

                        waitResponse.setStatus(WaitStatus.Status.COMPLETED1.name());
                        waitResponse.setChallenge(challengeExtracted);
                        updateStatus.remove(sdkweb);
                        log.info("refined table : " + updateStatus);
                        log.info("challenge extracted is : " + challengeExtracted);
                        log.info("a response from the mobile device!!!");


                    } else {
                        response.setContentType("text/html");
                        response.setStatus(401);
                        response.sendError(401, "blah blah ");
                    }
                }
            }
        } else {
            response.setContentType("text/html");
            response.setStatus(400);
            PrintWriter out = response.getWriter();
            out.println("<h3>Invalid request... !</h3>");
            log.info("<Please wait until the authorization process is over... !</h3>");
        }

        response.setContentType("application/json");
        String json = new Gson().toJson(waitResponse);
        try (PrintWriter out = response.getWriter()) {
            if (json.contains("status")) {
                log.info("json waitresponse: " + json);
            }
            out.print(json);
            out.flush();
        }
    }
}
