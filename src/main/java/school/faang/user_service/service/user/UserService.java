package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.publisher.UserDeactivationEvent;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PremiumRepository premiumRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserDto> getNotPremiumUsers(UserFilterDto filterDto) {
        Stream<User> usersToFilter = userRepository.findAll().stream();
        Stream<User> notPremiumUsers = filterPremiumUsers(usersToFilter);

        List<UserDto> filteredUsers = filter(notPremiumUsers, filterDto);
        log.info("Got {} filtered users, by filter {}", filteredUsers.size(), filterDto);
        return filteredUsers;
    }

    @Transactional(readOnly = true)
    public List<UserDto> getPremiumUsers(UserFilterDto filterDto) {
        Stream<User> users = premiumRepository.findPremiumUsers();

        List<UserDto> filteredUsers = filter(users, filterDto);
        log.info("Got {} filtered premium users, by filter {}", filteredUsers.size(), filterDto);
        return filteredUsers;
    }

    public void deactivationUser(Long userId) {
        log.info("The beginning deactivate user");
        User user = findById(userId);
        user.setActive(false);
        save(user);
        log.info("User successfully deactivate");
        eventPublisher.publishEvent(new UserDeactivationEvent(this, userId));
    }

    public UserDto getUserById(Long userId) {
        log.info("The beginning get user by id");
        return userMapper.toDto(findById(userId));
    }

    public List<UserDto> getUsersByIds(List<Long> userIds) {
        log.info("The beginning get users by ids");
        return userMapper.entityStreamToDtoList(findAllById(userIds).stream());
    }

    public UserDto saveUser(UserDto userDto) {
        log.info("The beginning save user");
        return userMapper.toDto(save(userMapper.toEntity(userDto)));
    }

    public void saveAll(List<User> user) {
        userRepository.saveAll(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " not found"));
    }

    public List<User> findAllById(List<Long> userIds) {
        log.info("The beginning get users by ids");
        return userRepository.findAllById(userIds);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }

    @Transactional(readOnly = true)
    public List<Long> getNotExistingUserIds(List<Long> userIds) {
        return userIds.isEmpty() ? Collections.emptyList() : userRepository.findNotExistingUserIds(userIds);
    }

    private List<UserDto> filter(Stream<User> usersStream, UserFilterDto filterDto) {
        return userMapper.entityStreamToDtoList(userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(filterDto))
                .reduce(usersStream,
                        (users, userFilter) -> userFilter.apply(users, filterDto),
                        (a, b) -> b));
    }

    private Stream<User> filterPremiumUsers(Stream<User> users) {
        return users.filter(user -> user.getPremium() == null
                || user.getPremium().getEndDate() == null
                || user.getPremium().getEndDate().isBefore(LocalDateTime.now()));
    }
}
