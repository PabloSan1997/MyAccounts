# Mis cuentas

Esta aplicacion es para llevar mis cuentas personales

Esta carpetea ./myaccountsservice solo va a estar el backend de la app.

La realcion de las tablas es la siguiente

## Datos, relaciones y construccion de los entities

Para las relaciones @OneToMany y @ManyToOne  por favor usa un objectMapper

Para el manytomany es con jointable.

Relacion de datos usando dbdiagram.io

```dbml
Table the_user{
  id bigint [primary key]
  id_init_capital bigint
  username varchar(60) [unique]
  nickname varchar(60)
  password varchar(255)
}

Table user_role{
  id_user bigint [primary key]
  id_roles bigint [primary key]
}

Table the_role{
  id bigint [primary key]
  name varchar(10) [unique]
}

Table the_init_capital{
  id bigint [primary key]
  init_value bigdecimal
  created Date
}

Table the_periods{
  id bigint [primary key]
  id_user bigint
  created varchar(60)
}

Table fixed_costs{
  id bigint [primary key]
  id_period bigint
  date date
  value bigdecimal
}

Table fixed_income{
  id bigint [primary key]
  id_period bigint
  date date
  value bigdecimal
}

Table variable_costs{
  id bigint [primary key]
  id_period bigint
  date date
  value bigdecimal
}

Table variable_income{
  id bigint [primary key]
  id_period bigint
  date date
  value bigdecimal
}

REF : the_user.id < user_role.id_user
REF : the_role.id < user_role.id_roles
REF : the_user.id < the_periods.id_user
Ref : the_init_capital.id < the_user.id_init_capital
REF : the_periods.id < fixed_costs.id_period
REF : the_periods.id < fixed_income.id_period
REF : the_periods.id < variable_costs.id_period
REF : the_periods.id < variable_income.id_period
```

Recuerda utilizar lombock y todos los servicios repositories o componentes de spring que se necesite de la inyeccion de dependencias 
hazlo con @Autowired de ser posible, hay casos en los que no se puede.

## Sistema de Autenticación Double JWT (Access Token + Refresh Token)

### #Descripción General

Este proyecto implementa un sistema de autenticación robusto con **dos tokens JWT**:

Obviamente las librerias que esten en el proyecto pueden ser diferentes a las de este documento, ya que este agents.md es una plantilla de seguridad antes guardad y esta siendo editada en este momento. 

1. **Access Token**: Token de corto duración (7 minutos por defecto) que contiene los roles/autoridades del usuario. Se envía en el header `Authorization: Bearer <token>` de cada petición.

2. **Login Token** (Refresh Token): Token de larga duración (7 días por defecto) que se almacena en la base de datos asociado a la sesión de login. Se envía al cliente como cookie HTTP-only.

### Flujo de Autenticación

#### 1. Login (`POST /api/user/login`)
```
Request:
{
  "username": "usuario",
  "password": "contraseña"
}

Response (200 OK):
- Header: Set-Cookie con el login token (cookie "the_cookie", httpOnly, duracion 7 dias)
- Body: { "token": "access_token" }
```

#### 2. Peticiones Protegidas
Cada petición debe incluir:
```
Authorization: Bearer <access_token>
```

#### 3. Refresh (`POST /api/user/refresh`)
Cuando el access token expira (después de 7 minutos), el cliente debe llamar a este endpoint:
- Envía automáticamente la cookie "the_cookie" con el login token
- Response: `{ "token": "nuevo_access_token" }`

#### 4. Logout (`POST /api/user/logout`)
- Invalida la sesión en la base de datos
- Elimina la cookie

### Estructura del Proyecto

