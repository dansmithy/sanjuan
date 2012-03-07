package com.github.dansmithy;

import com.github.dansmithy.config.JunitConfiguration;
import com.github.dansmithy.driver.DefaultValues;
import com.github.dansmithy.util.ATUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.UUID;

import static com.github.restdriver.serverdriver.RestServerDriver.get;


/**
 * Run a Fake Twitter service for faking logins
 */
public class FakeTwitterStart extends HttpServlet {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(FakeTwitterStart.class);
    
    private static final String BASE_URI_KEY = "baseUri";

    private static final String SAN_JUAN_BASE_URL = new JunitConfiguration().getProperty(BASE_URI_KEY,
            DefaultValues.BASE_URI);

    private static final String FAKE_TOKEN_SECRET = "secret";
    private static final String FAKE_ACCESS_TOKEN = "access_token";
    private static final String FAKE_USER_ID = "user_id";

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String path = req.getPathInfo();

        if ("/request_token".equals(path) && "POST".equals(req.getMethod())) {
            String fakeUsername = String.format("fake-%s", UUID.randomUUID().toString());
            String requestTokenReponseBody = String.format("oauth_token=%s&oauth_token_secret=%s&oauth_callback_confirmed=true", fakeUsername, FAKE_TOKEN_SECRET);
            sendResponse(resp, HttpURLConnection.HTTP_OK, requestTokenReponseBody);
        } else if ("/authenticate".equals(path) && "GET".equals(req.getMethod())) {
            String fakeUsername = req.getParameter("oauth_token");
            sendResponse(resp, HttpURLConnection.HTTP_OK, generateHtml(fakeUsername));
        } else if ("/access_token".equals(path) && "POST".equals(req.getMethod())){
            String header = req.getHeader("Authorization");
            LOGGER.info(String.format("Header is: [%s]", header));
            String fakeUsername = extractUsername(header);
            String accessTokenReponseBody = String.format("oauth_token=%s&oauth_token_secret=%s&user_id=%s&screen_name=%s", FAKE_ACCESS_TOKEN, FAKE_TOKEN_SECRET, FAKE_USER_ID, fakeUsername);
            sendResponse(resp, HttpURLConnection.HTTP_OK, accessTokenReponseBody);
        } else {
            sendResponse(resp, HttpURLConnection.HTTP_BAD_REQUEST, String.format("Failed to handle [%s] request to [%s]", req.getMethod(), req.getRequestURL().toString()));
        }
    }
    
    private String generateHtml(String defaultFakeUsername) {
        String html = String.format("<html></head><body><form action=\"%s/ws/auth/authValidate\" method=\"GET\"><input type=\"hidden\" name=\"oauth_token\" value=\"%s\" /><input type=\"text\" size=\"100\" name=\"oauth_verifier\" value=\"%s\" /><input type=\"submit\" name=\"Submit\" /></form></body></html>", SAN_JUAN_BASE_URL, defaultFakeUsername, defaultFakeUsername);
        return html;
    }

    private String extractUsername(String header) {
        String startKey = "oauth_verifier=\"";
        String endKey = "\"";
        int start = header.indexOf(startKey);
        if (start == -1) {
            return "unknown";
        }
        start += startKey.length();
        int end = header.indexOf(endKey, start);
        if (end == -1) {
            return "unknown";
        }
        return header.substring(start, end);
    }

    private void sendResponse(HttpServletResponse resp, int code, String body) throws IOException {
        resp.setStatus(code);
        BufferedWriter bw = new BufferedWriter(resp.getWriter());
        bw.write(body);
        bw.flush();
    }

    public static void main(String[] args) throws Exception  {
        String twitterBaseUrl = get(SAN_JUAN_BASE_URL + "/ws/admin/twitter.baseurl").getContent();
        int port = ATUtils.extractRestDriverPort(twitterBaseUrl);
        Server server = new Server(port);

        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(FakeTwitterStart.class, "/oauth/*");
        server.setHandler(handler);
        server.start();
        server.join();
    }
    
}
