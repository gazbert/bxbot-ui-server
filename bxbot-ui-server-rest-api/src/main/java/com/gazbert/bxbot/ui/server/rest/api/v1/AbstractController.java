package com.gazbert.bxbot.ui.server.rest.api.v1;

import com.gazbert.bxbot.ui.server.rest.api.v1.config.ResponseDataWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Base class for all controllers.
 *
 * @author gazbert
 */
public class AbstractController {

    private static final Logger LOG = LogManager.getLogger();

    protected ResponseEntity<?> buildResponseEntity(Object entity, HttpStatus httpStatus) {
        final ResponseDataWrapper responseDataWrapper = new ResponseDataWrapper(entity);
        LOG.info("Response: " + responseDataWrapper);
        return new ResponseEntity<>(responseDataWrapper, null, httpStatus);
    }
}
