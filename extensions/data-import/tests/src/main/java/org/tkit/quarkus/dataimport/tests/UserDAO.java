package org.tkit.quarkus.dataimport.tests;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.jpa.daos.AbstractDAO;

@ApplicationScoped
public class UserDAO extends AbstractDAO<User> {

}
