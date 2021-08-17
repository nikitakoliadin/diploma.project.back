package com.qthegamep.diploma.project.back.service;

import com.google.gson.Gson;
import com.iit.certificateAuthority.endUser.libraries.signJava.EndUserSession;
import com.iit.certificateAuthority.endUser.libraries.signJava.EndUserSignInfo;
import com.qthegamep.diploma.project.back.crypto.CryptoOperation;
import com.qthegamep.diploma.project.back.dto.*;
import com.qthegamep.diploma.project.back.utils.Consts;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.privatbank.cryptonite.CryptoniteX;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SignServiceImpl implements SignService {
    private static final Logger LOG = LoggerFactory.getLogger(SignServiceImpl.class.getName());
    private SenderHttpService senderHttpService;
    private ApiBackOperationService apiBackOperationService;
    private Gson gson;

    @Inject
    public SignServiceImpl(SenderHttpService senderHttpService, ApiBackOperationService apiBackOperationService, Gson gson) {
        this.senderHttpService = senderHttpService;
        this.apiBackOperationService = apiBackOperationService;
        this.gson = gson;
    }

    @Override
    public SignResponseDTO sign(SignRequestDTO requestDTO, String requestId) {
        SignResponseDTO signResponseDTO = new SignResponseDTO();
        try {
            byte[] reqData = requestDTO.getData().getBytes(StandardCharsets.UTF_8);
            String hashToSign = CryptoOperation.getHashData(reqData);
            List<EndUserSession> sessions = apiBackOperationService.getSessions(requestId);
            EndUserSession sessionCloud = sessions.get(0);
            EndUserSession sessionHsm = sessions.get(1);

            String operationId = apiBackOperationService.getOperationId(sessionCloud, requestId);

            String signRequest = createSignRequest(operationId, sessionCloud, hashToSign);
            Response responseSign = senderHttpService.getSignResponse(requestId, signRequest);
            EncryptedResponseDTO encryptedResponseDTO = gson.fromJson(responseSign.readEntity(String.class), EncryptedResponseDTO.class);
            String signData = new String(CryptoOperation.decrResp(encryptedResponseDTO.getEncryptedData(), sessionCloud), StandardCharsets.UTF_8);
            String status = getDataFromSignLong(signData, "status");
            LOG.info("**SIGN** {}", status);

            String userPassJson = createUserPassJson(Consts.USER_PASS);
            String userPassJsonEnc = apiBackOperationService.createEncReqWithEndUserSession(userPassJson, sessionHsm);
            String requestDataToApprove = createGsonToApproveSign(userPassJsonEnc, Consts.CLIENT_ID, operationId);
            String requestDataToApproveWithTechSign = apiBackOperationService.createJsonWithTechSign(requestDataToApprove.getBytes(StandardCharsets.UTF_8));
            Response responseApprove = senderHttpService.getApproveResponse(requestId, requestDataToApproveWithTechSign);
            LOG.info("**APPROVE** {}", responseApprove.readEntity(String.class));

            String reqStatusSign = createJsonSignStatus(Consts.CLIENT_ID, operationId);
            String encryptRequestStatusSign = apiBackOperationService.createEncReqWithEndUserSession(reqStatusSign, sessionCloud);
            Response responseStatusSign = senderHttpService.getStatusSignResponse(requestId, encryptRequestStatusSign);
            EncryptedResponseDTO encryptedResponseStatusSign = gson.fromJson(responseStatusSign.readEntity(String.class), EncryptedResponseDTO.class);
            signData = new String(CryptoOperation.decrResp(encryptedResponseStatusSign.getEncryptedData(), sessionCloud), StandardCharsets.UTF_8);
            String signResponse = apiBackOperationService.getDataFromSign(signData, "signature");
            LOG.info("**SIGN_STATUS** {}", signResponse);
            byte[] signatureByte = Base64.getDecoder().decode(signResponse);
            byte[] cmsWithSign = CryptoniteX.cmsSetData(signatureByte, reqData);
            String resultSign = Base64.getEncoder().encodeToString(cmsWithSign);
            LOG.info("**CMS** {}", resultSign);
            Path pathToSave = Paths.get("src/main/resources/sign.sig");
            Files.write(pathToSave, cmsWithSign);
//            try (OutputStream stream = new FileOutputStream("/result.sign")) {
//                stream.write(cmsWithSign);
//            }
            signResponseDTO.setSign(resultSign);


        } catch (Exception e) {
            LOG.warn("[SignService ERROR]", e);
        }
        return signResponseDTO;
    }

    public String createJsonSignStatus(String clientID, String operId) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("clientId", clientID);
        object.put("operationId", operId);
        return object.toString();
    }

    private String createUserPassJson(String pass) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("namedKeyPassword", pass);
        return object.toString();
    }

    public String createGsonToApproveSign(String encData, String clientId, String operationId) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("nameKeyLabel", Consts.USER_ALIAS);
        obj.put("password", new JSONObject(encData));
        obj.put("clientId", clientId);
        obj.put("operationId", operationId);
        return obj.toString();
    }
    private String getDataFromSignLong(String signData, String value) throws Exception {
        JSONObject object = new JSONObject(signData);
        EndUserSignInfo endUserSignInfo = CryptoOperation.getDataFromSign(object.getString("signedData"));
        object = new JSONObject(new String(endUserSignInfo.GetData(), StandardCharsets.UTF_8));
        LOG.info("Data from sign: {} Value: {}", object, value);
        return String.valueOf(object.getLong(value));
    }

    private String createSignRequest(String operationId, EndUserSession session, String hash) throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("clientId", Consts.CLIENT_ID);
        obj.put("operationId", operationId);
        obj.put("time", new Date().toString());
        obj.put("originatorDescription", "P24");
        obj.put("operationDescription", "QAZWSX123");
        obj.put("hash", hash);
        obj.put("signatureAlgorithmName", "DSTU4145");
        obj.put("signatureFormat", "PKCS7");
        String encAllReq = apiBackOperationService.createEncReqWithEndUserSession(obj.toString(), session);
        LOG.info("Sign request: {} Encrypted req: {}", obj.toString(), encAllReq);
        return encAllReq;
    }
}
