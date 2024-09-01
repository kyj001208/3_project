package com.project.memmem.security;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import com.project.memmem.domain.entity.UserEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import lombok.Getter;
@Getter
public class CustomUserDetails extends User {
    private static final long serialVersionUID = 1L;
    private final String email;
    private final String name;
    private final String nickName;
    private final UserEntity userEntity;
    private final long userId;
    private final String password;
    private final String number; // 수정된 부분
    private final Map<String, Object> attributes;
    public CustomUserDetails(UserEntity entity) {
        super(entity.getEmail(), entity.getPassword(),
              Set.of(new SimpleGrantedAuthority("ROLE_" + entity.getRole().name())));
        this.email = entity.getEmail();
        this.name = entity.getName();
        this.nickName = entity.getNickName();
        this.userEntity = entity;
        this.userId = entity.getUserId();
        this.password = entity.getPassword();
        this.number = entity.getNumber(); // 수정된 부분
        this.attributes = Map.of(); // 초기화 (필요에 따라 수정 가능)
    }
    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public String getUsername() {
        return email;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true; // 필요에 따라 수정
    }
    @Override
    public boolean isAccountNonLocked() {
        return true; // 필요에 따라 수정
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 필요에 따라 수정
    }
    @Override
    public boolean isEnabled() {
        return true; // 필요에 따라 수정
    }
}