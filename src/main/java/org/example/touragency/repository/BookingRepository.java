package org.example.touragency.repository;

import org.example.touragency.model.entity.Booking;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import static org.example.touragency.constant.QueryConstants.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class BookingRepository extends AbstractHibernateRepository {


    protected BookingRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Booking save(Booking booking) {
        return executeInTransaction(session ->  {
            session.persist(booking);
            return booking;
        });
    }

    public Optional<Booking> findBookingByUserAndTourId(UUID userId, UUID tourId) {
        return executeInTransaction(session ->
                session.createQuery("FROM Booking WHERE user.id = :userId AND tour.id = :tourId", Booking.class)
                        .setParameter(USER_ID, userId)
                        .setParameter(TOUR_ID, tourId)
                        .uniqueResultOptional()
        );
    }

    public List<Booking> findAllBookingsByUserId(UUID userId) {
        return executeInTransaction(session ->
                session.createQuery("FROM Booking WHERE user.id = :userId", Booking.class)
                        .setParameter(USER_ID, userId)
                        .list()
        );
    }

    public void deleteAllIfTourDeleted(UUID tourId) {
        executeInTransactionVoid(session ->
                session.createMutationQuery("DELETE FROM Booking WHERE tour.id = :tourId")
                        .setParameter(TOUR_ID, tourId)
                        .executeUpdate()
        );
    }

    public void deleteByUserIdAndTourId(UUID userId, UUID tourId) {
        executeInTransactionVoid(session ->
                session.createMutationQuery("DELETE FROM Booking WHERE user.id = :userId AND tour.id = :tourId")
                        .setParameter(USER_ID, userId)
                        .setParameter(TOUR_ID, tourId)
                        .executeUpdate()
        );
    }

    public void deleteAllIfUserDeleted(UUID userId){
        executeInTransaction(session ->
            session.createMutationQuery("DELETE FROM Booking WHERE user.id = :userId")
                    .setParameter(USER_ID, userId)
                    .executeUpdate()
        );
    }

}
