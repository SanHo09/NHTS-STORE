package com.nhom4.nhtsstore.services;

import rx.subjects.PublishSubject;

/**
 *
 * @author NamDang
 */
public class EventBus {
    // Transfer data from list screen to edit screen
    private static final PublishSubject<Object> entitySubject = PublishSubject.create();

    public static void postEntity(Object entity) {
        entitySubject.onNext(entity);
    }

    public static PublishSubject<Object> getEntitySubject() {
        return entitySubject;
    }
    
    // Reload list screen after access edit screen and create/edit/delete
    private static final PublishSubject<Boolean> reloadSubject = PublishSubject.create();

    public static void postReload(Boolean isReload) {
        reloadSubject.onNext(isReload);
    }

    public static PublishSubject<Boolean> getReloadSubject() {
        return reloadSubject;
    }
}
