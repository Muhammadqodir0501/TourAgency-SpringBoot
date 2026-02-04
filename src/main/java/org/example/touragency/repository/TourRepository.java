package org.example.touragency.repository;

import org.example.touragency.model.entity.Tour;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Function;

@Repository
public class TourRepository extends AbstractHibernateRepository {

    public TourRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Tour save(Tour tour) {
        return executeInTransaction(session -> {
            session.persist(tour);
            return tour;
        });
    }

    public Tour update(Tour tour) {
        return executeInTransaction(session ->
                session.merge(tour)
        );
    }

    public void deleteById(UUID id) {
        executeInTransactionVoid(session -> {
            Tour tour = session.get(Tour.class, id);
            if (tour != null) {
                session.remove(tour);
            }
        });
    }

    public Optional<Tour> findById(UUID id) {
        return executeInTransaction((Function<Session, Optional<Tour>>) session ->
                Optional.ofNullable(session.get(Tour.class, id))
        );
    }

    public List<Tour> findByAgencyId(UUID agencyId) {
        return executeInTransaction(session ->
                session.createQuery("FROM Tour WHERE agency.id = :agencyId ",Tour.class)
                        .setParameter("agencyId",agencyId)
                        .list()
        );
    }

    public List<Tour> findAll() {
        return executeInTransaction(session ->
                session.createQuery("FROM Tour", Tour.class).list()
        );
    }

    public void deleteAllByAgencyId(UUID agencyId) {
        executeInTransactionVoid(session ->
            session.createMutationQuery("DELETE FROM Tour WHERE agency.id = :agencyId ")
                    .setParameter("agencyId", agencyId)
                    .executeUpdate()
        );
    }






}
