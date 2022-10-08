package great.job.mytamin.topic.user.service;

import great.job.mytamin.topic.user.repository.UserRepository;
import great.job.mytamin.topic.user.entity.User;
import great.job.mytamin.global.exception.MytaminException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static great.job.mytamin.global.exception.ErrorMap.USER_NOT_FOUND_ERROR;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new MytaminException(USER_NOT_FOUND_ERROR));
    }

}
