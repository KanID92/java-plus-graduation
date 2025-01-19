package ru.practicum.core.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.AnalyzerClient;
import ru.practicum.core.api.client.LikeServiceClient;
import ru.practicum.core.api.client.LocationServiceClient;
import ru.practicum.core.api.client.RequestServiceClient;
import ru.practicum.core.api.client.UserServiceClient;
import ru.practicum.core.api.constant.Constants;
import ru.practicum.core.api.dto.event.EventFullDto;
import ru.practicum.core.api.dto.event.EventShortDto;
import ru.practicum.core.api.dto.event.NewEventDto;
import ru.practicum.core.api.dto.location.LocationDto;
import ru.practicum.core.api.dto.user.UserDto;
import ru.practicum.core.api.enums.EventState;
import ru.practicum.core.api.enums.RequestStatus;
import ru.practicum.core.api.enums.StateAction;
import ru.practicum.core.api.exception.AccessException;
import ru.practicum.core.api.exception.ConflictException;
import ru.practicum.core.api.exception.IncorrectValueException;
import ru.practicum.core.api.exception.NotFoundException;
import ru.practicum.core.event.controller.params.EventGetByIdParams;
import ru.practicum.core.event.controller.params.EventUpdateParams;
import ru.practicum.core.event.controller.params.search.EventSearchParams;
import ru.practicum.core.event.controller.params.search.PublicSearchParams;
import ru.practicum.core.event.entity.Category;
import ru.practicum.core.event.entity.Event;
import ru.practicum.core.event.mapper.EventMapper;
import ru.practicum.core.event.repository.CategoryRepository;
import ru.practicum.core.event.repository.EventRepository;
import ru.yandex.practicum.grpc.stats.recommendations.proto.RecommendationMessages;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static ru.practicum.core.event.entity.QEvent.event;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserServiceClient userServiceClient;
    private final EventMapper eventMapper;
    private final LocationServiceClient locationServiceClient;
    private final LikeServiceClient likeServiceClient;
    private final CategoryRepository categoryRepository;
    private final RequestServiceClient requestServiceClient;

    private final AnalyzerClient analyzerClient;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Constants.JSON_TIME_FORMAT);

    @Override
    public EventFullDto create(long userId, NewEventDto newEventDto) {
        log.info("call userServiceClient.getById: userId = {}", userId);
        UserDto savedUser = userServiceClient.getById(userId);
        log.info("userServiceClient.getById(userId) finished: userId = {}", userId);
        log.debug("result {{}}", savedUser);
        Category category = categoryRepository.findById(newEventDto.category())
                .orElseThrow(() -> new NotFoundException("Category with id " + newEventDto.category() + " not found"));

        log.info("locationServiceClient.create(userId, newEventDto.location(): userId = {}, newEventDto = {}",
                newEventDto, userId);
        LocationDto locationDto = locationServiceClient.create(userId, newEventDto.location());
        log.info("userServiceClient.getById(userId) finished: userId = {}", userId);
        log.debug("result {{}}", locationDto);
        Event event = eventMapper.newEventDtoToEvent(
                newEventDto, savedUser.id(), category, locationDto.id(), LocalDateTime.now());
        Event savedEvent = eventRepository.save(event);
        savedEvent.setInitiator(savedUser);
        savedEvent.setLocation(locationDto);
        return eventMapper.eventToEventFullDto(savedEvent);
    }

    @Override
    public Long addEventLike(long userId, long eventId) {
        log.info("likeServiceClient.addEventLike(userId, eventId): userId = {}, eventId = {}",
                userId, eventId);
        long eventLikeCount = likeServiceClient.addEventLike(userId, eventId);
        log.info("likeServiceClient.addEventLike(userId, eventId) finished: userId = {}, eventId = {}",
                userId, eventId);
        log.debug("result of likeServiceClient.addEventLike {{}}", eventLikeCount);
        return eventLikeCount;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllByInitiator(EventSearchParams searchParams) {

        long initiatorId = searchParams.getPrivateSearchParams().getInitiatorId();

        log.info("call userServiceClient.getById: initiatorId = {}", initiatorId);
        UserDto userDto = userServiceClient.getById(initiatorId);
        log.info("userServiceClient.getById(userId) finished: initiatorId = {}", initiatorId);
        log.debug("result of userServiceClient.getById {{}}", userDto);
        Pageable page = PageRequest.of(searchParams.getFrom(), searchParams.getSize());
        List<Event> receivedEvents = eventRepository.findAllByInitiatorId(initiatorId, page);

        List<Long> eventIds = receivedEvents.stream().map(Event::getId).toList();

        log.info("call likeServiceClient.getAllEventsLikesByIds: eventIds = {}", eventIds);
        Map<Long, Long> likesEventMap = likeServiceClient.getAllEventsLikesByIds(eventIds);
        log.info("likeServiceClient.getAllEventsLikesByIds finished: eventIds = {}", eventIds);
        log.debug("result of likeServiceClient.getAllEventsLikesByIds {{}}", likesEventMap);

        List<Long> locationIds = receivedEvents.stream().map(Event::getLocationId).toList();

        log.info("call locationServiceClient.getAllById(locationIds): locationIds = {}", locationIds);
        Map<Long, LocationDto> locationDtoMap = locationServiceClient.getAllById(locationIds);
        log.info("locationServiceClient.getAllById(locationIds) finished: locationIds = {}", locationIds);
        log.debug("result of locationServiceClient.getAllById(locationIds) {{}}", locationDtoMap);

        for (Event event : receivedEvents) {
            event.setLikes(likesEventMap.get(event.getId()));
            event.setLocation(locationDtoMap.get(event.getLocationId()));
            event.setInitiator(userDto);
        }

        return receivedEvents.stream()
                .map(eventMapper::eventToEventShortDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllByPublic(EventSearchParams searchParams) {

        Pageable page = PageRequest.of(searchParams.getFrom(), searchParams.getSize());

        BooleanExpression booleanExpression = event.isNotNull();

        PublicSearchParams publicSearchParams = searchParams.getPublicSearchParams();

        if (publicSearchParams.getText() != null) { //наличие поиска по тексту
            booleanExpression = booleanExpression.andAnyOf(
                    event.annotation.likeIgnoreCase(publicSearchParams.getText()),
                    event.description.likeIgnoreCase(publicSearchParams.getText())
            );
        }

        if (publicSearchParams.getCategories() != null) { // наличие поиска по категориям
            booleanExpression = booleanExpression.and(
                    event.category.id.in((publicSearchParams.getCategories())));
        }

        if (publicSearchParams.getPaid() != null) { // наличие поиска по категориям
            booleanExpression = booleanExpression.and(
                    event.paid.eq(publicSearchParams.getPaid()));
        }

        LocalDateTime rangeStart = publicSearchParams.getRangeStart();
        LocalDateTime rangeEnd = publicSearchParams.getRangeEnd();

        if (rangeStart != null && rangeEnd != null) { // наличие поиска дате события
            booleanExpression = booleanExpression.and(
                    event.eventDate.between(rangeStart, rangeEnd)
            );
        } else if (rangeStart != null) {
            booleanExpression = booleanExpression.and(
                    event.eventDate.after(rangeStart)
            );
            rangeEnd = rangeStart.plusYears(100);
        } else if (publicSearchParams.getRangeEnd() != null) {
            booleanExpression = booleanExpression.and(
                    event.eventDate.before(rangeEnd)
            );
            rangeStart = LocalDateTime.parse(LocalDateTime.now().format(dateTimeFormatter), dateTimeFormatter);
        }

        if (rangeEnd == null) {
            booleanExpression = booleanExpression.and(
                    event.eventDate.after(LocalDateTime.now())
            );
            rangeStart = LocalDateTime.parse(LocalDateTime.now().format(dateTimeFormatter), dateTimeFormatter);
            rangeEnd = rangeStart.plusYears(100);
        }

        List<Event> eventListBySearch =
                eventRepository.findAll(booleanExpression, page).stream().toList();

        List<Long> eventIds = eventListBySearch.stream().map(Event::getId).toList();

        log.info("CALL. likeServiceClient.getAllEventsLikesByIds in getAllByPublic: eventIds = {}", eventIds);
        Map<Long, Long> likesEventMap = likeServiceClient.getAllEventsLikesByIds(eventIds);
        log.info("likeServiceClient.getAllEventsLikesByIds in getAllByPublic finished: eventIds = {}", eventIds);
        log.debug("result of likeServiceClient.getAllEventsLikesByIds in getAllByPublic {{}}", likesEventMap);

        Map<Long, UserDto> users = userServiceClient.getAll(eventListBySearch.stream()
                .map(Event::getInitiatorId)
                .toList());


        List<Long> locationIds = eventListBySearch.stream().map(Event::getLocationId).toList();
        log.info("call locationServiceClient.getAllById(locationIds) in getAllByPublic: locationIds = {}", locationIds);
        Map<Long, LocationDto> locationDtoMap = locationServiceClient.getAllById(locationIds);
        log.info("locationServiceClient.getAllById(locationIds) finished in getAllByPublic: locationIds = {}",
                locationIds);
        log.debug("result of locationServiceClient.getAllById(locationIds) in getAllByPublic{{}}",
                locationDtoMap);

        log.info("CALL. requestServiceClient.countByStatusAndEventsIds(\n" +
                " RequestStatus.CONFIRMED, eventIds) in getAllByPublic: eventIds = {}", eventIds);
        Map<Long, Long> countsOfConfirmedRequestsMap = requestServiceClient.countByStatusAndEventsIds(
                RequestStatus.CONFIRMED, eventIds);
        log.info("FINISHED. requestServiceClient.countByStatusAndEventsIds(RequestStatus.CONFIRMED, eventIds)" +
                        " in getAllByPublic: eventIds = {}", eventIds);
        log.debug("RESULT. requestServiceClient.countByStatusAndEventsIds(RequestStatus.CONFIRMED, eventIds) " +
                        "in getAllByPublic{{}}", countsOfConfirmedRequestsMap);

        for (Event event : eventListBySearch) {
            event.setConfirmedRequests(countsOfConfirmedRequestsMap.get(event.getId()));
            event.setLikes(likesEventMap.get(event.getId()));
            event.setLocation(locationDtoMap.get(event.getLocationId()));
            event.setInitiator(users.get(event.getInitiatorId()));
        }

        return eventListBySearch.stream()
                .map(eventMapper::eventToEventShortDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getTopEvent(Integer count) {

        log.info("CALL. likeServiceClient.getTopLikedEventsIds(count) in getTopEvent: count = {}", count);
        Map<Long, Long> likesEventMap = likeServiceClient.getTopLikedEventsIds(count);
        log.info("FINISHED. likeServiceClient.getTopLikedEventsIds(count) in getTopEvent: count = {}", count);
        log.debug("RESULT. likeServiceClient.getTopLikedEventsIds(count) in getTopEvent: {{}}", likesEventMap);
        List<Long> topEventsIds = new ArrayList<>(likesEventMap.keySet());
        List<Event> eventTopList = eventRepository.findAllByIdIn(topEventsIds);

        log.info("CALL. userServiceClient.getAll in getTopEvent: initiators in eventTopList = {}", eventTopList);
        Map<Long, UserDto> users = userServiceClient.getAll(eventTopList.stream()
                .map(Event::getInitiatorId)
                .toList());
        log.info("FINISHED. userServiceClient.getAll in getTopEvent: initiators in eventTopList = {}",
                eventTopList);
        log.debug("RESULT. userServiceClient.getAll in getTopEvent: {{}}", users);

        List<Long> locationIds = eventTopList.stream().map(Event::getLocationId).toList();

        log.info("CALL. locationServiceClient.getAllById(locationIds) in getTopEvent: locationIds = {}",
                locationIds);
        Map<Long, LocationDto> locationDtoMap = locationServiceClient.getAllById(locationIds);
        log.info("FINISHED. locationServiceClient.getAllById(locationIds) in getTopEvent: locationIds = {}",
                locationIds);
        log.debug("RESULT. locationServiceClient.getAllById(locationIds) in getTopEvent: {{}}", locationDtoMap);


        log.info("CALL. requestServiceClient.countByStatusAndEventsIds(RequestStatus.CONFIRMED, topEventsIds) " +
                "in getTopEvent: eventIds = {}", topEventsIds);
        Map<Long, Long> countsOfConfirmedRequestsMap = requestServiceClient.countByStatusAndEventsIds(
                RequestStatus.CONFIRMED, topEventsIds);
        log.info("FINISHED. requestServiceClient.countByStatusAndEventsIds(RequestStatus.CONFIRMED, topEventsIds)" +
                " in getTopEvent: eventIds = {}", topEventsIds);
        log.debug("RESULT. requestServiceClient.countByStatusAndEventsIds(RequestStatus.CONFIRMED, topEventsIds) " +
                "in getTopEvent: {{}}", countsOfConfirmedRequestsMap);


        for (Event event : eventTopList) {
            event.setRating(getRatingFromAnalyzer(event.getId()));
            event.setConfirmedRequests(countsOfConfirmedRequestsMap.get(event.getId()));
            event.setLikes(likesEventMap.get(event.getId()));
            event.setLocation(locationDtoMap.get(event.getLocationId()));
            event.setInitiator(users.get(event.getInitiatorId()));
        }

        return eventTopList.stream()
                .map(eventMapper::eventToEventShortDto)
                .toList();
    }



    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAllByAdmin(EventSearchParams searchParams) {
        Pageable page = PageRequest.of(
                searchParams.getFrom(), searchParams.getSize());

        BooleanExpression booleanExpression = event.isNotNull();

        if (searchParams.getAdminSearchParams().getUsers() != null) {
            booleanExpression = booleanExpression.and(
                    event.initiatorId.in(searchParams.getAdminSearchParams().getUsers()));
        }

        if (searchParams.getAdminSearchParams().getCategories() != null) {
            booleanExpression = booleanExpression.and(
                    event.category.id.in(searchParams.getAdminSearchParams().getCategories()));
        }

        if (searchParams.getAdminSearchParams().getStates() != null) {
            booleanExpression = booleanExpression.and(
                    event.state.in(searchParams.getAdminSearchParams().getStates()));
        }

        LocalDateTime rangeStart = searchParams.getAdminSearchParams().getRangeStart();
        LocalDateTime rangeEnd = searchParams.getAdminSearchParams().getRangeEnd();

        if (rangeStart != null && rangeEnd != null) {
            booleanExpression = booleanExpression.and(
                    event.eventDate.between(rangeStart, rangeEnd));
        } else if (rangeStart != null) {
            booleanExpression = booleanExpression.and(
                    event.eventDate.after(rangeStart));
        } else if (rangeEnd != null) {
            booleanExpression = booleanExpression.and(
                    event.eventDate.before(rangeEnd));
        }

        List<Event> receivedEventList = eventRepository.findAll(booleanExpression, page).stream().toList();

        List<Long> eventIds = receivedEventList.stream().map(Event::getId).toList();

        log.info("CALL. likeServiceClient.getAllEventsLikesByIds(eventIds) in getAllByAdmin: eventIds = {}", eventIds);
        Map<Long, Long> likesEventMap = likeServiceClient.getAllEventsLikesByIds(eventIds);
        log.info("FINISHED. likeServiceClient.getAllEventsLikesByIds(eventIds) in getAllByAdmin: eventIds = {}", eventIds);
        log.debug("RESULT. likeServiceClient.getAllEventsLikesByIds(eventIds) in getAllByAdmin: {{}}", likesEventMap);

        List<Long> locationIds = receivedEventList.stream().map(Event::getLocationId).toList();

        log.info("CALL. locationServiceClient.getAllById(locationIds) in getAllByAdmin: locationIds = {}",
                locationIds);
        Map<Long, LocationDto> locationDtoMap = locationServiceClient.getAllById(locationIds);
        log.info("FINISHED. locationServiceClient.getAllById(locationIds) in getAllByAdmin: locationIds = {}",
                locationIds);
        log.debug("RESULT. locationServiceClient.getAllById(locationIds) in getAllByAdmin: {{}}", locationDtoMap);



        log.info("CALL. requestServiceClient.countByStatusAndEventsIds(RequestStatus.CONFIRMED, eventIds) " +
                "in getAllByAdmin: eventIds = {}", eventIds);
        Map<Long, Long> countsOfConfirmedRequestsMap = requestServiceClient.countByStatusAndEventsIds(
                RequestStatus.CONFIRMED, eventIds);
        log.info("FINISHED. requestServiceClient.countByStatusAndEventsIds(RequestStatus.CONFIRMED, eventIds)" +
                "in getAllByAdmin: eventIds = {}", eventIds);
        log.debug("RESULT. requestServiceClient.countByStatusAndEventsIds(RequestStatus.CONFIRMED, eventIds " +
                "in getAllByAdmin: {{}}", countsOfConfirmedRequestsMap);


        log.info("CALL. userServiceClient.getAll by initiator in receivedEventList " +
                "in getAllByAdmin: receivedEventList = {}", receivedEventList);
        Map<Long, UserDto> users = userServiceClient.getAll(receivedEventList.stream()
                .map(Event::getInitiatorId)
                .toList());
        log.info("FINISHED. userServiceClient.getAll by initiator in receivedEventList " +
                "in getAllByAdmin: receivedEventList = {}", receivedEventList);
        log.debug("RESULT. userServiceClient.getAll : {{}}", users);


        for (Event event : receivedEventList) {
            event.setConfirmedRequests(countsOfConfirmedRequestsMap.get(event.getId()));
            event.setLikes(likesEventMap.get(event.getId()));
            event.setLocation(locationDtoMap.get(event.getLocationId()));
            event.setInitiator(users.get(event.getInitiatorId()));
        }

        return receivedEventList
                .stream()
                .map(eventMapper::eventToEventFullDto)
                .toList();

    }


    @Override
    @Transactional(readOnly = true)
    public EventFullDto getById(EventGetByIdParams params) {
        Event receivedEvent;
        if (params.initiatorId() != null) {
            log.info("CALL. userServiceClient.checkExistence(params.initiatorId() " +
                    "in getById: params.initiatorId() = {}", params.initiatorId());
            userServiceClient.checkExistence(params.initiatorId());
            log.info("FINISHED. userServiceClient.checkExistence(params.initiatorId() " +
                    "in getById: params.initiatorId() = {}", params.initiatorId());
            receivedEvent = eventRepository.findByInitiatorIdAndId(params.initiatorId(), params.eventId())
                    .orElseThrow(() -> new NotFoundException(
                            "Event with id " + params.eventId() +
                                    " created by user with id " + params.initiatorId() + " not found"));
        } else {
            receivedEvent = eventRepository.findById(params.eventId())
                    .orElseThrow(() -> new NotFoundException("Event with id " + params.eventId() + " not found"));

            double rating = getRatingFromAnalyzer(params.eventId());

            receivedEvent.setRating(rating);
            log.info("CALL. requestServiceClient.countByStatusAndEventsIds(" +
                    " RequestStatus.CONFIRMED, receivedEvent.getId())" +
                    " in getById: eventIds = {}", receivedEvent.getId());
            receivedEvent.setConfirmedRequests(
                    requestServiceClient.countByStatusAndEventId(RequestStatus.CONFIRMED, receivedEvent.getId()));
            log.info("FINISHED. requestServiceClient.countByStatusAndEventsIds(" +
                    " RequestStatus.CONFIRMED, receivedEvent.getId())" +
                    " in getById: eventIds = {}", receivedEvent.getId());

            log.info("CALL. likeServiceClient.getCountByEventId(receivedEvent.getId()) " +
                    " in getById: receivedEvent.getId() = {}", receivedEvent.getId());
            receivedEvent.setLikes(likeServiceClient.getCountByEventId(receivedEvent.getId()));
            log.info("FINISHED. likeServiceClient.getCountByEventId(receivedEvent.getId()) " +
                    " in getById: receivedEvent.getId() = {}", receivedEvent.getId());

            log.info("CALL. locationServiceClient.getById(receivedEvent.getLocationId()) " +
                    " in getById: receivedEvent.getLocationId() = {}", receivedEvent.getLocationId());
            receivedEvent.setLocation(locationServiceClient.getById(receivedEvent.getLocationId()));
            log.info("FINISHED. locationServiceClient.getById(receivedEvent.getLocationId()) " +
                    " in getById: receivedEvent.getLocationId() = {}", receivedEvent.getLocationId());
        }

        log.info("CALL. userServiceClient.getById(receivedEvent.getInitiatorId()) in getById:" +
                        " receivedEvent.getInitiatorId(): {}",
                receivedEvent.getInitiatorId());
        UserDto initiator = userServiceClient.getById(receivedEvent.getInitiatorId());
        log.info("FINISHED. userServiceClient.getById(receivedEvent.getInitiatorId()) in getById: " +
                        "receivedEvent.getInitiatorId(): {}",
                receivedEvent.getInitiatorId());
        log.info("RESULT. userServiceClient.getById(receivedEvent.getInitiatorId()) in getById: {}",
                initiator);
        receivedEvent.setInitiator(initiator);

        return eventMapper.eventToEventFullDto(receivedEvent);
    }

    @Override
    public EventFullDto update(long eventId, EventUpdateParams updateParams) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));

        Event updatedEvent;

        if (updateParams.updateEventUserRequest() != null) { // private section
            userServiceClient.checkExistence(updateParams.userId());
            if (updateParams.updateEventUserRequest().category() != null) {
                Category category = categoryRepository.findById(updateParams.updateEventUserRequest().category())
                        .orElseThrow(() -> new NotFoundException(
                                "Category with id " + updateParams.updateEventUserRequest().category() + " not found"));
                event.setCategory(category);
            }
            if (!updateParams.userId().equals(event.getInitiatorId())) {
                throw new AccessException("User with id = " + updateParams.userId() + " do not initiate this event");
            }

            if (event.getState() != EventState.PENDING && event.getState() != EventState.CANCELED) {
                throw new ConflictException(
                        "User. Cannot update event: only pending or canceled events can be changed");
            }

            LocalDateTime eventDate = updateParams.updateEventUserRequest().eventDate();

            if (eventDate != null &&
                    eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ConflictException(
                        "User. Cannot update event: event date must be not earlier then after 2 hours ");
            }

            StateAction stateAction = updateParams.updateEventUserRequest().stateAction();
            log.debug("State action received from params: {}", stateAction);

            if (stateAction != null) {
                switch (stateAction) {
                    case CANCEL_REVIEW -> event.setState(EventState.CANCELED);

                    case SEND_TO_REVIEW -> event.setState(EventState.PENDING);
                }
            }

            if (updateParams.updateEventUserRequest().location() != null) {
                event.setLocationId(updateParams.updateEventUserRequest().location().id());
            }

            log.debug("Private. Событие до мапинга: {}", event);
            eventMapper.updateEventUserRequestToEvent(event, updateParams.updateEventUserRequest());
            log.debug("Private. Событие после мапинга для сохранения: {}", event);

        }

        if (updateParams.updateEventAdminRequest() != null) { // admin section

            if (updateParams.updateEventAdminRequest().category() != null) {
                Category category  = categoryRepository.findById(updateParams.updateEventAdminRequest().category())
                        .orElseThrow(() -> new NotFoundException(
                                "Category with id " + updateParams.updateEventAdminRequest().category() + " not found"));
                event.setCategory(category);
            }

            if (event.getState() != EventState.PENDING) {
                throw new ConflictException("Admin. Cannot update event: only pending events can be changed");
            }

            if (updateParams.updateEventAdminRequest().eventDate() != null &&
                    updateParams.updateEventAdminRequest().eventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                throw new IncorrectValueException(
                        "Admin. Cannot update event: event date must be not earlier then after 2 hours ");
            }
            log.debug("Admin. Событие до мапинга: {}; {}", event.getId(), event.getState());
            eventMapper.updateEventAdminRequestToEvent(event, updateParams.updateEventAdminRequest());
            log.debug("Admin. Событие после мапинга для сохранения: {}, {}", event.getId(), event.getState());

        }
        event.setId(eventId);

        updatedEvent = eventRepository.save(event);

        log.info("CALL. likeServiceClient.getCountByEventId(updatedEvent.getId()) " +
                " in update: receivedEvent.getId() = {}", updatedEvent.getId());
        updatedEvent.setLikes(likeServiceClient.getCountByEventId(updatedEvent.getId()));
        log.info("FINISHED. likeServiceClient.getCountByEventId(updatedEvent.getId()) " +
                " in update: receivedEvent.getId() = {}", updatedEvent.getId());
        log.debug("RESULT. likeServiceClient.getCountByEventId(updatedEvent.getId()) " +
                " in update: {{}}", updatedEvent.getLikes());

        log.info("CALL. locationServiceClient.getById(updatedEvent.getLocationId()) " +
                " in update: updatedEvent.getLocationId() = {}", updatedEvent.getLocationId());
        updatedEvent.setLocation(locationServiceClient.getById(updatedEvent.getLocationId()));
        log.info("FINISHED. locationServiceClient.getById(updatedEvent.getLocationId()) " +
                " in update: updatedEvent.getLocationId() = {}", updatedEvent.getLocationId());
        log.debug("RESULT. locationServiceClient.getById(updatedEvent.getLocationId()) " +
                " in update: {{}}", updatedEvent.getLocation());

        log.info("CALL. requestServiceClient.countByStatusAndEventId(" +
                " RequestStatus.CONFIRMED, updatedEvent.getId()) " +
                " in update: updatedEvent.getId() = {}", updatedEvent.getId());
        updatedEvent.setConfirmedRequests(requestServiceClient.countByStatusAndEventId(
                RequestStatus.CONFIRMED, updatedEvent.getId()));
        log.info("FINISHED. requestServiceClient.countByStatusAndEventId(" +
                " RequestStatus.CONFIRMED, updatedEvent.getId()) " +
                " in update: updatedEvent.getId() = {}", updatedEvent.getId());
        log.debug("RESULT. requestServiceClient.countByStatusAndEventId(" +
                " RequestStatus.CONFIRMED, updatedEvent.getId()) " +
                " in update: {{}}", updatedEvent.getConfirmedRequests());

        log.info("CALL. userServiceClient.getById(updatedEvent.getInitiatorId())" +
                " in update: updatedEvent.getInitiatorId() = {}", updatedEvent.getInitiatorId());
        updatedEvent.setInitiator(userServiceClient.getById(updatedEvent.getInitiatorId()));
        log.info("FINISHED. userServiceClient.getById(updatedEvent.getInitiatorId())" +
                " in update: updatedEvent.getInitiatorId() = {}", updatedEvent.getInitiatorId());
        log.debug("RESULT. userServiceClient.getById(updatedEvent.getInitiatorId())" +
                " in update: {{}}", updatedEvent.getInitiator());

        log.debug("Событие возвращенное из базы: {} ; {}", event.getId(), event.getState());

        return eventMapper.eventToEventFullDto(updatedEvent);
    }

    @Override
    public EventFullDto getByIdInternal(long eventId) {
        Event savedEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));
        log.info("CALL. userServiceClient.getById(savedEvent.getInitiatorId())" +
                " in update: savedEvent.getInitiatorId() = {}", savedEvent.getInitiatorId());
        savedEvent.setInitiator(userServiceClient.getById(savedEvent.getInitiatorId()));
        log.info("FINISHED. userServiceClient.getById(savedEvent.getInitiatorId())" +
                " in update: savedEvent.getInitiatorId() = {}", savedEvent.getInitiatorId());
        log.info("RESULT. userServiceClient.getById(savedEvent.getInitiatorId())" +
                " in update: {{}}", savedEvent.getInitiator());
        return eventMapper.eventToEventFullDto(savedEvent);
    }

    private double getRatingFromAnalyzer(long eventId) {

        log.info("CALL. analyzerClient.getInteractionsCount(List.of(eventId)). eventId: {}", eventId);
        Stream<RecommendationMessages.RecommendedEventProto> interactionsCountStream =
                analyzerClient.getInteractionsCount(List.of(eventId));
        log.info("FINISHED. analyzerClient.getInteractionsCount(List.of(eventId)). eventId: {}", eventId);
        double result = interactionsCountStream.findFirst()
                .map(RecommendationMessages.RecommendedEventProto::getScore)
                .orElse(0.0);
        log.debug("RESULT. getRatingFromAnalyzer. Counted result: {}", result);
        return result;
    }

}
