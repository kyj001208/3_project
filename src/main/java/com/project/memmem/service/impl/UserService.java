package com.project.memmem.service.impl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.memmem.domain.dto.SignupDTO;
import com.project.memmem.domain.entity.UserEntity;
import com.project.memmem.domain.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService{

    private final UserEntityRepository userEntityRepository;
    /*
    //참고서적 코드
    public UserEntity saveUser(UserEntity user) {
        checkUser(user);
        return userEntityRepository.save(user);
    }
     */
    public void saveUser(SignupDTO dto,PasswordEncoder pe) {
        checkUser(dto.toEntity(pe));
        userEntityRepository.save(dto.toEntity(pe));
    }
    private void checkUser(UserEntity user) {
        UserEntity findUser = userEntityRepository.findByEmail(user.getEmail());
        if(findUser != null) {
            throw new IllegalStateException("이미 있는 이메일입니다.");
        }
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userEntityRepository.findByEmail(email);

        if(user==null) {
            throw new UsernameNotFoundException(email);
        }

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().toString())
                .build();
    }
}