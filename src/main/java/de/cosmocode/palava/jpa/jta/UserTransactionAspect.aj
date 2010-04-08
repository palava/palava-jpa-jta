package de.cosmocode.palava.jpa.jta;

import de.cosmocode.palava.jta.AbstractUserTransactionAspect;

import de.cosmocode.palava.jpa.Transactional;

public aspect UserTransactionAspect extends AbstractUserTransactionAspect {

    protected pointcut transactional(): execution(@Transactional * *.*(..));

}
