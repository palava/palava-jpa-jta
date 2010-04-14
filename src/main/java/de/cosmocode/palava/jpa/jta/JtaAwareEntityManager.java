package de.cosmocode.palava.jpa.jta;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.transaction.UserTransaction;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;

import de.cosmocode.palava.jpa.ForwardingEntityManager;

/**
 * An {@link EntityManager} which uses a {@link UserTransaction} for transaction
 * management instead of an {@link EntityTransaction} but exposes the same api.
 * This means you can transparently use {@link EntityManager#getTransaction()}.
 * All method calls will be delegated to the current {@link UserTransaction}.
 *
 * @author Willi Schoenborn
 */
final class JtaAwareEntityManager extends ForwardingEntityManager {

    private final EntityManager manager;
    private final Provider<UserTransaction> provider;

    @Inject
    public JtaAwareEntityManager(EntityManagerFactory factory, Provider<UserTransaction> provider) {
        this.manager = Preconditions.checkNotNull(factory, "Factory").createEntityManager();
        this.provider = Preconditions.checkNotNull(provider, "Provider");
    }
    
    @Override
    protected EntityManager delegate() {
        return manager;
    }
    
    @Override
    public EntityTransaction getTransaction() {
        final UserTransaction utx = provider.get();
        return new UserEntityTransaction(utx); 
    }
    
    @Override
    public void joinTransaction() {
        
    }
    
}
