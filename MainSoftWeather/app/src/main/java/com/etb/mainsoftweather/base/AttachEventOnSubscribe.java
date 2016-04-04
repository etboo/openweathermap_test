package com.etb.mainsoftweather.base;

import android.view.View;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

/**
 * Created by etb on 03.04.16.
 */

final class AttachEventOnSubscribe implements Observable.OnSubscribe<Boolean> {

    final View view;

    AttachEventOnSubscribe(View view) {
        this.view = view;
    }

    @Override public void call(final Subscriber<? super Boolean> subscriber) {

        if(!Utils.ensureMainThread())
            throw new IllegalStateException();

        final View.OnAttachStateChangeListener listener = new View.OnAttachStateChangeListener() {
            @Override public void onViewAttachedToWindow(final View v) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(true);
                }
            }

            @Override public void onViewDetachedFromWindow(final View v) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(false);
                }
            }
        };

        view.addOnAttachStateChangeListener(listener);

        subscriber.add(new MainThreadSubscription() {
            @Override protected void onUnsubscribe() {
                view.removeOnAttachStateChangeListener(listener);
            }
        });
    }
}
