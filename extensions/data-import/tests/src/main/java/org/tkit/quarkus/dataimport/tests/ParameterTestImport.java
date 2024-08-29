package org.tkit.quarkus.dataimport.tests;

import java.nio.charset.StandardCharsets;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tkit.quarkus.dataimport.DataImport;
import org.tkit.quarkus.dataimport.DataImportConfig;
import org.tkit.quarkus.dataimport.DataImportService;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

@DataImport("key1")
public class ParameterTestImport implements DataImportService {

    private static final Logger log = LoggerFactory.getLogger(ParameterTestImport.class);

    @Inject
    ParameterTestEntityDAO dao;

    @Inject
    ObjectMapper mapper;

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void importData(DataImportConfig config) {
        log.info("File: {}", config.getFile());
        log.info("MD5: {}", config.getMD5());
        log.info("Metadata: {}", config.getMetadata());
        log.info("Data: \n {}", new String(config.getData(), StandardCharsets.UTF_8));

        try {
            dao.deleteAll();
            JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, ParameterTestEntity.class);
            List<ParameterTestEntity> entries = mapper.readValue(config.getData(), type);
            dao.create(entries);
        } catch (Exception ex) {
            throw new RuntimeException("Error import", ex);
        }
    }
}
