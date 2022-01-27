package org.tkit.quarkus.it.jpa;

import org.tkit.quarkus.jpa.daos.AbstractDAO;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserDAO extends AbstractDAO<User> {

}
