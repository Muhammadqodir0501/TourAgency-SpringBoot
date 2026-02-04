package org.example.touragency.repository;

import org.example.touragency.model.entity.Rating;
import org.example.touragency.model.entity.RatingCounter;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import static org.example.touragency.constant.QueryConstants.*;

import java.util.Optional;
import java.util.UUID;

@Repository
public class RatingRepository extends AbstractHibernateRepository{

    protected RatingRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public void saveRating(Rating rating) {
        executeInTransaction(session -> {
            session.persist(rating);
            return rating;
        });
    }

    public void saveCounter(RatingCounter ratingCounter) {
        executeInTransaction(session -> {
            session.persist(ratingCounter);
            return ratingCounter;
        });
    }


    public Rating updateRating(Rating rating) {
        return executeInTransaction(session ->
            session.merge(rating)
        );
    }

    public void updateCounter(RatingCounter counter) {
        executeInTransaction(session ->
            session.merge(counter)
        );
    }


    public Optional<Rating> findRatingByUserAndTourIds(UUID userId, UUID tourId){
        return executeInTransaction(session ->
                session.createQuery("FROM Rating WHERE userId = :userId AND tourId = :tourId", Rating.class)
                        .setParameter(USER_ID, userId)
                        .setParameter(TOUR_ID, tourId)
                        .uniqueResultOptional()
        );
    }


    public Optional<RatingCounter> findRatingCounterByTourId(UUID tourId){
        return executeInTransaction( session ->
                session.createQuery("FROM RatingCounter WHERE tourId =: tourId", RatingCounter.class)
                        .setParameter(TOUR_ID, tourId)
                        .uniqueResultOptional()
        );
    }

    public void deleteAllCountersIfTourDeleted(UUID tourId) {
        executeInTransactionVoid(session ->
            session.createMutationQuery("DELETE FROM RatingCounter WHERE tourId = :tourId")
                    .setParameter(TOUR_ID, tourId)
                    .executeUpdate()
        );
    }

    public void deleteAllRatingsIfTourDeleted(UUID tourId) {
        executeInTransactionVoid(session ->
            session.createMutationQuery("DELETE FROM Rating WHERE tourId = :tourId")
                    .setParameter(TOUR_ID, tourId)
                    .executeUpdate()
        );
    }

    public void deleteAllIfUserDeleted(UUID userId){
        executeInTransactionVoid(session ->
            session.createMutationQuery("DELETE FROM Rating WHERE userId = :userId")
                    .setParameter(USER_ID, userId)
                    .executeUpdate()
        );
    }


}