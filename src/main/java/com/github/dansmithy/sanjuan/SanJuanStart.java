package com.github.dansmithy.sanjuan;


import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap;
import org.jboss.resteasy.plugins.spring.SpringContextLoaderListener;

/**
 * Debug class used for running jetty within Eclipse.
 */
public class SanJuanStart {

	private static Logger logger = Logger.getLogger(SanJuanStart.class);
	
	private static final int DEFAULT_PORT = 8086;

    /**
     * Main function, starts the jetty server. Puts data into
     * the db.
     * @param args  contextPath, war
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception  {

        final Server server = new Server(getPort());
//        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.addEventListener(new ResteasyBootstrap());
        context.addEventListener(new SpringContextLoaderListener());
        context.addServlet(HttpServletDispatcher.class, "/ws/*");
        
//        ResourceHandler resource_handler = new ResourceHandler();
//        resource_handler.setDirectoriesListed(true);
//        resource_handler.setWelcomeFiles(new String[]{ "index.html" });
//        resource_handler.setResourceBase("src/main/webapp");

//        HandlerList handlers = new HandlerList();
//        handlers.setHandlers(new Handler[] { resource_handler, context });
//        server.setHandler(handlers);
        context.setWar("src/main/webapp");
        server.setHandler(context);
        server.start();
        server.join();
    }
    
    private static Integer getPort() {
    	String portVar = System.getenv("PORT");
    	if (portVar != null) {
    		return Integer.valueOf(portVar);
    	} else {
    		return DEFAULT_PORT;
    	}
    }


}
