package great.job.mytamin.Service;

import great.job.mytamin.Repository.UserRepository;
import great.job.mytamin.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static great.job.mytamin.exception.ErrorMessage.USER_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND.getMsg()));
    }

}