```
src/main/java/com/paquete/proyecto/
├── components/
│   └── PropsSesionComponent.java       # Configuración de tiempos y cookies
├── controllers/
│   ├── UserController.java             # Endpoints: login, refresh, logout, userinfo
│   └── ExceptionController.java        # Manejo centralizado de excepciones
├── exceptions/
│   ├── RefreshException.java           # Excepción para token expirado (requiere refresh)
│   ├── ReLodingException.java         # Excepción para token inválido (requiere login)
│   └── MyBadRequestException.java      # Excepción para errores de validación
├── models/
│   ├── dtos/
│   │   ├── LoginDto.java               # DTO para login (username, password)
│   │   ├── JwtDto.java                 # DTO para respuesta de token
│   │   ├── DoubleJwtDto.java           # DTO para login (access + login token)
│   │   ├── UserDetailsDto.java        # DTO para detalles de usuario (implementa UserDetails)
│   │   ├── LoginClaimsDto.java        # DTO para claims del login token
│   │   ├── UserInfoDto.java            # DTO para información del usuario
│   │   └── ErrorDto.java               # DTO para errores
│   └── entities/
│       ├── UserEntity.java             # Entidad de usuario (JPA)
│       ├── LoginEntity.java            # Entidad de sesión de login (JPA)
│       └── RoleEntity.java             # Entidad de roles (JPA)
├── repositories/
│   ├── LoginRepository.java            # Repository para LoginEntity
│   ├── UserRepository.java             # Repository para UserEntity
│   └── RoleRepository.java             # Repository para RoleEntity
├── security/
│   ├── SecurityConfig.java             # Configuración de Spring Security
│   └── filter/
│       └── JwtValidationTokenFilter.java # Filtro para validar access token
└── services/
    ├── JwtService.java                 # Interfaz del servicio JWT
    ├── UserService.java                # Interfaz del servicio de usuario
    └── imp/
        ├── JwtServiceImp.java         # Implementación del servicio JWT
        ├── UserServiceImp.java        # Implementación del servicio de usuario
        └── UserDetailsServiceImp.java # Implementación de UserDetailsService
```

### application.properties

```properties
spring.application.name=servicio
server.port=3000

spring.datasource.url=jdbc:postgresql://localhost:5432/nombre_db
spring.datasource.username=usuario
spring.datasource.password=contraseña

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Keys JWT en Base64 (mínimo 256 bits = 32 bytes)
jwt.accesstoken.key={aqui genera letras al azar}
jwt.logintoken.key={aqui genera letras al azar}
```

### Dependencias Maven (pom.xml)

**IMPORTANTE**: Estas son las dependencias mínimas necesarias que DEBEN existir. NO borrar las dependencias existentes del proyecto.

```xml
<dependencies>
    <!-- AGREGAR estas dependencias si no existen -->

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- Opcional: Si usas JPA con MySQL/PostgreSQL -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Opcional: Si usas MongoDB (NO usar con JPA) -->
    <!--
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-mongodb</artifactId>
    </dependency>
    -->
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.12.5</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.12.5</version>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

---

## Implementación Paso a Paso

### Paso 1: Entidades JPA

#### RoleEntity.java
```java
@Entity
@Table(name = "the_roles")
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private List<UserEntity> users;
}
```

#### UserEntity.java
```java
@Entity
@Table(name = "the_users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    private String password;
    private String nickname;

    @ManyToMany
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"),
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "role_id"})}
    )
    private List<RoleEntity> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoginEntity> logins;
}
```

#### LoginEntity.java
```java
@Entity
@Table(name = "login")
public class LoginEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 500)
    private String jwt;
    private Instant created;
    private Instant updated;
    private Boolean active;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private UserEntity user;

    @PrePersist
    public void prePersist(){
        this.created = Instant.now();
        this.updated = Instant.now();
        this.active = true;
    }
    @PreUpdate
    public void preUpdate(){
        this.updated = Instant.now();
    }
}
```

### Paso 2: DTOs

#### LoginDto.java
```java
@Data
public class LoginDto {
    @Size(min = 1, max = 50)
    @NotBlank
    private String username;
    @Size(min = 1, max = 250)
    @NotBlank
    private String password;
}
```

#### JwtDto.java
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtDto {
    private String token;
}
```

#### DoubleJwtDto.java
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoubleJwtDto {
    private String accessToken;
    private String loginToken;
}
```

#### LoginClaimsDto.java
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginClaimsDto {
    private Long idLogin;
    private String username;
}
```

#### UserInfoDto.java
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {
    private String username;
    private String nickname;
}
```

#### ErrorDto.java
```java
@Data
@NoArgsConstructor
public class ErrorDto {
    private String message;
    private Integer statusCode;
    private String error;

    public ErrorDto(HttpStatus status, String message){
        this.message = message;
        this.statusCode = status.value();
        this.error = status.getReasonPhrase();
    }

