package org.tkit.quarkus.jpa.test;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;

@ApplicationScoped
public class ParentDAO extends AbstractDAO<Parent> {

  @Transactional(Transactional.TxType.NOT_SUPPORTED)
  Parent loadById(String id) {
    var cb = this.getEntityManager().getCriteriaBuilder();
    var cq = cb.createQuery(Parent.class);
    var root = cq.from(Parent.class);

    cq.where(cb.equal(root.get("id"), id));
    Parent p = this.getEntityManager().createQuery(cq).setHint(HINT_LOAD_GRAPH, this.getEntityManager().getEntityGraph("Parent.loadChildren")).getSingleResult();
    this.getEntityManager().detach(p);
    return p;
  }

  PageResult<Parent> loadPage() {
    var cb = this.getEntityManager().getCriteriaBuilder();
    var cq = cb.createQuery(Parent.class);
    var root = cq.from(Parent.class);

    cq.where(root.get("id").isNotNull());
    return this.createPageQuery(cq, Page.of(0,10), "Parent.loadChildren").getPageResult();
  }
}
