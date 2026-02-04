package org.example.touragency.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.function.Consumer;
import java.util.function.Function;

@Repository
public abstract class AbstractHibernateRepository {

    private final SessionFactory sessionFactory;

    protected AbstractHibernateRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected <T> T executeInTransaction(Function<Session, T> action) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            T result = action.apply(session);
            tx.commit();
            return result;
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    protected void executeInTransactionVoid(Consumer<Session> action) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            action.accept(session);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}
