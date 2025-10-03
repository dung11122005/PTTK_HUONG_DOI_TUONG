package com.example.exam_portal.config;

// import org.apache.catalina.Context;
// import org.apache.catalina.connector.Connector;
// import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
// import org.springframework.boot.web.server.WebServerFactoryCustomizer;
// import org.springframework.context.annotation.Bean;

//@Configuration
public class TomcatConfig {

    // @Bean
    // public WebServerFactoryCustomizer<TomcatServletWebServerFactory> containerCustomizer() {
    //     return factory -> {
    //         factory.addContextCustomizers((Context context) -> {
    //             // Bật multipart parsing
    //             context.setAllowCasualMultipartParsing(true);
    //         });

    //         factory.addConnectorCustomizers((Connector connector) -> {
    //             // Gán system property cho FileCountMax (giới hạn số input/file trong multipart)
    //             System.setProperty("org.apache.tomcat.util.http.fileupload.FileCountMax", "100");
    //         });
    //     };
    // }
}
