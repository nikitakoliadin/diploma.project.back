package com.qthegamep.diploma.project.back.controllers;

import com.qthegamep.diploma.project.back.dto.GenerationRequestDTO;
import com.qthegamep.diploma.project.back.dto.GenerationResponseDTO;
import com.qthegamep.diploma.project.back.service.GenerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/generate")
public class CertGenerateController {
    private static final Logger LOG = LoggerFactory.getLogger(SignController.class.getName());
    private GenerationService generationService;

    @Inject
    CertGenerateController(GenerationService generationService) {
        this.generationService = generationService;
    }

    @Context
    private HttpHeaders httpHeaders;

    @POST
    @Path("/cert")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response generateCert(GenerationRequestDTO requestDTO) {
        String requestId = httpHeaders.getHeaderString("requestId");
        LOG.info("Start generate cert. RequestId: {}", requestId);
        GenerationResponseDTO responseDTO = generationService.generate(requestDTO, requestId);
        return Response.status(javax.ws.rs.core.Response.Status.OK)
                .entity(responseDTO)
                .build();
    }
}
