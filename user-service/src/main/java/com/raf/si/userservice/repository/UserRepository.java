package com.raf.si.userservice.repository;

import com.raf.si.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByLbz(UUID lbz);

    @Query(value = "select case when (count(u) > 0)  then true else false end" +
            " from users u where u.lbz = :lbz and u.isDeleted = :isDeleted")
    boolean userExists(@PathVariable("lbz") UUID lbz, @PathVariable("isDeleted") boolean isDeleted);
}
