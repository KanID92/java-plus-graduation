package ru.practicum.core.event.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.core.api.dto.category.CategoryDto;
import ru.practicum.core.api.dto.category.NewCategoryDto;
import ru.practicum.core.event.service.CategoryService;


@Slf4j
@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody NewCategoryDto dto) {
        log.info("==> POST. Adding new Category: {}", dto);
        CategoryDto savedCategory = categoryService.createCategory(dto);
        log.info("<== POST. New Category added: {}", dto);
        return savedCategory;
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable long catId) {
        log.info("==> POST. Deleting Category: {}", catId);
        categoryService.delete(catId);
        log.info("<== POST. Category deleted");
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(@PathVariable long catId, @Valid @RequestBody CategoryDto categoryDto) {
        categoryDto.setId(catId);
        log.info("==> POST. Updating Category: {}", categoryDto);
        CategoryDto updatedCategory = categoryService.updateCategory(categoryDto);
        log.info("<== POST. Category updated: {}", categoryDto);
        return updatedCategory;
    }
}
