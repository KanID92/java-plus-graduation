package ru.yandex.practicum.stats.analyzer.handler;

import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.stats.recommendations.RecommendationsControllerGrpc;

@GrpcService
public class RecommendationsCollector extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {

}
