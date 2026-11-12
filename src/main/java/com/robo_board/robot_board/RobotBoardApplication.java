package com.robo_board.robot_board;

import org.apache.catalina.webresources.TomcatURLStreamHandlerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RobotBoardApplication {

    public static void main(String[] args) {
        // Tells Java that this app has a GUI.
        System.setProperty("java.awt.headless", "false");

        // Fix for macOS so that the Robocode GUI is displayed.
        System.setProperty("apple.awt.UIElement", "false");

        // Robocode installs its own URLStreamHandlerFactory for 'jarjar' URLs.
        // In Java, URL.setURLStreamHandlerFactory can only be called once per JVM.
        // Disable Tomcat's factory registration so Robocode can register its own.
        TomcatURLStreamHandlerFactory.disable();

        SpringApplication.run(RobotBoardApplication.class, args);
    }
}
