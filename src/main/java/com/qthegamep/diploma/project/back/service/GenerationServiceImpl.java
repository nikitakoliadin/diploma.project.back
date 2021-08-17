package com.qthegamep.diploma.project.back.service;

import com.iit.certificateAuthority.endUser.libraries.signJava.EndUserSession;
import com.qthegamep.diploma.project.back.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import java.util.List;


public class GenerationServiceImpl implements GenerationService {

    private static final Logger LOG = LoggerFactory.getLogger(GenerationServiceImpl.class.getName());
    @Inject
    private ApiBackOperationService apiBackOperationService;
    @Override
    public GenerationResponseDTO generate(GenerationRequestDTO generationRequestDTO, String requestId) {
        try {
            List<EndUserSession> sessions = apiBackOperationService.getSessions(requestId);
            EndUserSession sessionHsm = sessions.get(1);
            EndUserSession sessionCloud = sessions.get(0);
//            String operationId = apiBackOperationService.getOperationId(sessionCloud, requestId);
            String response = apiBackOperationService.generateKeys(sessionHsm, requestId);
            LOG.info("Result generation: {} RequestId: {}", response, requestId);

        } catch (Exception e) {
            LOG.warn("GENERATION ERROR", e);

        }


        return null;
    }
}
