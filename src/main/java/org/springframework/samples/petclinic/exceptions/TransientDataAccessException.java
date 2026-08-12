package org.springframework.samples.petclinic.exceptions;

import org.springframework.dao.DataAccessException;

/**
 * Custom exception to indicate a transient data access failure that can be retried.
 */
public class TransientDataAccessException extends DataAccessException {

    public TransientDataAccessException(String msg) {
        super(msg);
    }

    public TransientDataAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
