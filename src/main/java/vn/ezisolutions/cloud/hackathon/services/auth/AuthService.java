package vn.ezisolutions.cloud.hackathon.services.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import vn.ezisolutions.cloud.hackathon.core.common.AbstractCacheService;
import vn.ezisolutions.cloud.hackathon.core.common.AuthorizedUser;
import vn.ezisolutions.cloud.hackathon.core.common.BaseResponse;
import vn.ezisolutions.cloud.hackathon.core.common.RedisClient;
import vn.ezisolutions.cloud.hackathon.core.exceptions.CustomValidationException;
import vn.ezisolutions.cloud.hackathon.core.shared.RedisKeys;
import vn.ezisolutions.cloud.hackathon.core.utils.StringUtils;
import vn.ezisolutions.cloud.hackathon.dto.auth.AppAuthResponse;
import vn.ezisolutions.cloud.hackathon.dto.auth.IdSystemAuthExchangeRequest;
import vn.ezisolutions.cloud.hackathon.dto.redis.BizUser;
import vn.ezisolutions.cloud.hackathon.entities.RoleEntity;
import vn.ezisolutions.cloud.hackathon.entities.UserEntity;
import vn.ezisolutions.cloud.hackathon.properties.IdSystemProperties;
import vn.ezisolutions.cloud.hackathon.repositories.jpa.RoleRepository;
import vn.ezisolutions.cloud.hackathon.repositories.jpa.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class AuthService extends AbstractCacheService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ObjectMapper mapper;

    private final RestClient idSystemRestClient;
    private final IdSystemProperties idSystemProperties;

    public AuthService(RedisClient client, UserRepository userRepository, RoleRepository roleRepository, ObjectMapper mapper, RestClient idSystemRestClient, IdSystemProperties idSystemProperties) {
        super(client, "eziops:auth");
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.mapper = mapper;
        this.idSystemRestClient = idSystemRestClient;
        this.idSystemProperties = idSystemProperties;
    }

    public UserEntity getUser(String id) {
        return getHashMap("users", id, UserEntity.class);
    }

    public void cacheUser(UserEntity source) {
        cacheHashMap("users", source.getId().toString(), source, 7 * 24 * 60 * 60L);
    }

    @Transactional
    public AuthorizedUser getUserByToken(String token) {
        try {
            BizUser bizUser = getClient().hGet(RedisKeys.TOKEN_KEY, token, BizUser.class);
            if (bizUser == null) {
                return null;
            }
            String uuidStr = bizUser.getId();
            UserEntity user = getUser(uuidStr);
            if (user == null) {
                //khong co trong cache
                UserEntity dbUser = userRepository.findById(UUID.fromString(uuidStr)).orElse(null);
                if (dbUser == null) {
                    //tim theo id khong thay
                    UserEntity newUser = UserEntity.builder().id(UUID.fromString(uuidStr)).name(bizUser.getName()).username(bizUser.getId()).email(bizUser.getEmail()).rememberToken(StringUtils.random(128)).isActive(false).build();
                    roleRepository.findByName("member").ifPresent(role -> newUser.setRoles(new HashSet<>(List.of(role))));
                    userRepository.save(newUser);
                    throw new Exception("not found user");
                } else {
                    //tim thay trong db
                    if (!dbUser.getIsActive()) {
                        throw new Exception("User not active");
                    }

                    if (dbUser.getRoles().isEmpty()) {
                        throw new Exception("User not permission");
                    }
                    List<String> roleList = dbUser.getRoles().stream().map(RoleEntity::getName).toList();
                    dbUser.setRoleList(roleList);
                    cacheUser(dbUser);
                    user = dbUser;
                }
            }

            return AuthorizedUser.builder().id(user.getId().toString()).name(user.getName()).username(user.getName()).roles(user.getRoleList()).build();
        } catch (Exception e) {
            log.error("getUserByToken", e);
            return null;
        }
    }

    public AuthorizedUser getUserByApiToken(String apiToken) {
        try {
            String userId = getClient().hGet(RedisKeys.API_TOKEN_KEY, apiToken, String.class);
            UserEntity user;
            if (userId == null) {
                UserEntity dbUser = userRepository.findByRememberToken(apiToken).orElseThrow(() -> new Exception("not found user"));
                userId = dbUser.getId().toString();
                getClient().hSet(RedisKeys.API_TOKEN_KEY, apiToken, userId);
            }
            user = getUser(userId);
            if (user == null) {
                //khong co trong cache
                throw new Exception("not found user");
            }

            return AuthorizedUser.builder().id(userId).name(user.getName()).username(user.getName()).roles(user.getRoles().stream().map(RoleEntity::getName).toList()).build();
        } catch (Exception e) {
            log.error("getUserByApiToken", e);
            return null;
        }
    }

    public AppAuthResponse getExchange(String code, String clientId, String deviceId) {
        //validate request
        if (StringUtils.isEmpty(code)) {
            throw new CustomValidationException("Code is empty", null);
        }

        if (StringUtils.isEmpty(clientId)) {
            throw new CustomValidationException("clientId is empty", null);
        }

        if (StringUtils.isEmpty(deviceId)) {
            throw new CustomValidationException("deviceId is empty", null);
        }
        UUID deviceUUID;
        try {
            deviceUUID = UUID.fromString(deviceId);
        } catch (IllegalArgumentException e) {
            throw new CustomValidationException("deviceId invalid", null);
        }

        //call API exchange ID system
        BaseResponse response = idSystemRestClient.post().uri(idSystemProperties.getBaseUrl() + "/auth_exchange")
                .contentType(MediaType.APPLICATION_JSON)
                .body(IdSystemAuthExchangeRequest.builder()
                        .code(code)
                        .clientId(clientId)
                        .deviceId(deviceUUID)
                        .build()).retrieve()
                .body(BaseResponse.class);
        if (response.getStatus() == 1) {
            return mapper.convertValue(response.getData(), AppAuthResponse.class);
        } else {
            throw new CustomValidationException(response.getMessage(), null);
        }
    }
}
