package org.example.touragency.repository;


import org.example.touragency.model.entity.FavouriteTour;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import static org.example.touragency.constant.QueryConstants.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FavTourRepository extends AbstractHibernateRepository{

    protected FavTourRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public FavouriteTour save(FavouriteTour favouriteTour) {
        return executeInTransaction(session ->  {
            session.persist(favouriteTour);
            return favouriteTour;
        });
    }

    public List<FavouriteTour> findAllByUserId(UUID userId){
        return executeInTransaction(session->
                session.createQuery("FROM FavouriteTour WHERE user.id = :userId", FavouriteTour.class)
                .setParameter(USER_ID, userId)
                        .list()
                );
    }

    public Optional<FavouriteTour> findByUserAndTourId(UUID userId, UUID tourId){
        return executeInTransaction( session ->
                session.createQuery("FROM FavouriteTour WHERE user.id = :userId and tour.id = :tourId", FavouriteTour.class)
                        .setParameter(USER_ID,userId)
                        .setParameter(TOUR_ID, tourId)
                        .uniqueResultOptional());

    }

    public void deleteAllIfTourDeleted(UUID tourId) {
        executeInTransaction(session ->
            session.createMutationQuery("DELETE FROM FavouriteTour WHERE tour.id = :tourId")
                    .setParameter(TOUR_ID, tourId)
                    .executeUpdate()
        );
    }

    public void deleteByUserIdAndTourId(UUID userId, UUID tourId) {
        executeInTransaction(session ->
            session.createMutationQuery("DELETE FROM FavouriteTour WHERE user.id = :userId AND tour.id = :tourId")
                    .setParameter(USER_ID, userId)
                    .setParameter(TOUR_ID, tourId)
                    .executeUpdate()
        );
    }

    public void deleteAllIfUserDeleted(UUID userId){
        executeInTransaction(session ->
            session.createMutationQuery("DELETE FROM FavouriteTour WHERE user.id = :userId")
                    .setParameter(USER_ID, userId)
                    .executeUpdate()
        );
    }

}
