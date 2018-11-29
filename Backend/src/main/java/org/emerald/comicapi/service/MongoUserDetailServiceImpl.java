package org.emerald.comicapi.service;

import org.emerald.comicapi.model.data.User;
import org.emerald.comicapi.model.form.UserForm;
import org.emerald.comicapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("userDetailsService")
public class MongoUserDetailServiceImpl implements MongoUserDetailService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optional = userRepository.findByUsername(username);
        return optional.orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }

    @Override
    public void hashPassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    @Override
    public User register(UserForm form, String role) {
        User user = new User(form);
        hashPassword(user);
        user.addRole(role);
        return userRepository.insert(user);
    }
}
