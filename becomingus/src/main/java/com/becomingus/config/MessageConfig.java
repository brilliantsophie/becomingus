package com.becomingus.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

@Configuration
public class MessageConfig {

    // 사용자의 언어 설정을 자동 감지하는 로케일 리졸버 설정
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.KOREAN); // 기본 로케일: 한국어
        return localeResolver;
    }

    // 다국어 메시지 소스 설정
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages/messages");   // 메시지 파일 경로
        messageSource.setDefaultEncoding("UTF-8");  // 인코딩 설정
        messageSource.setCacheSeconds(3600);    // 1시간마다 갱신
        return messageSource;
    }
}
