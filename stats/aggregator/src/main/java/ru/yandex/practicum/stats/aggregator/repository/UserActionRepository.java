package ru.yandex.practicum.stats.aggregator.repository;

import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.Map;

public interface UserActionRepository {


     Map<Long, Double> getActionsByEvent(Long eventId);

     void save(UserActionAvro userAction);

     double getWeightFromAvro(ActionTypeAvro actionType);

}
