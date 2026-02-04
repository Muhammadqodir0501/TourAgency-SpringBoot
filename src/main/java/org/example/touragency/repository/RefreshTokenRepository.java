package org.example.touragency.repository;

import org.example.touragency.model.entity.RefreshToken;
import org.example.touragency.model.entity.User;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public class RefreshTokenRepository extends AbstractHibernateRepository{

    public RefreshTokenRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public RefreshToken save(RefreshToken refreshToken) {
        return executeInTransaction(session -> {
            session.persist(refreshToken);
            return refreshToken;
        });
    }
    public Optional<RefreshToken> findByToken(String token) {
        return executeInTransaction(session ->
                session.createQuery("FROM RefreshToken rt WHERE rt.token = :token", RefreshToken.class)
                        .setParameter("token", token)
                        .uniqueResultOptional()
        );
    }

    public void deleteByUser(User user) {
        executeInTransactionVoid(session ->
                session.createMutationQuery("DELETE FROM RefreshToken rt WHERE rt.user = :user")
                        .setParameter("user", user)
                        .executeUpdate()
        );
    }

    public void deleteExpired(Instant now) {
        executeInTransactionVoid(session ->
                session.createMutationQuery("DELETE FROM RefreshToken rt WHERE rt.expiryDate < :now")
                        .setParameter("now", now)
                        .executeUpdate()
        );
    }
}
