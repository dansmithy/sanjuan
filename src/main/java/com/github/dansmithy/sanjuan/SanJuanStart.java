package com.github.dansmithy.sanjuan;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.FilterMapping;
import org.eclipse.jetty.webapp.WebAppContext;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap;
import org.jboss.resteasy.plugins.spring.SpringContextLoaderListener;
import org.springframework.web.filter.DelegatingFilterProxy;

/**
 * Debug class used for running jetty within Eclipse.
 */
public class SanJuanStart {
	
	private static final int DEFAULT_PORT = 8086;

    /**
     * Main function, starts the jetty server. Puts data into
     * the db.
     * @param args  contextPath, war
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception  {

    	 Server server = new Server(getPort());
         WebAppContext context = new WebAppContext();
         context.setContextPath("/");
         server.setHandler(context);
         DelegatingFilterProxy filterProxy = new DelegatingFilterProxy();
         filterProxy.setTargetBeanName("springSecurityFilterChain");
         context.addFilter(new FilterHolder(filterProxy), "/*", FilterMapping.DEFAULT);
         context.addEventListener(new ResteasyBootstrap());
         context.addEventListener(new SpringContextLoaderListener());
         context.addServlet(HttpServletDispatcher.class, "/ws/*");
         context.setWar("src/main/webapp");
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
