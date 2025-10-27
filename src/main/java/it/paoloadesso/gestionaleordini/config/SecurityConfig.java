package it.paoloadesso.gestionaleordini.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails mario = User.builder()
                .username("mario")
                .password(passwordEncoder().encode("cameriere"))
                .roles("CAMERIERE")
                .build();

        UserDetails giulia = User.builder()
                .username("giulia")
                .password(passwordEncoder().encode("cameriere"))
                .roles("CAMERIERE")
                .build();

        UserDetails sara = User.builder()
                .username("sara")
                .password(passwordEncoder().encode("cameriere"))
                .roles("CAMERIERE")
                .build();

        UserDetails chef = User.builder()
                .username("chef")
                .password(passwordEncoder().encode("cuoco"))
                .roles("CUOCO")
                .build();

        UserDetails luca = User.builder()
                .username("luca")
                .password(passwordEncoder().encode("cuoco"))
                .roles("CUOCO")
                .build();

        UserDetails antonio = User.builder()
                .username("antonio")
                .password(passwordEncoder().encode("cuoco"))
                .roles("CUOCO")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin"))
                .roles("CAMERIERE", "CUOCO")
                .build();

        return new InMemoryUserDetailsManager(mario, giulia, sara, chef, luca, antonio, admin);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // ✅ TUTTO PUBBLICO tranne le API specifiche
                        .requestMatchers("/ordini/**").hasAnyRole("CAMERIERE", "CUOCO")
                        .requestMatchers("/prodotti/**").hasAnyRole("CAMERIERE", "CUOCO")
                        .requestMatchers("/tavoli/**").hasAnyRole("CAMERIERE", "CUOCO")

                        // ✅ TUTTO IL RESTO È PUBBLICO (incluso Swagger)
                        .anyRequest().permitAll()
                )
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
