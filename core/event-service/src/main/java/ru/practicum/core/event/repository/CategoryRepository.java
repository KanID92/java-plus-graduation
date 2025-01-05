package ru.practicum.core.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.core.event.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