    public Date getTimestamp(){
        return new Date();
    }
}
```

#### UserDetailsDto.java
```java
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailsDto implements UserDetails {

    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    @Getter
    private UserEntity user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setAuthoritiesAsRoles(List<RoleEntity> roles) {
        authorities = roles.stream()
                .map(p -> new SimpleGrantedAuthority("ROLE_"+p.getName())).toList();
    }
}
```

### Paso 3: Repositories

#### UserRepository.java
```java
public interface UserRepository extends CrudRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
}
```

#### LoginRepository.java
```java
public interface LoginRepository extends CrudRepository<LoginEntity, Long> {
    @Query("select u from LoginEntity u where u.id=:id and u.user.username=:username")
    Optional<LoginEntity> findByIdAndUsername(@Param("id") Long id, @Param("username") String username);
}
```

#### RoleRepository.java
```java
public interface RoleRepository extends CrudRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(String name);
}
```

### Paso 4: Excepciones

#### RefreshException.java
```java
public class RefreshException extends RuntimeException{
    public RefreshException(){
        super("refresh");
    }
}
```

#### ReLodingException.java
```java
public class ReLodingException extends RuntimeException {
    public ReLodingException() {
        super("Vuelva a iniciar seccion");
    }
}
```

#### MyBadRequestException.java
```java
public class MyBadRequestException extends RuntimeException{
    public MyBadRequestException(String message){
        super(message);
    }
    public MyBadRequestException(){}
}
```

### Paso 5: Componente de Configuración

#### PropsSesionComponent.java
```java
@Component
public class PropsSesionComponent {

    private Long accesstime = 1000 * 60 * 7L;           // 7 minutos
    private Long loginTime = 1000 * 60 * 60 * 24 * 7L; // 7 días
    @Getter
    private String sameStite = "Lax";
    @Getter
    private Boolean security = false;
    @Getter
    private String path = "";
    @Getter
    private Boolean httpOnly = false;

    public Date getAccesstime() {
        return new Date(System.currentTimeMillis() + accesstime);
    }

    public Date getLoginTime() {
        return new Date(System.currentTimeMillis() + loginTime);
    }

    public Long getLoginTimeCookie() {
        return loginTime/1000;
    }
}
```

### Paso 6: Servicios JWT

#### JwtService.java (Interfaz)
```java
public interface JwtService {
    String accessToken(UserDetailsDto userDetailsDto);
    UserDetailsDto validationAccessToken(String token);
    String loginToken(UserEntity user);
    LoginClaimsDto validationLoginToken(String token);
    void logout(String token);
}
```

#### JwtServiceImp.java (Implementación)
```java
@Service
@Transactional
public class JwtServiceImp implements JwtService {

    @Value("${jwt.accesstoken.key}")
    private String accesskey;
    @Value("${jwt.logintoken.key}")
    private String logintokenkey;

    @Autowired
    private PropsSesionComponent component;
    @Autowired
    private LoginRepository loginRepository;

