package com.example.exam_portal.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.exam_portal.domain.ActivityLog;
import com.example.exam_portal.domain.dto.ResultPaginationDTO;
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

    public ResultPaginationDTO fetchAllLogs(Specification<ActivityLog> spec, Pageable pageable) {
        Page<ActivityLog> page = this.activityLogRepository.findAll(spec, pageable);

        ResultPaginationDTO dto = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        dto.setMeta(meta);

        List<ActivityLog> result = page.getContent()
            .stream()
            .collect(Collectors.toList());

        dto.setResult(result);
        return dto;
    }

}
