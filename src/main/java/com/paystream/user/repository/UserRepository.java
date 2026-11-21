package com.paystream.user.repository;

import com.paystream.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    List<User> findByMerchantId(String merchantId);


    @Query("SELECT u FROM User u WHERE " +
            "(:fullName IS NOT NULL AND :fullName != '' AND LOWER(u.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) OR " +
            "(:email IS NOT NULL AND :email != '' AND LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))")
    List<User> searchUser(@Param("fullName") String fullName, @Param("email") String email);


    @Query("SELECT u FROM User u WHERE " +
            "(" +
            "  (:fullName IS NOT NULL AND :fullName != '' AND LOWER(u.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) OR " +
            "  (:email IS NOT NULL AND :email != '' AND LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))" +
            ") " +
            "AND u.merchantId = :merchantId")
    List<User> searchUserMerchant(
            @Param("fullName") String fullName,
            @Param("email") String email,
            @Param("merchantId") String merchantId
    );

}
