package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.core.api.dto.category.CategoryDto;
import ru.practicum.core.api.dto.category.NewCategoryDto;
import ru.practicum.ewm.entity.Category;


@UtilityClass
public class CategoryMapper {
    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }


    public static Category dtoToCategory(NewCategoryDto newCategoryDto) {

        Category category = new Category();
        category.setName(newCategoryDto.getName());
        return category;
    }
}