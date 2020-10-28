package com.qudini.reactive.logging.log4j2;

import com.qudini.reactive.utils.metadata.MetadataService;

public interface Tracker {

    void track(QudiniLogEvent event);

    default void init(MetadataService metadataService) {
        // no-op by default
    }

}
