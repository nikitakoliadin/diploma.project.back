package com.qthegamep.diploma.project.back;

import com.qthegamep.diploma.project.back.binder.ApplicationBinder;
import com.qthegamep.diploma.project.back.crypto.CryptoOperation;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.privatbank.cryptonite.CryptoniteX;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        Config.init();
        CryptoOperation.getEndUser();
        CryptoniteX.init();
        LOG.info("CryptoniteX was initialized");
        String host = System.getProperty("host", "0.0.0.0");
        String port = System.getProperty("port", "8080");
        String appContext = System.getProperty("appContext", "");
        String appUrl = "http://" + host + ":" + port + appContext;

        startServer(appUrl);
        LOG.info("cloud-service-app started at {}", appUrl);
        Thread.currentThread().join();
    }

    private static void startServer(String appUrl) {
        final ResourceConfig rc = new ResourceConfig()
                .packages(Main.class.getPackage().getName())
                .register(ApplicationBinder.builder().build());
        GrizzlyHttpServerFactory.createHttpServer(URI.create(appUrl), rc);
    }
}
