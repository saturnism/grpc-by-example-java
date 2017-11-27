package com.example.chat.vaadin;

import javax.servlet.annotation.WebServlet;

import com.example.chat.ChatUI;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;

/**
 *
 */

@WebServlet(
    urlPatterns = "/*",
    name = "MyServlet",
    displayName = "gRPC Vaadin Client",
    asyncSupported = true,
    loadOnStartup = 1)
@VaadinServletConfiguration(ui = ChatUI.class, productionMode = false)
public class MyServlet extends VaadinServlet {
}
