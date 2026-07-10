package duoc.sumativa.transportes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Deshabilitar CSRF 
            .csrf(csrf -> csrf.disable())
            
            // 2. Asegurar que la API no guarde estado de sesión (Stateless)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 3. Reglas de autorización específicas para tus endpoints
            .authorizeHttpRequests(authorize -> authorize
                // Regla para descargar: requiere explícitamente el rol de descarga
                .requestMatchers(HttpMethod.GET, "/api/transportes/guias/descargar").hasAuthority("ROLE_DESCARGA")
                
                // Regla general: listar o cualquier acción en transportes requiere ser ADMIN
                .requestMatchers("/api/transportes/**").hasAuthority("ROLE_ADMIN")
                
                // Cualquier otra petición fuera de ese path debe estar simplemente autenticada
                .anyRequest().authenticated()
            )
            
            // 4. Resource Server pasándole nuestro extractor de puesto (jobTitle)
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );

        return http.build();
    }

    /**
     * Mapeador encargado de extraer el claim 'jobTitle' del token de Azure B2C
     * y transformarlo en una autoridad ejecutable por Spring Security.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        
        // Extraemos el puesto directo del token ("jobTitle")
        grantedAuthoritiesConverter.setAuthoritiesClaimName("jobTitle");
        
        // Le anteponemos el prefijo ROLE_ para que "ADMIN" sea interpretado como "ROLE_ADMIN"
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}