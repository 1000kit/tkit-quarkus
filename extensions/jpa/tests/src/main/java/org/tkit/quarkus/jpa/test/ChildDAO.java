package org.tkit.quarkus.jpa.test;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.jpa.daos.AbstractDAO;

@ApplicationScoped
public class ChildDAO extends AbstractDAO<Child> {
}
