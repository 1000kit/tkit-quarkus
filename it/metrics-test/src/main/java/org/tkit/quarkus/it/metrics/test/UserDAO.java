package org.tkit.quarkus.it.metrics.test;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.jpa.daos.AbstractDAO;

@ApplicationScoped
public class UserDAO extends AbstractDAO<User> {

}
