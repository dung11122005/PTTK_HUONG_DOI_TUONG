package com.example.exam_portal.service;

import org.springframework.stereotype.Service;

import com.example.exam_portal.domain.ActivityLog;
import com.example.exam_portal.repository.ActivityLogRepository;


@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository){
        this.activityLogRepository=activityLogRepository;
    }

    public void handleSaveActivityLog(String username, String action, String method, String path, String ip, int statusCode) {
        ActivityLog log = new ActivityLog();
        log.setUsername(username);
        log.setAction(action);
        log.setMethod(method);
        log.setPath(path);
        log.setIpAddress(ip);
        log.setStatusCode(statusCode);
        this.activityLogRepository.save(log);
    }
}
