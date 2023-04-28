package org.tkit.quarkus.jpa.test;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.jpa.daos.AbstractDAO;

@ApplicationScoped
public class BusinessProjectDAO extends AbstractDAO<BusinessProject> {

}
