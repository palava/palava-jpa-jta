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

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import de.cosmocode.patterns.Adapter;

/**
 * An {@link Adapter} from {@link UserTransaction} to {@link EntityTransaction}.
 *
 * @author Willi Schoenborn
 */
@Adapter(EntityTransaction.class)
final class UserEntityTransaction implements EntityTransaction {

    private final UserTransaction tx;
    
    @Inject
    public UserEntityTransaction(UserTransaction tx) {
        this.tx = Preconditions.checkNotNull(tx, "Tx");
    }

    @Override
    public void begin() {
        Preconditions.checkState(!isActive(), "Transaction is already active");
        try {
            tx.begin();
        } catch (NotSupportedException e) {
            throw new UnsupportedOperationException(e);
        } catch (SystemException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void commit() {
        Preconditions.checkState(isActive(), "Transaction is not active");
        try {
            tx.commit();
        } catch (SecurityException e) {
            throw new RollbackException(e);
        } catch (javax.transaction.RollbackException e) {
            throw new RollbackException(e);
        } catch (HeuristicMixedException e) {
            throw new RollbackException(e);
        } catch (HeuristicRollbackException e) {
            throw new RollbackException(e);
        } catch (SystemException e) {
            throw new RollbackException(e);
        }
    }

    @Override
    public boolean getRollbackOnly() {
        Preconditions.checkState(isActive(), "Transaction is not active");
        try {
            return tx.getStatus() == Status.STATUS_MARKED_ROLLBACK;
        } catch (SystemException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean isActive() {
        try {
            return tx.getStatus() == Status.STATUS_ACTIVE;
        } catch (SystemException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void rollback() {
        Preconditions.checkState(isActive(), "Transaction is not active");
        try {
            tx.rollback();
        } catch (IllegalStateException e) {
            throw new PersistenceException(e);
        } catch (SecurityException e) {
            throw new PersistenceException(e);
        } catch (SystemException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void setRollbackOnly() {
        Preconditions.checkState(isActive(), "Transaction is not active");
        try {
            tx.setRollbackOnly();
        } catch (SystemException e) {
            throw new IllegalStateException(e);
        }
    }
    
}
