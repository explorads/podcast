package com.explorads.podcast.net.sync.gpoddernet;

import com.explorads.podcast.net.sync.model.SyncServiceException;

public class GpodnetServiceException extends SyncServiceException {
    private static final long serialVersionUID = 1L;

    public GpodnetServiceException(String message) {
        super(message);
    }

    public GpodnetServiceException(Throwable e) {
        super(e);
    }
}
