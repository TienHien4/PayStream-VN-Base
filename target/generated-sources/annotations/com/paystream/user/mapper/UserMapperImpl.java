package com.paystream.user.mapper;

import com.paystream.user.dto.UserProfileResponse;
import com.paystream.user.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-16T15:26:46+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserProfileResponse toProfileResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserProfileResponse userProfileResponse = new UserProfileResponse();

        userProfileResponse.setPermissions( rolesToPermissionCodes( user.getRoles() ) );
        userProfileResponse.setRoles( rolesToRoleDtos( user.getRoles() ) );
        userProfileResponse.setRoleCodes( rolesToRoleCodes( user.getRoles() ) );
        userProfileResponse.setId( user.getId() );
        userProfileResponse.setMerchantId( user.getMerchantId() );
        userProfileResponse.setEmail( user.getEmail() );
        userProfileResponse.setFullName( user.getFullName() );
        userProfileResponse.setPhone( user.getPhone() );
        userProfileResponse.setStatus( user.getStatus() );
        userProfileResponse.setCreatedAt( user.getCreatedAt() );

        return userProfileResponse;
    }
}
