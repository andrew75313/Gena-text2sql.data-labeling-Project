package org.example.datalabelingtool.global.security;

import lombok.RequiredArgsConstructor;
import org.example.datalabelingtool.domain.users.entity.User;
import org.example.datalabelingtool.domain.users.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameAndIsActiveTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("Can't find user."));

        return new UserDetailsImpl(user);
    }

    public UserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByIdAndIsActiveTrue(userId)
        .orElseThrow(() -> new UsernameNotFoundException("Can't find user."));

        return new UserDetailsImpl(user);
    }
}
