package com.thecoffeetaster.repository;

import com.thecoffeetaster.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para la entidad User.
 * 
 * Extiende JpaRepository para proporcionar operaciones CRUD automáticas.
 * Spring Data JPA genera la implementación en tiempo de ejecución.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param email el correo electrónico del usuario a buscar
     * @return un Optional que contiene el usuario si existe, o vacío si no
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Busca un usuario por su UUID.
     *
     * @param uuid el UUID único del usuario a buscar
     * @return un Optional que contiene el usuario si existe, o vacío si no
     */
    Optional<User> findByUuid(UUID uuid);
}
