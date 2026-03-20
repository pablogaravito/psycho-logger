package com.pablogb.psychologger.service;

public interface SnapshotService {
    void takeSnapshotForMonth(Integer userId, Integer year, Integer month);
    void takeCurrentMonthSnapshot();
    void catchUpMissingSnapshots();
}
