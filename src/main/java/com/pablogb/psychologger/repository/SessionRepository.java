package com.pablogb.psychologger.repository;

import com.pablogb.psychologger.entity.Session;
import org.springframework.data.repository.CrudRepository;

public interface SessionRepository extends CrudRepository<Session, Long> {
}
