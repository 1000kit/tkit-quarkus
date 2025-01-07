package org.tkit.quarkus.it.jpa.tenant;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.jpa.daos.AbstractDAO;

@ApplicationScoped
public class UserDAO extends AbstractDAO<User> {

}
