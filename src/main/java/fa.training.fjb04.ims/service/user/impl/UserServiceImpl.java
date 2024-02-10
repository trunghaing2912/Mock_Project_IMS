package fa.training.fjb04.ims.service.user.impl;

import fa.training.fjb04.ims.config.security.SecurityUtils;
import fa.training.fjb04.ims.entity.user.User;
import fa.training.fjb04.ims.repository.user.UserRepository;
import fa.training.fjb04.ims.service.user.UserService;
import fa.training.fjb04.ims.util.dto.user.RoleListDTO;
import fa.training.fjb04.ims.util.dto.user.UserListDTO;
import fa.training.fjb04.ims.util.page.Page;
import fa.training.fjb04.ims.util.page.PageRoles;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Page<UserListDTO> getPageUser(Integer pageIndex, Integer pageSize, String search, String field) {
        Integer totalPage;
        List<UserListDTO> users = userRepository.getUserPaging(pageIndex, pageSize, search, field);

        if (pageSize >= userRepository.getAllUser(search, field).size()) {
            totalPage = 1;
        } else {
            totalPage = (int) Math.ceil(userRepository.getAllUser(search,field).size() * 1.0 / pageSize);
        }

        String role = SecurityUtils.getCurrentRole().toString();

        Page<UserListDTO> page = new Page<>(totalPage, pageIndex, users, pageSize, role);
        return page;
    }

    @Override
    public Optional<User> findByUserId(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public void updateResetPasswordToken(String token, String email) {
        User findUser = userRepository.findByEmailIgnoreCase(email);

        if(findUser != null) {
            findUser.setResetPasswordToken(token);
            findUser.setExpriedDate(LocalDateTime.now().plusHours(24));
            findUser.setIsResetSuccessfully("FALSE");
            userRepository.save(findUser);
        } else {
            throw new UsernameNotFoundException("Could not find any customer with the email" + email);
        }
    }

    @Override
    public User getByResetPasswordToken(String token) {
        return userRepository.findByResetPasswordToken(token);
    }

    @Override
    public void updatePassword(User user, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setIsResetSuccessfully("SUCCESS");
        System.out.println(newPassword);
        user.setResetPasswordToken(null);
        userRepository.save(user);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }


    @Override
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean existByUserName(String username) {
        return userRepository.existsByUsernameIgnoreCase(username);
    }

    @Override
    public boolean existByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public boolean existByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public boolean existById(Integer id) {
        return userRepository.existsById(id);
    }

    @Override
    public String findFullNameByUserName(String userName) {
        return userRepository.findFullNameByUserName(userName);
    }

    @Override
    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public List<User> getUsersIsManager() {
        return userRepository.getUsersIsManager();
    }

    @Override
    public boolean existByIdAndPhoneNumber(Integer id, String phoneNumber) {
        return userRepository.existsByIdAndPhoneNumber(id, phoneNumber);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void saveAllUser(List<User> list) {
        userRepository.saveAll(list);
    }

    @Override
    public PageRoles<RoleListDTO> getPageRole(Integer id) {
        List<RoleListDTO> roleListDTOList=userRepository.getRolesById(id);
        PageRoles<RoleListDTO>pageRoles=new PageRoles<>(roleListDTOList);
        return pageRoles;
    }


    @Override
    public boolean existByIdAndEmail(Integer id, String email) {
        return userRepository.existsByIdAndEmail(id, email);
    }


}
