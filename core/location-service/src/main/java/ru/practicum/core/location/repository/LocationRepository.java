package ru.practicum.core.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.core.location.entity.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {

}
