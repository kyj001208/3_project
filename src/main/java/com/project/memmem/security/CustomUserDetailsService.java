package com.project.memmem.security;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.project.memmem.domain.entity.UserEntity;
import com.project.memmem.domain.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	
    private final UserEntityRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    	
    	System.out.println(">>>>>>email:"+email);
        UserEntity user = userRepository.findByEmail(email);

        return new MemmemUserDetails(user);
    }
}