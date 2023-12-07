package com.example.telegrambotbackend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BotConfig {
    @Value("${bot.username}")
    public String username;

    @Value("${bot.token}")
    public String token;

    @Value("${bot.webHookUrl}")
    public String webHookUrl;
}
