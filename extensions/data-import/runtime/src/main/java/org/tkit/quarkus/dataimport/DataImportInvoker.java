package org.tkit.quarkus.dataimport;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.xml.bind.DatatypeConverter;

import org.jboss.logging.Logger;
import org.tkit.quarkus.dataimport.log.DataImportLog;

import io.quarkus.arc.Arc;
import io.quarkus.arc.InjectableBean;
import io.quarkus.arc.InstanceHandle;
import io.quarkus.arc.Unremovable;

@Unremovable
@ApplicationScoped
public class DataImportInvoker {

    private static final Logger LOGGER = Logger.getLogger(DataImportInvoker.class);

    @Inject
    EntityManager em;

    @Transactional(value = Transactional.TxType.REQUIRED, dontRollbackOn = { DontRollbackException.class })
    public void processItem(String key, String bean, DataImportRuntimeConfig.DataImportConfiguration config,
            DataImportRuntimeConfig rootConfig) throws Exception {
        // check if the data import fo the key is enabled
        if (!config.enabled) {
            LOGGER.info("Data import for key: " + key + " is disabled.");
            return;
        }

        // check if file is defined
        if (config.file == null || config.file.isBlank()) {
            LOGGER.warn("Data import file is not defined. Key: " + key);
            return;
        }

        // check if the file exists
        Path file = Paths.get(config.file);
        if (!Files.exists(file)) {
            LOGGER.info("Data import file does not exists. Key: " + key + " file: " + config.file);
            return;
        }

        // create parameter
        DataImportConfig param = new DataImportConfig();
        param.key = key;
        param.metadata = config.metadata;
        param.file = file;
        param.data = loadData(param.file);
        param.md5 = createChecksum(param.data);

        // find import entry and lock it
        DataImportLog log = em.find(DataImportLog.class, param.key, PESSIMISTIC_WRITE);
        if (log == null) {
            log = new DataImportLog();
            log.setId(param.key);
            log.setCreationDate(LocalDateTime.now());
            try {
                em.persist(log);
                em.flush();
            } catch (Exception ex) {
                // ignore
            }
            log = em.find(DataImportLog.class, param.key, PESSIMISTIC_WRITE);
        }

        // check the MD5
        if (log.getMd5() != null && Objects.equals(param.md5, log.getMd5())) {
            if ((log.getError() == null || log.getError().isBlank()) || !config.retryErrorImport) {
                LOGGER.info("No changes found in the data import file. Key: " + key + " file: " + config.file);
                return;
            }
        }

        // call data import service
        log.setModificationDate(LocalDateTime.now());
        log.setMd5(param.md5);
        log.setFile(param.file.toString());
        log.setError(null);
        InjectableBean<DataImportService> bi = Arc.container().bean(bean);
        try (InstanceHandle<DataImportService> instance = Arc.container().instance(bi)) {
            instance.get().importData(param);
        } catch (Exception ex) {
            String msg = ex.getMessage();
            if (msg != null) {
                msg = msg.substring(0, Math.min(msg.length(), rootConfig.errorMsgLength));
            }
            log.setError(msg);
            throw new DontRollbackException(msg, ex);
        } finally {
            em.merge(log);
            em.flush();
        }
    }

    private static String createChecksum(byte[] data) throws RuntimeException {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] hash = md5.digest(data);
            return DatatypeConverter.printHexBinary(hash).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error create MD5 from the file content ", e);
        }
    }

    private static byte[] loadData(Path path) throws RuntimeException {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException("Error loading data from " + path, e);
        }
    }

    public static class DontRollbackException extends Exception {

        DontRollbackException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }
}
