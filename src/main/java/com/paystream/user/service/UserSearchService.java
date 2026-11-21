package com.paystream.user.service;

import com.paystream.user.document.UserDocument;
import com.paystream.user.dto.UserCreateRequest;
import com.paystream.user.dto.UserProfileResponse;

import java.util.List;

public interface UserSearchService {
    UserDocument createUser(UserDocument user);
    UserDocument getUserProfile(String userId);
    void deleteUser(String userId);
    void  removeUser(String userId);
    List<UserDocument> getAllUsers();

    public List<UserDocument> searchUsers(String keyword, String status, int page, int size);
}
