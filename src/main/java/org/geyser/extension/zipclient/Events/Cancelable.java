package org.geyser.extension.zipclient.Events;

public abstract class Cancelable {
    private boolean isCanceled = false;

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    public boolean isCanceled() {
        return isCanceled;
    }
}
