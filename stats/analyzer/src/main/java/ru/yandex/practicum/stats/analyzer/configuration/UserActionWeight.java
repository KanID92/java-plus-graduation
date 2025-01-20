package ru.yandex.practicum.stats.analyzer.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties("analyzer.user.action.weight")
public final class UserActionWeight {

    public static double view;

    public static double register;

    public static double like;

}
