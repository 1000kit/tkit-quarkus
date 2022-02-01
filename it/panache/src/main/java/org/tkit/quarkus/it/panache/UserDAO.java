package org.tkit.quarkus.it.panache;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserDAO implements PanacheRepositoryBase<User, String> {

}
