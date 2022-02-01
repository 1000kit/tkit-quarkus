package org.tkit.quarkus.it.panache.reactive;



import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserDAO implements PanacheRepositoryBase<User, String> {

}
