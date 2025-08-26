package com.vivekk.authservice.repository;

import com.vivekk.authservice.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends CrudRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByToken(String token);
}
