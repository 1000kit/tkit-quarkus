package org.tkit.quarkus.it.panache;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

@ApplicationScoped
public class UserDAO implements PanacheRepositoryBase<User, String> {

}