    private SecretKey getAccessKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(accesskey));
    }
    private SecretKey getLoginKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(logintokenkey));
    }

    @Override
    public String accessToken(UserDetailsDto userDetailsDto) {
        List<String> authorities = userDetailsDto.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();
        String username = userDetailsDto.getUsername();
        Claims claims = Jwts.claims().add("authorities", authorities).build();
        return Jwts.builder().signWith(getAccessKey())
                .subject(username)
                .claims(claims)
                .issuedAt(new Date())
                .expiration(component.getAccesstime())
                .compact();
    }

    @Override
    public UserDetailsDto validationAccessToken(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(getAccessKey()).build()
                    .parseSignedClaims(token).getPayload();
            @SuppressWarnings("unchecked")
            List<String> authoritiesname = (List<String>) claims.get("authorities");
            Collection<? extends GrantedAuthority> authorities = authoritiesname.stream()
                    .map(SimpleGrantedAuthority::new).toList();
            String username = claims.getSubject();
            return UserDetailsDto.builder()
                    .username(username).authorities(authorities).build();
        } catch (ExpiredJwtException e) {
            throw new RefreshException();
        }catch (Exception e){
            throw new ReLodingException();
        }
    }

    @Override
    public String loginToken(UserEntity user) {
        LoginEntity loginEntity = LoginEntity.builder().user(user).build();
        LoginEntity newLoginEntity = loginRepository.save(loginEntity);
        Claims claims = Jwts.claims().add("id", String.valueOf(newLoginEntity.getId())).build();
        String token = Jwts.builder().signWith(getLoginKey())
                .claims(claims)
                .issuedAt(new Date())
                .expiration(component.getLoginTime())
                .subject(user.getUsername()).compact();
        newLoginEntity.setJwt(token);
        return token;
    }

    @Override
    public LoginClaimsDto validationLoginToken(String token) {
        LoginEntity loginEntity = getLoginEntity(token);
        return LoginClaimsDto.builder()
                .username(loginEntity.getUser().getUsername())
                .idLogin(loginEntity.getId()).build();
    }

    @Override
    public void logout(String token) {
        LoginEntity loginEntity = getLoginEntity(token);
        loginEntity.setActive(false);
    }

    private LoginEntity getLoginEntity(String token) {
       try{
           var claims = Jwts.parser().verifyWith(getLoginKey()).build()
                   .parseSignedClaims(token).getPayload();
           Long idlogin = Long.parseLong((String) claims.get("id"));
           String username = claims.getSubject();
           Optional<LoginEntity> loginEntity = loginRepository.findByIdAndUsername(idlogin, username);
           if(loginEntity.isEmpty() || !loginEntity.get().getActive())
               throw new ReLodingException();
           return loginEntity.get();
       }catch (ExpiredJwtException e){
           throw new ReLodingException();
       }
    }
}
```

### Paso 7: UserDetailsService

#### UserDetailsServiceImp.java
```java
@Service
@Transactional
public class UserDetailsServiceImp implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        UserDetailsDto userDetailsDto = UserDetailsDto.builder()
                .user(user).username(username).password(user.getPassword()).build();
        userDetailsDto.setAuthoritiesAsRoles(user.getRoles());
        return userDetailsDto;
    }
}
```

### Paso 8: Servicios de Usuario

#### UserService.java (Interfaz)
```java
public interface UserService{
    DoubleJwtDto login(LoginDto loginDto);
    UserInfoDto getUserInfo();
    void logout(String token);
    JwtDto refreshToken(String token);
}
```

#### UserServiceImp.java (Implementación)
```java
@Service
@Transactional
public class UserServiceImp implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public DoubleJwtDto login(LoginDto loginDto) {
        Authentication authtoken = new UsernamePasswordAuthenticationToken(
            loginDto.getUsername(), loginDto.getPassword());
        try{
            UserDetailsDto userDetailsDto = (UserDetailsDto) authenticationManager
                .authenticate(authtoken).getPrincipal();
            assert userDetailsDto != null;
            UserEntity user = userDetailsDto.getUser();
            String jwt = jwtService.accessToken(userDetailsDto);
            String loginToken = jwtService.loginToken(user);
            return new DoubleJwtDto(jwt, loginToken);
        }catch (Exception ex){
            throw new MyBadRequestException("Incorrect username or password");
        }
    }

    @Override
    public UserInfoDto getUserInfo() {
        String username = (String) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(ReLodingException::new);
        return new UserInfoDto(username, user.getNickname());
    }

    @Override
    public void logout(String token) {
        jwtService.logout(token);
    }

    @Override
    public JwtDto refreshToken(String token) {
        LoginClaimsDto claimsDto = jwtService.validationLoginToken(token);
        UserEntity user = userRepository.findByUsername(claimsDto.getUsername())
            .orElseThrow(ReLodingException::new);
        UserDetailsDto userDetailsDto = UserDetailsDto.builder()
            .username(claimsDto.getUsername()).build();
        userDetailsDto.setAuthoritiesAsRoles(user.getRoles());
        String jwt = jwtService.accessToken(userDetailsDto);
        return new JwtDto(jwt);
    }
}
```

### Paso 9: Filtro de Seguridad

#### JwtValidationTokenFilter.java
```java
public class JwtValidationTokenFilter extends BasicAuthenticationFilter {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtValidationTokenFilter(AuthenticationManager authenticationManager, 
            JwtService jwtService) {
        super(authenticationManager);
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
            HttpServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }
        String token = header.replace("Bearer ", "");

        try{
            UserDetailsDto userDetails = jwtService.validationAccessToken(token);
            String username = userDetails.getUsername();
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            Authentication authenticationtoken = new UsernamePasswordAuthenticationToken(
                username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationtoken);
            chain.doFilter(request, response);
        }
        catch (RefreshException e){
            ErrorDto errorDto = new ErrorDto(HttpStatus.UNAUTHORIZED, e.getMessage());
            response.setStatus(errorDto.getStatusCode());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(errorDto));
        }
        catch (Exception e){
            chain.doFilter(request, response);
        }
    }
}
```

### Paso 10: Configuración de Seguridad

#### SecurityConfig.java
```java
@Configuration
public class SecurityConfig {

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwtService) 
            throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(a -> a
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/user/login",
                    "/api/user/refresh",
                    "/api/user/logout"
                ).permitAll()
                .requestMatchers("/api/user/userinfo").hasRole("USER")
                .anyRequest().authenticated()
            )
            .addFilter(new JwtValidationTokenFilter(authenticationManager(), jwtService))
            .cors(c->c.configurationSource(corsConfigurationSource()))
            .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

