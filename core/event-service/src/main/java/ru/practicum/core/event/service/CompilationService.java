package ru.practicum.core.event.service;

import ru.practicum.core.api.dto.compilation.CompilationDto;
import ru.practicum.core.api.dto.compilation.NewCompilationDto;
import ru.practicum.core.api.dto.compilation.UpdateCompilationRequestDto;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getAll(Boolean pinned, int from, int size);

    CompilationDto getById(Long categoryId);

    void delete(long compilationId);

    CompilationDto createCompilation(NewCompilationDto dto);

    CompilationDto updateCompilation(Long id, UpdateCompilationRequestDto dto);
}