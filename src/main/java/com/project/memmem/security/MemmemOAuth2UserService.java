package com.project.memmem.security;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.project.memmem.domain.entity.Role;
import com.project.memmem.domain.entity.UserEntity;
import com.project.memmem.domain.repository.UserEntityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemmemOAuth2UserService extends DefaultOAuth2UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserEntityRepository userRepository;

    private static final Random RANDOM = new Random(); // Random 객체를 재사용합니다.

    // 소셜 로그인 시 사용자의 정보를 로드하는 메서드
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // OAuth2 인증 서버에서 사용자 정보를 가져옴
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        return processSocialLogin(oAuth2User, registrationId);
    }

    // 소셜 로그인 처리 메서드: 사용자가 이미 존재하는지 확인하고, 없으면 새로 생성
    private OAuth2User processSocialLogin(OAuth2User oAuth2User, String registrationId) {
        String email = extractEmail(oAuth2User, registrationId);
        String name = extractName(oAuth2User, registrationId);

        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            String nickName = generateUniqueNickName(name); // 닉네임 생성
            user = createSocialUser(email, name, nickName);
        }

        return new MemmemUserDetails(user);
    }

    private UserEntity createSocialUser(String email, String name, String nickName) {
        UserEntity entity = UserEntity.builder()
                .name(name)
                .birthDate("미입력")
                .address("미입력")
                .number("미입력")
                .email(email)
                .password(passwordEncoder.encode(String.valueOf(System.currentTimeMillis())))
                .nickName(nickName)
                .role(Role.USER)
                .build();

        return userRepository.save(entity);
    }

    private String generateUniqueNickName(String baseName) {
        String nickName;
        do {
            nickName = baseName + generateRandomNumber(); // 랜덤 숫자를 생성하여 닉네임을 만듭니다.
        } while (!isUniqueNickName(nickName)); // 중복되지 않을 때까지 반복합니다.
        return nickName;
    }

    private int generateRandomNumber() {
        return RANDOM.nextInt(9000) + 1000; // 1000부터 9999까지의 랜덤 숫자 생성
    }

    private boolean isUniqueNickName(String nickName) {
        return userRepository.findByNickName(nickName).isEmpty(); // Optional<UserEntity>를 사용하여 유니크 여부를 확인합니다.
    }

    // 소셜 로그인 플랫폼에 따라 이메일 정보를 추출하는 메서드
    private String extractEmail(OAuth2User oAuth2User, String registrationId) {
        if ("google".equals(registrationId)) {
            return oAuth2User.getAttribute("email");
        } else if ("naver".equals(registrationId)) {
            Map<String, Object> response = oAuth2User.getAttribute("response");
            return String.valueOf(response.get("email"));
        } else if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
            return String.valueOf(kakaoAccount.get("email"));
        }
        throw new OAuth2AuthenticationException("Unsupported registration ID");
    }

    // 소셜 로그인 플랫폼에 따라 사용자 이름을 추출하는 메서드
    private String extractName(OAuth2User oAuth2User, String registrationId) {
        if ("google".equals(registrationId)) {
            return oAuth2User.getAttribute("name");
        } else if ("naver".equals(registrationId)) {
            Map<String, Object> response = oAuth2User.getAttribute("response");
            return String.valueOf(response.get("name"));
        } else if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            return String.valueOf(profile.get("nickname"));
        }
        throw new OAuth2AuthenticationException("Unsupported registration ID");
    }
}
