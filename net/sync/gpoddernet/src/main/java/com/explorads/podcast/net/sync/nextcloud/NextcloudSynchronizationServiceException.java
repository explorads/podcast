package com.explorads.podcast.net.sync.nextcloud;

import com.explorads.podcast.net.sync.model.SyncServiceException;

public class NextcloudSynchronizationServiceException extends SyncServiceException {
    public NextcloudSynchronizationServiceException(Throwable e) {
        super(e);
    }
}
