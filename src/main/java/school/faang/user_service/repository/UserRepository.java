package school.faang.user_service.repository;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(nativeQuery = true, value = """
            SELECT COUNT(s.id) FROM users u
            JOIN user_skill us ON us.user_id = u.id
            JOIN skill s ON us.skill_id = s.id
            WHERE u.id = ?1 AND s.id IN (?2)
            """)
    int countOwnedSkills(long userId, List<Long> ids);

    @Query(nativeQuery = true, value = """
            SELECT u.* FROM users u
            JOIN user_premium up ON up.user_id = u.id
            WHERE up.end_date > NOW()
            """)
    Stream<User> findPremiumUsers();

    List<User> findByUsernameLike(String username);

    @Query(nativeQuery = true, value = """
            SELECT u.id FROM users u
            WHERE u.id NOT IN (:userIds)
            """)
    List<Long> findNotExistingUserIds(@Param("userIds") List<Long> userIds);

    @Query("""
    SELECT u.userProfilePic 
    FROM User u 
    WHERE u.id = :userId
    """)
    Optional<UserProfilePic> findUserProfilePicByUserId(
            @Param("userId") Long userId
    );

    @Modifying
    @Query("""
    UPDATE User u 
    SET u.userProfilePic.fileId = :fileId, 
        u.userProfilePic.smallFileId = :smallFileId 
    WHERE u.id = :userId
    """)
    void updateProfilePic(
            @Param("userId") Long userId,
            @Param("fileId") String fileId,
            @Param("smallFileId") String smallFileId
    );

    @Modifying
    @Query("""
    UPDATE User u 
    SET u.userProfilePic.fileId = NULL, 
        u.userProfilePic.smallFileId = NULL 
    WHERE u.id = :userId
    """)
    void deleteProfilePic(
            @Param("userId") Long userId
    );
}