package school.faang.user_service.service.user_jira;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.userJira.UserJira;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.user_jira.UserJiraRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserJiraServiceTest {

    @Mock
    private UserJiraRepository userJiraRepository;

    @InjectMocks
    private UserJiraService userJiraService;

    @Test
    void saveOrUpdateExistingUserJiraTest() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        String jiraDomain = "itmad1x";
        String existingUserJiraEmail = "123@gmail.com";
        String existingUserJiraAccountId = "219u2140o94u1902ou";
        String existingUserJiraToken = "9018409128f09128i4098i10i120if041i4";
        UserJira existingUserJira = UserJira.builder()
                .user(user)
                .jiraDomain(jiraDomain)
                .jiraEmail(existingUserJiraEmail)
                .jiraAccountId(existingUserJiraAccountId)
                .jiraToken(existingUserJiraToken)
                .build();
        String userToSaveJiraEmail = "352b325b@gmail.com";
        String userToSaveJiraAccountId = "53b2352325b235b23b532b5352b";
        String userToSaveJiraToken = "235b235b235b235b235b235b4";
        UserJira userToSaveJira = UserJira.builder()
                .user(user)
                .jiraDomain(jiraDomain)
                .jiraEmail(userToSaveJiraEmail)
                .jiraAccountId(userToSaveJiraAccountId)
                .jiraToken(userToSaveJiraToken)
                .build();

        when(userJiraRepository.findByUserIdAndJiraDomain(userId, jiraDomain)).thenReturn(Optional.of(existingUserJira));
        when(userJiraRepository.save(existingUserJira)).thenReturn(userToSaveJira);

        UserJira result = assertDoesNotThrow(() -> userJiraService.saveOrUpdate(userToSaveJira));

        verify(userJiraRepository, times(1)).findByUserIdAndJiraDomain(userId, jiraDomain);
        verify(userJiraRepository, times(1)).save(any(UserJira.class));

        assertEquals(userToSaveJiraEmail, result.getJiraEmail());
        assertEquals(userToSaveJiraAccountId, result.getJiraAccountId());
        assertEquals(userToSaveJiraToken, result.getJiraToken());
    }

    @Test
    void saveOrUpdateNewUserJiraTest() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        String jiraDomain = "itmad1x";
        String userToSaveJiraEmail = "352b325b@gmail.com";
        String userToSaveJiraAccountId = "53b2352325b235b23b532b5352b";
        String userToSaveJiraToken = "235b235b235b235b235b235b4";
        UserJira userToSaveJira = UserJira.builder()
                .user(user)
                .jiraDomain(jiraDomain)
                .jiraEmail(userToSaveJiraEmail)
                .jiraAccountId(userToSaveJiraAccountId)
                .jiraToken(userToSaveJiraToken)
                .build();

        when(userJiraRepository.findByUserIdAndJiraDomain(userId, jiraDomain)).thenReturn(Optional.empty());
        when(userJiraRepository.save(userToSaveJira)).thenReturn(userToSaveJira);

        UserJira result = assertDoesNotThrow(() -> userJiraService.saveOrUpdate(userToSaveJira));

        verify(userJiraRepository, times(1)).findByUserIdAndJiraDomain(userId, jiraDomain);
        verify(userJiraRepository, times(1)).save(any(UserJira.class));

        assertEquals(userToSaveJiraEmail, result.getJiraEmail());
        assertEquals(userToSaveJiraAccountId, result.getJiraAccountId());
        assertEquals(userToSaveJiraToken, result.getJiraToken());
    }

    @Test
    void getByUserIdAndJiraDomainExistingTest() {
        long userId = 1L;
        String jiraDomain = "itmad1x";
        when(userJiraRepository.findByUserIdAndJiraDomain(userId, jiraDomain)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userJiraService.getByUserIdAndJiraDomain(userId, jiraDomain));

        verify(userJiraRepository, times(1)).findByUserIdAndJiraDomain(userId, jiraDomain);
    }

    @Test
    void getByUserIdAndJiraDomainNotFoundTest() {
        long userId = 1L;
        String jiraDomain = "itmad1x";
        UserJira userJira = new UserJira();
        when(userJiraRepository.findByUserIdAndJiraDomain(userId, jiraDomain)).thenReturn(Optional.of(userJira));

        assertDoesNotThrow(() -> userJiraService.getByUserIdAndJiraDomain(userId, jiraDomain));

        verify(userJiraRepository, times(1)).findByUserIdAndJiraDomain(userId, jiraDomain);
    }
}