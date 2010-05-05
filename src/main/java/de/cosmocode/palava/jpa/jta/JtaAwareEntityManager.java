/**
 * palava - a java-php-bridge
 * Copyright (C) 2007-2010  CosmoCode GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
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
