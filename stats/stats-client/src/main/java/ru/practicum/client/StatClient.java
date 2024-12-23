package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.HitDto;
import ru.practicum.HitStatDto;

import java.util.List;

@FeignClient(name = "stats-server")
public interface StatClient {

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    void saveHit(@RequestBody HitDto hitDto);

    @GetMapping("/stats")
    List<HitStatDto> getStats(@RequestParam String startTime,
                              @RequestParam String endTime,
                              @RequestParam(required = false) List<String> uris,
                              @RequestParam (required = false, defaultValue = "false") Boolean unique);
}
