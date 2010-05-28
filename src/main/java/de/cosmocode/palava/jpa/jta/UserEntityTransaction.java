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
            return tx.getStatus() != Status.STATUS_NO_TRANSACTION && 
                tx.getStatus() != Status.STATUS_UNKNOWN &&
                tx.getStatus() != Status.STATUS_ROLLEDBACK;
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
