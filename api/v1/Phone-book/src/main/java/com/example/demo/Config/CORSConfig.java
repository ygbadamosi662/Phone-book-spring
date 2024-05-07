//package com.example.landlord.Config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class CORSConfig implements WebMvcConfigurer
//{
//    @Autowired
//    private AllowedOriginsService allowedOriginsService;
//
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        List<String> allowedOrigins = allowedOriginsService.getAllowedOrigins();
//
//        // First CORS configuration for "/api/**" endpoints
//        registry.addMapping("/api/**")
//                .allowedOrigins(allowedOrigins.toArray(new String[0]))
//                .allowedMethods("GET", "POST")
//                .allowedHeaders("*");
//
//        // Second CORS configuration for "/other/**" endpoints
//        registry.addMapping("/other/**")
//                .allowedOrigins("http://example.com")  // Custom allowed origins for these endpoints
//                .allowedMethods("GET", "POST", "PUT")
//                .allowedHeaders("Content-Type");
//    }
//}
