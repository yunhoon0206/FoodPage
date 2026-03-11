package com.dongyang.example1;

import java.util.Date;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppLifeListener implements ServletContextListener {
    public void contextInitialized(ServletContextEvent sce)  {
    	ServletContext context =sce.getServletContext();
    	System.out.println("------------------------------");
    	System.out.println("웹 애플리케이션 시작" +new Date() );
    	System.out.println( context.getServerInfo());
    
    }

    public void contextDestroyed(ServletContextEvent sce)  {
    	System.out.println("------------------------------");
    	System.out.println("웹 애플리케이션 종료됨" +new Date() );
    }
	
}
