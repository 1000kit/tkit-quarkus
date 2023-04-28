package org.tkit.quarkus.jpa.test;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.quarkus.jpa.daos.AbstractDAO;

@ApplicationScoped
public class AddressDAO extends AbstractDAO<Address> {

}
