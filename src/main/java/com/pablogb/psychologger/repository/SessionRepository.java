package com.pablogb.psychologger.repository;

import com.pablogb.psychologger.domain.entity.SessionEntity;
import org.springframework.data.repository.CrudRepository;

public interface SessionRepository extends CrudRepository<SessionEntity, Long> {

}
