package org.tkit.quarkus.dataimport.log;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import io.quarkus.arc.Unremovable;

@Entity
@Unremovable
@Table(name = "dataimportlog")
@NamedQuery(name = "findAll", query = "SELECT d FROM DataImportLog d")
public class DataImportLog {

    @Id
    @Column(name = "id")
    String id;

    @Column(name = "file")
    String file;

    @Column(name = "md5")
    String md5;

    @Column(name = "creationdate")
    LocalDateTime creationDate;

    @Column(name = "modificationdate")
    LocalDateTime modificationDate;

    @Column(name = "error")
    String error;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
