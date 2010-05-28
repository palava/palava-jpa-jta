/**
 * Copyright 2010 CosmoCode GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cosmocode.palava.jpa.jta;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.transaction.UserTransaction;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;

import de.cosmocode.palava.jpa.ForwardingEntityManager;
import de.cosmocode.palava.scope.Destroyable;

/**
 * An {@link EntityManager} which uses a {@link UserTransaction} for transaction
 * management instead of an {@link EntityTransaction} but exposes the same api.
 * This means you can transparently use {@link EntityManager#getTransaction()}.
 * All method calls will be delegated to the current {@link UserTransaction}.
 *
 * @author Willi Schoenborn
 */
final class JtaAwareEntityManager extends ForwardingEntityManager implements Destroyable {

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
        return new UserEntityTransaction(provider.get()); 
    }
    
    @Override
    public void joinTransaction() {
        
    }
    
    @Override
    public void destroy() {
        if (isOpen()) close();
    }
    
}
