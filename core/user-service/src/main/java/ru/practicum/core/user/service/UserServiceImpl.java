package ru.practicum.core.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.core.api.dto.user.UserCreateDto;
import ru.practicum.core.api.dto.user.UserDto;
import ru.practicum.core.api.exception.NotFoundException;
import ru.practicum.core.user.controller.AdminUsersGetAllParams;
import ru.practicum.core.user.entity.User;
import ru.practicum.core.user.mapper.UserMapper;
import ru.practicum.core.user.repository.UserRepository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto add(UserCreateDto userCreateDto) {
        User user = userMapper.userCreateDtoToUser(userCreateDto);
        return userMapper.userToUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public List<UserDto> getAll(AdminUsersGetAllParams adminUsersGetAllParams) {
        List<User> userSearchList;
        if (adminUsersGetAllParams.ids() != null) {
            userSearchList = userRepository.findAllByIdIn(
                    Arrays.asList(adminUsersGetAllParams.ids()), PageRequest.of(adminUsersGetAllParams.from(), adminUsersGetAllParams.size()));
        } else {
            userSearchList =
                    userRepository.findAll(
                            PageRequest.of(
                                    adminUsersGetAllParams.from(), adminUsersGetAllParams.size())).stream().toList();
        }
        return userSearchList.stream()
                .map(userMapper::userToUserDto)
                .toList();
    }

    @Override
    @Transactional
    public void delete(long userId) {
        try {
            userRepository.findById(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Required user with id " + userId + " was not found.");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public void checkExistence(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
    }

    @Override
    public UserDto getById(long userId) {
         User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        return userMapper.userToUserDto(user);
    }

    @Override
    public Map<Long, UserDto> getByIds(List<Long> userIds) {
        List<User> userList = userRepository.findAllById(userIds);
        Map<Long, UserDto> usersMap = new HashMap<>();
        for (User user : userList) {
            usersMap.put(user.getId(), userMapper.userToUserDto(user));
        }
        return usersMap;
    }


}
