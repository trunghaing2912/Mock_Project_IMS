package fa.training.fjb04.ims.repository.user;

import fa.training.fjb04.ims.entity.user.User;
import fa.training.fjb04.ims.enums.user.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, PagingAndSortingRepository<User, Integer>, UserRepositoryCustom {

    Optional<User> findByIdAndStatus(Integer id, UserStatus status);

    Optional<User> findByUsernameIgnoreCaseAndStatus(String username, UserStatus status);

    @Query(value = "SELECT u FROM User u WHERE u.status = 'ACTIVE'")
    Page<User> findPageData(Pageable pageable);

//    @Query(value = "SELECT u FROM User u WHERE u.status = :?1")
//    Page<User> findPageData(UserStatus userStatus, Pageable pageable);

    @Query(value = "SELECT u from User u WHERE u.email = ?1")
    User findByEmailIgnoreCase(String email);

    User findByResetPasswordToken(String token);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmailIgnoreCase(String email);


    @Query("select u.fullName from User u where u.username=:userName")
    String findFullNameByUserName(String userName);

    @Query("select u from User u where u.username = :username")
    User findByUserName(String username);

    boolean existsByIdAndPhoneNumber(Integer id, String phoneNumber);

    boolean existsByIdAndEmail(Integer id, String email);

}
