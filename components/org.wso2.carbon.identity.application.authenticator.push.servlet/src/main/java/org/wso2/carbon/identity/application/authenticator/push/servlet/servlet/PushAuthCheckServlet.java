package org.wso2.carbon.identity.application.authenticator.push.servlet.servlet;

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authentication.framework.inbound.InboundConstants;
import org.wso2.carbon.identity.application.authenticator.push.servlet.PushServletConstants;
import org.wso2.carbon.identity.application.authenticator.push.servlet.model.WaitStatus;
import org.wso2.carbon.identity.application.authenticator.push.servlet.store.PushDataStore;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet for handling the status checks for authentication requests from the push authenticator wait page.
 */
public class PushAuthCheckServlet extends HttpServlet {

    private static final Log log = LogFactory.getLog(PushAuthCheckServlet.class);
    private static final long serialVersionUID = -913670970043040923L;
    private final PushDataStore pushDataStoreInstance = PushDataStore.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!(request.getParameterMap().containsKey(InboundConstants.RequestProcessor.CONTEXT_KEY))) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);

            if (log.isDebugEnabled()) {
                log.debug(PushServletConstants.ErrorMessages.ERROR_CODE_WEB_SESSION_DATA_KEY_NOT_FOUND.toString());
            }

        } else {
            handleWebResponse(request, response);
        }
    }

    /**
     * Handles requests received from the wait page to check the authentication status.
     *
     * @param request  HTTP request
     * @param response HTTP response
     * @throws IOException
     */
    private void handleWebResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {

        WaitStatus waitStatus = new WaitStatus();

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(PushServletConstants.MEDIA_TYPE_JSON);

        String sessionDataKeyWeb = request.getParameter(InboundConstants.RequestProcessor.CONTEXT_KEY);
        String status = pushDataStoreInstance.getAuthStatus(sessionDataKeyWeb);
        if (status == null) {
            waitStatus.setStatus(PushServletConstants.Status.PENDING.name());

            if (log.isDebugEnabled()) {
                log.debug("Mobile authentication response has not been received yet.");
            }
        } else if (status.equals(PushServletConstants.Status.COMPLETED.name())) {
            waitStatus.setStatus(PushServletConstants.Status.COMPLETED.name());
            pushDataStoreInstance.removePushData(sessionDataKeyWeb);

            if (log.isDebugEnabled()) {
                log.debug("Mobile authentication has been received. Proceeding to authenticate.");
            }
        }

        String waitResponse = new Gson().toJson(waitStatus);
        PrintWriter out = response.getWriter();
        out.print(waitResponse);
        out.flush();
        out.close();
    }
}
