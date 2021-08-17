package com.qthegamep.diploma.project.back.service;

import com.qthegamep.diploma.project.back.utils.Consts;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class SenderHttpServiceImpl implements SenderHttpService {

    private Client client = ClientBuilder.newClient();

    public Response getApproveResponse(String requestId, String data) {
        return client.target(Consts.URL_KUBER_APPROVE)
                .request(MediaType.APPLICATION_JSON)
                .header("requestId", requestId)
                .post(Entity.json(data));
    }

    public Response getStatusSignResponse(String requestId, String data) {
        return client.target(Consts.URL_KUBER_SIGN_STATUS)
                .request(MediaType.APPLICATION_JSON)
                .header("requestId", requestId)
                .post(Entity.json(data));
    }

    public Response getApiCerts(String requestId) {
        return client.target(Consts.URL_KUBER_GET_CERTS_EXT)
                .request()
                .header("requestId", requestId)
                .get();
    }

    public Response getSignResponse(String requestId, String data) {
        return client.target(Consts.URL_KUBER_SIGN)
                .request(MediaType.APPLICATION_JSON)
                .header("requestId", requestId)
                .post(Entity.json(data));
    }

    public Response getOperationId(String requestId, String data) {
        return client.target(Consts.URL_KUBER_GET_OPERATION_ID)
                .request(MediaType.APPLICATION_JSON)
                .header("requestId", requestId)
                .post(Entity.json(data));
    }

    public Response generateNewCerts(String requestId, String data) {
        return client.target(Consts.URL_KUBER_KEY_GENERATE)
                .request(MediaType.APPLICATION_JSON)
                .header("requestId", requestId)
                .post(Entity.json(data));
    }

}
