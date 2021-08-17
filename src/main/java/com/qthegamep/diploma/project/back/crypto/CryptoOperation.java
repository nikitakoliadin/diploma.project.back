package com.qthegamep.diploma.project.back.crypto;

import com.iit.certificateAuthority.endUser.libraries.signJava.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class CryptoOperation {
    private static final Logger LOG = LoggerFactory.getLogger(CryptoOperation.class);
    private static final String CRYPTO_END_USER_CHARSET = System.getProperty("crypto.enduser.charset", StandardCharsets.UTF_8.displayName());
    private static final boolean CRYPTO_END_USER_UI_MODE = Boolean.parseBoolean(System.getProperty("crypto.enduser.ui.mode", "false"));
    private static final String CRYPTO_END_USER_LIBRARY_PATH = System.getProperty("crypto.enduser.library.path", "/opt/iit/diploma.project.back/EUSign-x64-1.3.148");
    private static final boolean CRYPTO_END_USER_OFFLINE_MODE = Boolean.parseBoolean(System.getProperty("crypto.enduser.offline.mode", "false"));
    private static final boolean CRYPTO_END_USER_USE_PROXY = Boolean.parseBoolean(System.getProperty("crypto.enduser.use.proxy", "false"));
    private static final boolean CRYPTO_END_USER_PROXY_ANONYMOUS = Boolean.parseBoolean(System.getProperty("crypto.enduser.proxy.anonymous", "true"));
    private static final String CRYPTO_END_USER_PROXY_ADDRESS = System.getProperty("crypto.enduser.proxy.address", "");
    private static final String CRYPTO_END_USER_PROXY_PORT = System.getProperty("crypto.enduser.proxy.port", "");
    private static final String CRYPTO_END_USER_PROXY_USER = System.getProperty("crypto.enduser.proxy.user", "");
    private static final String CRYPTO_END_USER_PROXY_PASSWORD = System.getProperty("crypto.enduser.proxy.password", "");
    private static final boolean CRYPTO_END_USER_PROXY_SAVE_PASSWORD = Boolean.parseBoolean(System.getProperty("crypto.enduser.proxy.save.password", "false"));
    private static final String CRYPTO_END_USER_FILE_STORE_PATH = System.getProperty("crypto.enduser.file.store.path", "/opt/certs/");
    private static final boolean CRYPTO_END_USER_SAVE_LOADED_CERTS = Boolean.parseBoolean(System.getProperty("crypto.enduser.save.loaded.certs", "true"));
    private static final boolean CRYPTO_END_USER_CMP_USE = Boolean.parseBoolean(System.getProperty("crypto.enduser.cmp.use", "true"));
    private static final String CRYPTO_END_USER_CMP_ADDRESS = System.getProperty("crypto.enduser.cmp.address", "http://acsk.privatbank.ua/services/cmp/");
    private static final String CRYPTO_END_USER_CMP_PORT = System.getProperty("crypto.enduser.cmp.port", "80");
    private static final String CRYPTO_END_USER_CMP_COMMON_NAME = System.getProperty("crypto.enduser.cmp.common.name", "");
    private static final boolean CRYPTO_END_USER_TSP_GET_STAMPS = Boolean.parseBoolean(System.getProperty("crypto.enduser.tsp.get.stamps", "true"));
    private static final String CRYPTO_END_USER_TSP_ADDRESS = System.getProperty("crypto.enduser.tsp.address", "http://acsk.privatbank.ua/services/tsp/");
    private static final String CRYPTO_END_USER_TSP_PORT = System.getProperty("crypto.enduser.tsp.port", "80");
    private static final boolean CRYPTO_END_USER_OCSP_USE = Boolean.parseBoolean(System.getProperty("crypto.enduser.ocsp.use", "true"));
    private static final String CRYPTO_END_USER_OCSP_ADDRESS = System.getProperty("crypto.enduser.ocsp.address", "http://acsk.privatbank.ua/services/ocsp/");
    private static final String CRYPTO_END_USER_OCSP_PORT = System.getProperty("crypto.enduser.ocsp.port", "80");
    private static EndUser endUser;

    public static EndUser getEndUser() {
        return endUser;
    }

    static {
        try {
            initEndUser();
            initEndUserMode();
            initEndUserProxy();
            initFileStore();
            initCMP();
            initTSP();
            initOCSP();
            initLDAP();
            String libraryPath = EndUserResourceExtractor.GetInstallPath();
            LOG.info("EndUser library path: {}", libraryPath);
            String storePath = endUser.GetFileStoreSettings().GetPath();
            LOG.info("EndUser store path: {}", storePath);
        } catch (Exception e) {
            LOG.error("[EUSign] Initialization ERROR:", e);
        }
    }

    private CryptoOperation() {
    }

    private static void initEndUser() throws Exception {
        endUser = new EndUser();
        endUser.SetUIMode(CRYPTO_END_USER_UI_MODE);
        endUser.SetLanguage(EndUser.EU_RU_LANG);
        endUser.SetCharset(CRYPTO_END_USER_CHARSET);
        LOG.info("EndUser path: {}", CRYPTO_END_USER_LIBRARY_PATH);
        Path path = Paths.get(CRYPTO_END_USER_LIBRARY_PATH);
        if (!Files.exists(Paths.get(path.toString()))){
            Files.createDirectories(path);
        }
        EndUserResourceExtractor.SetPath(CRYPTO_END_USER_LIBRARY_PATH);
        EndUserResourceExtractor.ExtractResources();
        endUser.Initialize();
        LOG.info("EndUser initialized: {}", endUser.IsInitialized());
    }

    private static void initEndUserMode() throws Exception {
        LOG.info("EndUser offline mode: {}", CRYPTO_END_USER_OFFLINE_MODE);
        EndUserModeSettings modeSettings = new EndUserModeSettings(CRYPTO_END_USER_OFFLINE_MODE);
        modeSettings.SetOfflineMode(CRYPTO_END_USER_OFFLINE_MODE);
        endUser.SetModeSettings(modeSettings);
    }

    private static void initEndUserProxy() throws Exception {
        EndUserProxySettings proxySettings = endUser.CreateProxySettings();
        proxySettings.SetUseProxy(CRYPTO_END_USER_USE_PROXY);
        proxySettings.SetAnonymous(CRYPTO_END_USER_PROXY_ANONYMOUS);
        proxySettings.SetAddress(CRYPTO_END_USER_PROXY_ADDRESS);
        proxySettings.SetPort(CRYPTO_END_USER_PROXY_PORT);
        proxySettings.SetUser(CRYPTO_END_USER_PROXY_USER);
        proxySettings.SetPassword(CRYPTO_END_USER_PROXY_PASSWORD);
        proxySettings.SetSavePassword(CRYPTO_END_USER_PROXY_SAVE_PASSWORD);
        endUser.SetProxySettings(proxySettings);
        LOG.info("EndUser proxy was initialized");
    }

    private static void initFileStore() throws Exception {
        EndUserFileStoreSettings fileStoreSettings = endUser.CreateFileStoreSettings();
        Path path = Paths.get(CRYPTO_END_USER_FILE_STORE_PATH);
        if (!Files.exists(Paths.get(path.toString()))){
            Files.createDirectories(path);
        }
        fileStoreSettings.SetPath(CRYPTO_END_USER_FILE_STORE_PATH);
        fileStoreSettings.SetSaveLoadedCerts(CRYPTO_END_USER_SAVE_LOADED_CERTS);
        endUser.SetFileStoreSettings(fileStoreSettings);
        LOG.info("EndUser file store was initialized");
    }

    private static void initCMP() throws Exception {
        EndUserCMPSettings cmpSettings = endUser.CreateCMPSettings();
        cmpSettings.SetUseCMP(CRYPTO_END_USER_CMP_USE);
        cmpSettings.SetAddress(CRYPTO_END_USER_CMP_ADDRESS);
        cmpSettings.SetPort(CRYPTO_END_USER_CMP_PORT);
        cmpSettings.SetCommonName(CRYPTO_END_USER_CMP_COMMON_NAME);
        endUser.SetCMPSettings(cmpSettings);
        LOG.info("EndUser CMP was initialized. CMP address: {}", CRYPTO_END_USER_CMP_ADDRESS);
    }

    private static void initTSP() throws Exception {
        EndUserTSPSettings tspSettings = endUser.CreateTSPSettings();
        tspSettings.SetGetStamps(CRYPTO_END_USER_TSP_GET_STAMPS);
        tspSettings.SetAddress(CRYPTO_END_USER_TSP_ADDRESS);
        tspSettings.SetPort(CRYPTO_END_USER_TSP_PORT);
        endUser.SetTSPSettings(tspSettings);
        LOG.info("EndUser TSP was initialized. TSP address: {}", CRYPTO_END_USER_TSP_ADDRESS);
    }

    private static void initOCSP() throws Exception {
        EndUserOCSPSettings ocspSettings = endUser.CreateOCSPSettings();
        ocspSettings.SetUseOCSP(CRYPTO_END_USER_OCSP_USE);
        ocspSettings.SetAddress(CRYPTO_END_USER_OCSP_ADDRESS);
        ocspSettings.SetPort(CRYPTO_END_USER_OCSP_PORT);
        endUser.SetOCSPSettings(ocspSettings);
        LOG.info("EndUser OCSP was initialized. OCSP address: {}", CRYPTO_END_USER_OCSP_ADDRESS);
    }

    private static void initLDAP() throws Exception {
        EndUserLDAPSettings lDAPSettings = endUser.CreateLDAPSettings();
        endUser.SetLDAPSettings(lDAPSettings);
    }
    public static EndUserSession genSessionEnc(byte[] cert) {
        EndUserSession clientSession = null;
        try {
            clientSession = endUser.ClientDynamicKeySessionCreate(900, cert);
        } catch (EndUserException e) {
            LOG.error("Error: {} : {}", e, e.GetErrorCode());
        } catch (Exception e) {
            LOG.error("Error Exception", e);
        }
        return clientSession;
    }

    public static byte[] decrResp(String encData, EndUserSession session) throws Exception {
        return endUser.SessionDecrypt(session, encData);
    }

    public static EndUserSignInfo getDataFromSign(String signData) throws Exception {
        return endUser.VerifyInternal(signData);
    }
    public static String sessionEnc(EndUserSession session, String data) throws Exception {
        return endUser.SessionEncrypt(session, data);
    }
    public static String getHashData(byte[] data) throws Exception {
        EndUserContext context = endUser.CtxCreate();
        EndUserHashContext hashContext = endUser.CtxHashBegin(context, EndUser.EU_CTX_HASH_ALGO_GOST34311, null);
        endUser.CtxHashContinue(hashContext, data);
        String hash = endUser.CtxHashEnd(hashContext);
        LOG.warn("[EUSign] Hash: {}", hash);
        return hash;
    }
    public static String appendSign(byte[] data, String sign) throws Exception {
        return endUser.AppendSign(data, sign);
    }

}

