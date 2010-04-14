package de.cosmocode.palava.jpa.jta;

import javax.persistence.EntityManager;

import com.google.inject.Binder;
import com.google.inject.Module;

import de.cosmocode.palava.scope.UnitOfWork;

/**
 * Binds {@link EntityManager} to {@link JtaAwareEntityManager}.
 *
 * @author Willi Schoenborn
 */
public final class JtaPersistenceModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(EntityManager.class).to(JtaAwareEntityManager.class).in(UnitOfWork.class);
    }
    
}
