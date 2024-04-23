package com.pablogb.psychologger.service;

import com.pablogb.psychologger.entity.Session;

public interface SessionService {
    /**
     * Patient getPatient(Long id);
     *     Patient saveStudent(Patient student);
     *     void deletePatient(Long id);
     *     List<Patient> getPatients();
     *     Set<Session> getPatientSessions(Long id);
     */
    Session getSession(Long id);
    Session saveSession(Session session);
    void deleteSession(Long id);

}
