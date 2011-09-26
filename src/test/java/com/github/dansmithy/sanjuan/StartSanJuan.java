package com.github.dansmithy.sanjuan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * Debug class used for running jetty within Eclipse.
 */
public class StartSanJuan {

	private static Logger logger = Logger.getLogger(StartSanJuan.class);

    /**
     * Main function, starts the jetty server. Puts data into
     * the db.
     * @param args  contextPath, war
     */
    public static void main(String[] args)  {

        final Server server = new Server();
        server.setStopAtShutdown(true);
        server.setGracefulShutdown(1000); // 1 second


        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(8086);
        server.setConnectors(new Connector[] { connector });

        logger.info("Starting Jetty");
        WebAppContext web = new WebAppContext();
        web.setContextPath("/sanjuan");

        web.setWar("src/main/webapp");
        server.addHandler(web);

         new Thread(new Runnable() {
			@Override
            public void run() {
				try {
					new BufferedReader(new InputStreamReader(System.in)).readLine();
					server.stop();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

        try {
            server.start();
            logger.info("Started Jetty");
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public static class RootServlet extends HttpServlet {
    	/**
         *
         */
        private static final long serialVersionUID = -2360532137858517815L;

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException  {
    		response.setContentType("text/html");
    		response.setStatus(HttpServletResponse.SC_OK);
    		response.getWriter().println("<h1>Jetty Servlet</h1>");
    	}
    }


}
