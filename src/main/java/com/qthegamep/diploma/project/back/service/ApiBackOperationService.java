package com.qthegamep.diploma.project.back.service;

import com.iit.certificateAuthority.endUser.libraries.signJava.EndUserSession;
import com.qthegamep.diploma.project.back.dto.EncryptedRequestDTO;
import org.json.JSONException;

import java.util.List;

public interface ApiBackOperationService {
    List<EndUserSession> getSessions(String requestId) throws Exception;

    String getOperationId(EndUserSession sessionCloud, String requestId) throws Exception;

    EncryptedRequestDTO createEncryptedRequest(EndUserSession session, String dataToEnc);

    String createEncReqWithEndUserSession(String data, EndUserSession clientSession) throws Exception;

    String getDataFromSign(String signData, String value) throws Exception;

    String createJsonWithTechSign( byte[] data) throws JSONException;

    String generateKeys(EndUserSession sessionHsm, String requestId) throws Exception;
}
