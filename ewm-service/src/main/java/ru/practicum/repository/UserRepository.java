package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.user.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

   // @Query("select u from User u where u.id in :ids")
  //  @Query("select u from User u where u.id in ?1")
    List<User> findByIdIn(List<Long> ids, Pageable pageable);
}