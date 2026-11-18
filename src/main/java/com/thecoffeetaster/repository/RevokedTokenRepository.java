package com.thecoffeetaster.repository;

import com.thecoffeetaster.entity.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio para la entidad {@link RevokedToken}.
 * 
 * Proporciona acceso a datos para la gestión de tokens revocados,
 * incluyendo validación de revocaciones y operaciones de revocación en masa.
 */
@Repository
public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Long> {
    
    /**
     * Verifica si un token con el hash especificado ha sido revocado.
     * 
     * Este método se utiliza para validar si un token JWT debe ser rechazado
     * durante la autenticación. La búsqueda se realiza comparando el hash del token
     * en lugar del token completo por razones de seguridad.
     * 
     * @param tokenHash el hash del token a verificar (no nulo)
     * @return {@code true} si existe un token revocado con el hash especificado,
     *         {@code false} en caso contrario
     */
    boolean existsByTokenHash(String tokenHash);
    
    /**
     * Obtiene todos los tokens revocados de un usuario específico.
     * 
     * Este método se utiliza para implementar la revocación en masa de tokens
     * de un usuario (por ejemplo, al cambiar contraseña o al cerrar sesión en todos los dispositivos).
     * Retorna todos los registros de tokens revocados asociados al usuario,
     * permitiendo auditoría y seguimiento de revocaciones.
     * 
     * @param userId el identificador único del usuario (no nulo)
     * @return una lista de tokens revocados del usuario especificado.
     *         Retorna una lista vacía si el usuario no tiene tokens revocados.
     */
    List<RevokedToken> findAllByUserId(UUID userId);
}
