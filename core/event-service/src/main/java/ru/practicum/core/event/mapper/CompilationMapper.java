package ru.practicum.core.event.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.core.api.dto.compilation.CompilationDto;
import ru.practicum.core.api.dto.compilation.NewCompilationDto;
import ru.practicum.core.api.dto.event.EventShortDto;
import ru.practicum.core.event.entity.Compilation;
import ru.practicum.core.event.entity.Event;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CompilationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", source = "events")
    Compilation toCompilation(final NewCompilationDto compilationDto, final List<Event> events);

    @Mapping(target = "events", source = "list")
    CompilationDto toCompilationDto(final Compilation compilation, List<EventShortDto> list);
}