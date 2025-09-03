package com.customworld.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

   @Bean
   public CorsFilter corsFilter() {
       CorsConfiguration corsConfiguration = new CorsConfiguration();
       corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:3002","http://localhost:3001","http://localhost:3000","https://customworld.vercel.app"));
       corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE","OPTIONS"));
       corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
       corsConfiguration.setAllowCredentials(true);

       UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
       source.registerCorsConfiguration("/**", corsConfiguration);
       return new CorsFilter(source);
   }
    

    // @Bean
    // public CorsFilter corsFilter() {
    //     CorsConfiguration corsConfiguration = new CorsConfiguration();
    //     corsConfiguration.setAllowedOrigins(Arrays.asList("*")); // Autoriser toutes les origines pour tester
    //     corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    //     corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
    //     corsConfiguration.setAllowCredentials(false); // Désactiver temporairement pour tester sans credentials

    //     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //     source.registerCorsConfiguration("/**", corsConfiguration);
    //     return new CorsFilter(source);
    // }

}
