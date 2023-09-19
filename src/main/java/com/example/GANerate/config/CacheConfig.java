package com.example.GANerate.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
/** @EnableCaching :해당 애플리케이션에서 캐싱을 이용하겠다는 명시를 처리해줘야 한다.
해당 어노테이션을 적용하게 되면 @Cacheable 라는 어노테이션이 적용된 메서드가 실행될 때 마다
AOP의 원리인 후처리 빈에 의해 해당 메소드에 프록시가 적용되어 캐시를 적용하는 부가기능이 추가되어 작동하게 된다.
 */
//@EnableCaching
//@Configuration
public class CacheConfig {

//    // ttl 가져오기
//    private final CacheProperties cacheProperties;
//
//    @Value("${spring.redis.host}")
//    private String redisHost;
//
//    @Value("${spring.redis.port}")
//    private int redisPort;
//
//    // Redis Client 설정(자바 표준은 Jedis이지만, 비동기적으로 우수한 성능을 자랑하는 Lettuce 사용)
//    @Bean(name = "redisCacheConnectionFactory") // 레디스를 관심사에 따라 여러개 만든다면 네이밍을 지정해야 한다.
//    public RedisConnectionFactory redisCacheConnectionFactory() {
//        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisHost, redisPort);
//        return lettuceConnectionFactory;
//    }
//
//    /*
//     * Jackson2는 Java8의 LocalDate의 타입을 알지못해서 적절하게 직렬화해주지 않는다.
//     * 때문에 역직렬화 시 에러가 발생한다.
//     * 따라서 적절한 ObjectMapper를 Serializer에 전달하여 직렬화 및 역직렬화를 정상화 시켰다.
//     */
//    private ObjectMapper objectMapper() {
//
//        // jackson 2.10이상 3.0버전까지 적용 가능
//        PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator
//                .builder().allowIfSubType(Object.class)
//                .build();
//
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        mapper.registerModule(new JavaTimeModule());
//        // GenericJackson2JsonRedisSerializer 직렬화시 클래스 타입을 함께 정보하는데,
//        // ObjectMapper의 경우 직렬/역직렬화시 클래스 타입을 포함하지 않으므로, 직렬화시 데이터에 type 정보가 존재하지 않음.
//        // 따라서 역직렬화 시 LinkedHashMap으로 역직렬화 되기 때문에 에러가 난다.
//        // 따라서 activateDefaultTyping를 통해 클래스 타입도 직/역직렬화 한다.
//        mapper.activateDefaultTyping(typeValidator, ObjectMapper.DefaultTyping.NON_FINAL);
//        return mapper;
//    }
//
//    //RedisCacheManager에 옵션을 부여할 수 있는 RedisCacheConfiguration 오브젝트
//    private RedisCacheConfiguration redisCacheDefaultConfiguration() {
//        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
//                .defaultCacheConfig()
//                .serializeKeysWith(RedisSerializationContext.SerializationPair
//                        .fromSerializer(new StringRedisSerializer()))
//                .serializeValuesWith(RedisSerializationContext.SerializationPair
//                        .fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper())));
//        return redisCacheConfiguration;
//    }
//
//    /*
//     * properties에서 가져온 캐시명과 ttl 값으로 RedisCacheConfiguration을 만들고 Map에 넣어 반환한다.
//     */
//    private Map<String, RedisCacheConfiguration> redisCacheConfigurationMap() {
//        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
//        for (Map.Entry<String, Long> cacheNameAndTimeout : cacheProperties.getTtl().entrySet()) {
//            cacheConfigurations
//                    .put(cacheNameAndTimeout.getKey(), redisCacheDefaultConfiguration().entryTtl(
//                            Duration.ofSeconds(cacheNameAndTimeout.getValue())));
//        }
//        return cacheConfigurations;
//    }
//
//    // Redis 서버 분리로 config가 여럿 잇으면, 아래와 같이 Bean name 옵션과 Qualifier로 명시하여 주입
//    @Bean
//    public CacheManager redisCacheManager(@Qualifier("redisCacheConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
//        RedisCacheManager redisCacheManager = RedisCacheManager.RedisCacheManagerBuilder
//                .fromConnectionFactory(redisConnectionFactory)
//                .cacheDefaults(redisCacheDefaultConfiguration())
//                .withInitialCacheConfigurations(redisCacheConfigurationMap()).build();
//        return redisCacheManager;
//    }
}
