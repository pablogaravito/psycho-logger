package com.pablogb.psychologger.repository;

import com.pablogb.psychologger.domain.entity.Session;
import org.springframework.data.repository.CrudRepository;

public interface SessionRepository extends CrudRepository<Session, Long> {
//    @Query("SELECT s FROM session s ORDER BY id DESC LIMIT 10")
//    Set<Session> getLast10Sessions();
}
