package com.qthegamep.diploma.project.back.service;

import javax.ws.rs.core.Response;

public interface SenderHttpService {
    Response getApproveResponse(String requestId, String data);

    Response getStatusSignResponse(String requestId, String data);

    Response getApiCerts(String requestId);

    Response getSignResponse(String requestId, String data);

    Response getOperationId(String requestId, String data);

    Response generateNewCerts(String requestId, String data);
}
