package com.adi.smartcalendar.web.service.service;

public interface ProjectAuthService {

    boolean canViewProject(Long projectId, String permissionName);

}