### Paso 11: Controlador

#### UserController.java
```java
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private PropsSesionComponent component;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDto loginDto) {
        var res = userService.login(loginDto);
        JwtDto jwtDto = new JwtDto(res.getAccessToken());
        ResponseCookie cookie = ResponseCookie.from("the_cookie", res.getLoginToken())
                .sameSite(component.getSameStite())
                .httpOnly(component.getHttpOnly())
                .secure(component.getSecurity())
                .maxAge(component.getLoginTimeCookie())
                .path(component.getPath()).build();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().headers(headers).body(jwtDto);
    }

    @GetMapping("/userinfo")
    public ResponseEntity<?> getUserInfo() {
        return ResponseEntity.ok(userService.getUserInfo());
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(name = "the_cookie", required = false) 
            String cookie) {
        if (cookie == null || cookie.isEmpty()) throw new ReLodingException();
        return ResponseEntity.ok(userService.refreshToken(cookie));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "the_cookie", required = false) 
            String cookie) {
        if (cookie == null || cookie.isEmpty()) throw new ReLodingException();
        userService.logout(cookie);
        ResponseCookie responseCookie = ResponseCookie.from("the_cookie", "")
                .sameSite(component.getSameStite())
                .httpOnly(component.getHttpOnly())
                .secure(component.getSecurity())
                .maxAge(0)
                .path(component.getPath()).build();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, responseCookie.toString());
        return ResponseEntity.noContent().headers(headers).build();
    }
}
```

### Paso 12: Manejo de Excepciones

#### ExceptionController.java
```java
@RestControllerAdvice
public class ExceptionController {

    @Autowired
    private PropsSesionComponent component;

    @ExceptionHandler({
        ReLodingException.class,
        RefreshException.class
    })
    public ResponseEntity<?> unauthorized(Exception e) {
        ErrorDto errorDto = new ErrorDto(HttpStatus.UNAUTHORIZED, e.getMessage());
        
        if (e instanceof ReLodingException) {
            ResponseCookie cookie = ResponseCookie.from("the_cookie", "")
                    .sameSite(component.getSameStite())
                    .httpOnly(component.getHttpOnly())
                    .secure(component.getSecurity())
                    .maxAge(0)
                    .path(component.getPath()).build();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        }

        return ResponseEntity.status(errorDto.getStatusCode()).body(errorDto);
    }

    @ExceptionHandler({
        MyBadRequestException.class,
        MethodArgumentNotValidException.class
    })
    public ResponseEntity<?> badRequest(Exception e) {
        ErrorDto errorDto = new ErrorDto(HttpStatus.BAD_REQUEST, e.getMessage());
        
        if(e instanceof MethodArgumentNotValidException err){
            StringBuilder stringBuilder = new StringBuilder();
            for(FieldError field: err.getFieldErrors()){
                stringBuilder.append(field.getField()).append(": ")
                        .append(field.getDefaultMessage()).append(". ");
            }
            errorDto.setMessage(stringBuilder.toString().trim());
        }

        return ResponseEntity.status(errorDto.getStatusCode()).body(errorDto);
    }
}
```

---

### Respuestas de Error

| Código | Mensaje | Acción del Cliente |
|--------|---------|-------------------|
| 401 | "refresh" | Llamar a `/api/user/refresh` para obtener nuevo access token |
| 401 | "Vuelva a iniciar seccion" | Redirigir a pantalla de login |
| 400 | Mensaje de validación | Mostrar errores de validación al usuario |

### Notas Importantes

1. **BCrypt**: El sistema usa BCrypt con factor 12 para hashear contraseñas.
2. **Stateless**: No usa sesiones de servidor, es completamente stateless (excepto el almacenamiento del login token en BD).
3. **CORS**: Configuración permisiva (`*`), ajustar para producción.
4. **Cookies**: El login token se envía en cookie, configurable como httpOnly, secure, sameSite.
5. **Tiempos**: Modificar en `PropsSesionComponent` según necesidades:
   - `accesstime`: Duración del access token (default 7 minutos)
   - `loginTime`: Duración del login token (default 7 días)
6. **Keys JWT**: Deben ser mínimo 256 bits (32 bytes) y codificadas en Base64.
7. **Roles**: Se almacenan con prefijo "ROLE_" en los authorities.
