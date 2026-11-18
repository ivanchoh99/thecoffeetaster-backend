package com.thecoffeetaster.repository;

import com.thecoffeetaster.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para gestionar operaciones de base de datos relacionadas con tokens de restablecimiento de contraseña.
 * 
 * Proporciona métodos para:
 * - Buscar tokens no utilizados para validación durante el proceso de restablecimiento
 * - Eliminar todos los tokens asociados a un usuario para limpiar tokens antiguos cuando se solicita uno nuevo
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Busca un token de restablecimiento de contraseña no utilizado por su hash.
     * 
     * Este método valida que el token exista y aún no haya sido utilizado (usedAt es null).
     * Se utiliza durante el proceso de validación de tokens en el restablecimiento de contraseña.
     * 
     * @param tokenHash el hash del token a buscar
     * @return un Optional con el token si existe y no ha sido utilizado, vacío en caso contrario
     */
    Optional<PasswordResetToken> findByTokenHashAndUsedAtIsNull(String tokenHash);

    /**
     * Elimina todos los tokens de restablecimiento de contraseña asociados a un usuario específico.
     * 
     * Este método se utiliza para limpiar tokens anteriores cuando el usuario solicita un nuevo
     * token de restablecimiento, asegurando que solo el token más reciente sea válido.
     * 
     * @param userId el UUID del usuario cuyos tokens serán eliminados
     */
    void deleteAllByUserId(UUID userId);
}
