package com.qthegamep.diploma.project.back.service;

import com.google.gson.Gson;
import com.iit.certificateAuthority.endUser.libraries.signJava.EndUserSession;
import com.iit.certificateAuthority.endUser.libraries.signJava.EndUserSignInfo;
import com.qthegamep.diploma.project.back.crypto.CryptoOperation;
import com.qthegamep.diploma.project.back.dto.EncryptedRequestDTO;
import com.qthegamep.diploma.project.back.dto.EncryptedResponseDTO;
import com.qthegamep.diploma.project.back.dto.SignedDataDTO;
import com.qthegamep.diploma.project.back.utils.Consts;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ApiBackOperationServiceImpl implements ApiBackOperationService {
    private static final Logger LOG = LoggerFactory.getLogger(ApiBackOperationServiceImpl.class.getName());

    @Inject
    private SenderHttpService senderHttpService;
    @Inject
    private Gson gson;

    @Override
    public List<EndUserSession> getSessions(String requestId) throws Exception {
        Response response = senderHttpService.getApiCerts(requestId);
        SignedDataDTO signRequestDTO = gson.fromJson(response.readEntity(String.class), SignedDataDTO.class);
        JSONObject jsonObject = getCertsFromSign(signRequestDTO);
        String cloudCert = jsonObject.getJSONArray("certificates").getJSONObject(0).getString("cert");
        LOG.info("CloudApiBackCert: {}", cloudCert);
        String hsmCert = jsonObject.getJSONArray("certificates").getJSONObject(1).getString("cert");
        LOG.info("GryadaCert: {}", hsmCert);
        byte[] certCloudByte = Base64.getDecoder().decode(cloudCert);
        byte[] certHsmByte = Base64.getDecoder().decode(hsmCert);
        EndUserSession sessionCloud = CryptoOperation.genSessionEnc(certCloudByte);
        EndUserSession sessionHsm = CryptoOperation.genSessionEnc(certHsmByte);
        List<EndUserSession> sessions = new ArrayList<>();
        sessions.add(sessionCloud);
        sessions.add(sessionHsm);
        return sessions;
    }

    public String createUserPassJson(String pass) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("namedKeyPassword", pass);
        return object.toString();
    }

    public String generateKeys(EndUserSession sessionHsm, String requestId) throws Exception {
        String userPassJson = createUserPassJson(Consts.USER_PASS);
        String userPassJsonEnc = createEncReqWithEndUserSession(userPassJson, sessionHsm);
        String requestToGenKeysEnc = createReqToGenerateKey(userPassJsonEnc);
        String requestToGenKeysEncWithSign = createJsonWithTechSign(requestToGenKeysEnc.getBytes(StandardCharsets.UTF_8));
        Response resultGen = senderHttpService.generateNewCerts(requestId, requestToGenKeysEncWithSign);
        return resultGen.readEntity(String.class);

    }
    public String createJsonWithTechSign( byte[] data) throws JSONException {
        String signed = null;
//        String prominSession = Util.getProminSession();
        String prominSession = Consts.PROMIN;
        String req = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><req><sid>#sid#</sid><busid>CLOUD</busid><bank>1</bank><tickets><ticket>#data#</ticket></tickets></req>";
        String info = req.replaceFirst("#sid#", prominSession).replaceFirst("#data#", Base64.getEncoder().encodeToString(data));
        Client client = ClientBuilder.newClient();
        Response response = client.target(Consts.URL_ACSK_TECH_SIGN)
                .request(MediaType.APPLICATION_XML)
                .post(Entity.xml(info));
        String result = response.readEntity(String.class);
        LOG.info("SIGN_TECH_KEY: {}", result);
        if (result.contains("<signed>")) {
            signed = result.substring(result.indexOf("<signed>") + 8, result.indexOf("</signed>"));
        }
        JSONObject job = new JSONObject();
        job.put("signedData", signed);
        return job.toString();
    }

    public String getOperationId(EndUserSession sessionCloud, String requestId) throws Exception {
        String reqClientIdJson = createClientIdJson();
        String encReqClientId = CryptoOperation.sessionEnc(sessionCloud, reqClientIdJson);
        EncryptedRequestDTO encryptedRequestDTO = createEncryptedRequest(sessionCloud, encReqClientId);
        Response responseOperId = senderHttpService.getOperationId(requestId, gson.toJson(encryptedRequestDTO));
//        LOG.info("OperationId response: {}", responseOperId.readEntity(String.class));
        EncryptedResponseDTO responseDTO = gson.fromJson(responseOperId.readEntity(String.class), EncryptedResponseDTO.class);
        LOG.info("**Acquire-operation-id response: {}", responseDTO);
        String signData = new String(CryptoOperation.decrResp(responseDTO.getEncryptedData(), sessionCloud), StandardCharsets.UTF_8);
        String operationId = getDataFromSign(signData, "operationId");
        LOG.info("**Acquire-operation-id** {}", operationId);
        return operationId;
    }

    public String createReqToGenerateKey(String pass) {
        JSONObject obj = new JSONObject();
        obj.put("ekbId", Consts.EKB_ID);
        obj.put("nameKeyLabel", Consts.USER_ALIAS);
        obj.put("password", new JSONObject(pass));
        return obj.toString();
    }

    public EncryptedRequestDTO createEncryptedRequest(EndUserSession session, String dataToEnc) {
        EncryptedRequestDTO encryptedRequestDTO = new EncryptedRequestDTO();
        String authData = Base64.getEncoder().encodeToString(session.GetData());
        encryptedRequestDTO.setAuthData(authData);
        encryptedRequestDTO.setEncryptedData(dataToEnc);
        return encryptedRequestDTO;
    }
    private JSONObject getCertsFromSign(SignedDataDTO signedDataDTO) throws Exception {
        EndUserSignInfo endUserSignInfo = CryptoOperation.getDataFromSign(signedDataDTO.getSignedData());
        return new JSONObject(new String(endUserSignInfo.GetData(), StandardCharsets.UTF_8));
    }

    private String createClientIdJson() {
        Map<String, String> map = new HashMap<>();
        map.put("clientId", Consts.CLIENT_ID);
        return gson.toJson(map);
    }
    public String createEncReqWithEndUserSession(String data, EndUserSession clientSession) throws Exception {
        String encPass = CryptoOperation.sessionEnc(clientSession, data);
        String authData = Base64.getEncoder().encodeToString(clientSession.GetData());
        Map<String, String> map = new HashMap<>();
        map.put("authData", authData);
        map.put("encryptedData", encPass);
        return gson.toJson(map);
    }

    public String getDataFromSign(String signData, String value) throws Exception {
        JSONObject jsonObject = new JSONObject(signData);
        EndUserSignInfo endUserSignInfo = CryptoOperation.getDataFromSign(jsonObject.getString("signedData"));
        jsonObject = new JSONObject(new String(endUserSignInfo.GetData(), StandardCharsets.UTF_8));
        LOG.info("Data from signed: {} Value: {}", jsonObject, value);
        return jsonObject.getString(value);
    }
}
