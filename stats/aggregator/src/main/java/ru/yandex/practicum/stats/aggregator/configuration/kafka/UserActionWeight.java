package ru.yandex.practicum.stats.aggregator.configuration.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties("aggregator.user.action.weight")
public final class UserActionWeight {

    public static double view;

    public static double register;

    public static double like;

}
