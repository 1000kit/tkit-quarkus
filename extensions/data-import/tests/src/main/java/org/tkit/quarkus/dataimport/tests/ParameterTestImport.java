package org.tkit.quarkus.dataimport.tests;

import java.nio.charset.StandardCharsets;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.tkit.quarkus.dataimport.DataImport;
import org.tkit.quarkus.dataimport.DataImportConfig;
import org.tkit.quarkus.dataimport.DataImportService;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

@DataImport("key1")
public class ParameterTestImport implements DataImportService {

    @Inject
    ParameterTestEntityDAO dao;

    @Inject
    UserDAO userDAO;

    @Inject
    ObjectMapper mapper;

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void importData(DataImportConfig config) {
        System.out.println("File: " + config.getFile());
        System.out.println("MD5: " + config.getMD5());
        System.out.println("Metadata: " + config.getMetadata());
        System.out.println("Data: \n" + new String(config.getData(), StandardCharsets.UTF_8));

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
