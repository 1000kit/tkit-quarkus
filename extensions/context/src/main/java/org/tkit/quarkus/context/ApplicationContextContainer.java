package org.tkit.quarkus.context;

import jakarta.enterprise.context.RequestScoped;

import io.quarkus.arc.Unremovable;

/**
 * This container is fallback for ApplicationContext
 *
 * @see <a href="https://github.com/quarkusio/quarkus/issues/29960">Issue 29960</a>
 */
@RequestScoped
@Unremovable
public class ApplicationContextContainer {

    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public void resume() {
        if (ApplicationContext.isEmpty()) {
            ApplicationContext.set(context);
        }
    }
}
