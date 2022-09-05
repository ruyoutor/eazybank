package com.eazybytes.config;

import com.eazybytes.model.Customer;
import com.eazybytes.repository.CustomerRepository;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EazyBankUsernamePwdAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private CustomerRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String pwd = authentication.getCredentials().toString();
        List<Customer> customers = repository.findByEmail(username);

        if (!customers.isEmpty()){
            var customer = customers.get(0);
            if (passwordEncoder.matches(pwd, customer.getPwd())){
                List<GrantedAuthority> authorities = Lists.newArrayList(new SimpleGrantedAuthority(customer.getRole()));
                return new UsernamePasswordAuthenticationToken(username, pwd, authorities);
            }
        }
        throw new BadCredentialsException("Username or password is invalid");
    }

    @Override
    public boolean supports(Class<?> authenticationType) {
        return authenticationType.equals(UsernamePasswordAuthenticationToken.class);
    }
}
