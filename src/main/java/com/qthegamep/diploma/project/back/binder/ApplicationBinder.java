package com.qthegamep.diploma.project.back.binder;

import com.google.gson.Gson;
import com.qthegamep.diploma.project.back.service.*;
import org.glassfish.jersey.internal.inject.AbstractBinder;

import javax.inject.Singleton;

public class ApplicationBinder extends AbstractBinder {

    private SignService signService;
    private SenderHttpService senderHttpService;
    private Gson gson;
    private ApiBackOperationService apiBackOperationService;
    private GenerationService generationService;

    private ApplicationBinder(SignService signService, SenderHttpService senderHttpService, Gson gson, ApiBackOperationService apiBackOperationService, GenerationService generationService) {
        this.signService = signService;
        this.senderHttpService = senderHttpService;
        this.gson = gson;
        this.apiBackOperationService =apiBackOperationService;
        this.generationService = generationService;
    }

    public GenerationService getGenerationService() {
        return generationService;
    }
    public SignService getSignService() {
        return signService;
    }

    public SenderHttpService getSenderHttpService() {
        return senderHttpService;
    }

    public Gson getGsonObj() {
        return gson;
    }

    public ApiBackOperationService getApiBackOperationService(){
        return apiBackOperationService;
    }

    public static ApplicationBinderBuilder builder() {
        return new ApplicationBinderBuilder();
    }

    @Override
    protected void configure() {
        bindGenerationService();
        bindSenderService();
        bindGson();
        bindApiBachService();
        bindSignService();
    }
    private void bindGenerationService() {
        if (generationService == null) {
            bind(GenerationServiceImpl.class).to(GenerationService.class).in(Singleton.class);
        } else {
            bind(generationService).to(GenerationService.class).in(Singleton.class);
        }
    }
    private void bindGson() {
        if (gson == null) {
            gson = new Gson();
            bind(gson).to(Gson.class).in(Singleton.class);
        }
    }

    private void bindSignService() {
        if (signService == null) {
            bind(SignServiceImpl.class).to(SignService.class).in(Singleton.class);
        } else {
            bind(signService).to(SignService.class).in(Singleton.class);
        }
    }
    private void bindApiBachService() {
        if (apiBackOperationService == null) {
            bind(ApiBackOperationServiceImpl.class).to(ApiBackOperationService.class).in(Singleton.class);
        } else {
            bind(signService).to(ApiBackOperationService.class).in(Singleton.class);
        }
    }

    private void bindSenderService() {
        if (senderHttpService == null) {
            bind(SenderHttpServiceImpl.class).to(SenderHttpService.class).in(Singleton.class);
        } else {
            bind(senderHttpService).to(SenderHttpService.class).in(Singleton.class);
        }
    }

    public static class ApplicationBinderBuilder {
        private GenerationService generationService;
        private SignService signService;
        private SenderHttpService senderHttpService;
        private Gson gson;
        private ApiBackOperationService apiBackOperationService;

        public ApplicationBinderBuilder setSignService(SignService signService) {
            this.signService = signService;
            return this;
        }
        public ApplicationBinderBuilder setGenService(GenerationService generationService) {
            this.generationService = generationService;
            return this;
        }

        public ApplicationBinderBuilder setSenderHttpService(SenderHttpService senderHttpService) {
            this.senderHttpService = senderHttpService;
            return this;
        }
        public ApplicationBinderBuilder setGson(Gson gson) {
            this.gson = gson;
            return this;
        }
        public ApplicationBinderBuilder setApiBackService(ApiBackOperationService apiBackOperationService) {
            this.apiBackOperationService = apiBackOperationService;
            return this;
        }

        public ApplicationBinder build() {
            return new ApplicationBinder(signService, senderHttpService, gson, apiBackOperationService, generationService);
        }
    }
}
