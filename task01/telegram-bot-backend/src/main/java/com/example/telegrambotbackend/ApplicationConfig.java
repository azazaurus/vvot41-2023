package com.example.telegrambotbackend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

@Configuration
public class ApplicationConfig {
    @Bean
    public DefaultBotOptions defaultBotOptionsInstance() {
        return new DefaultBotOptions();
    }

    @Bean
    public SetWebhook setWebhookInstance(BotConfig botConfig) {
        return SetWebhook.builder().url(botConfig.webHookUrl).build();
    }
}
