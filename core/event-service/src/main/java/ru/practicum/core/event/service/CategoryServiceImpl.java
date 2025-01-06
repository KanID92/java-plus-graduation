package ru.practicum.core.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.core.api.dto.category.CategoryDto;
import ru.practicum.core.api.dto.category.NewCategoryDto;
import ru.practicum.core.api.exception.NotFoundException;
import ru.practicum.core.event.entity.Category;
import ru.practicum.core.event.mapper.CategoryMapper;
import ru.practicum.core.event.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.core.event.mapper.CategoryMapper.dtoToCategory;
import static ru.practicum.core.event.mapper.CategoryMapper.toCategoryDto;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public void delete(long categoryId) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Категория id= " + categoryId + " не найдена"));
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public CategoryDto createCategory(@Validated NewCategoryDto dto) {
        return toCategoryDto(categoryRepository.save(dtoToCategory(dto)));
    }

    @Override
    public CategoryDto updateCategory(CategoryDto dto) {

        Category category = categoryRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Категория id= " + dto.getId() + " не найдена"));
        category.setName(dto.getName());

        return toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> getAll(int from, int size) {
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        Page<Category> categoriesPage = categoryRepository.findAll(pageable);

        return categoriesPage.getContent().stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Категория id= " + categoryId + " не найдена"));

        return toCategoryDto(category);
    }
}
