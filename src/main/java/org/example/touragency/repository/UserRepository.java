package org.example.touragency.repository;

import org.example.touragency.model.entity.User;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public class UserRepository extends AbstractHibernateRepository {

    public UserRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public User save(User user) {
        return executeInTransaction(session -> {
            session.persist(user);
            return user;
        });
    }

    public User update(User user) {
        return executeInTransaction(session ->
            session.merge(user)
        );
    }

    public Optional<User> findById(UUID id) {
        return executeInTransaction( session ->
                Optional.ofNullable(session.get(User.class, id))
        );
    }

    public Optional<User> findByEmail(String email) {
        return executeInTransaction(session ->
                session.createQuery("FROM User WHERE email = :email", User.class)
                        .setParameter("email", email)
                        .uniqueResultOptional()
        );
    }

    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return executeInTransaction(session ->
                session.createQuery("FROM User WHERE phoneNumber = :phone", User.class)
                        .setParameter("phone", phoneNumber)
                        .uniqueResultOptional()
        );
    }

    public List<User> findAll() {
        return executeInTransaction(session ->
                session.createQuery("FROM User", User.class).list()
        );
    }

    public void deleteById(UUID id) {
        executeInTransactionVoid(session -> {
            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user);
            }
        });
    }
}