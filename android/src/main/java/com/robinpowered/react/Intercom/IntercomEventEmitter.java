package com.robinpowered.react.Intercom;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import javax.annotation.Nullable;

import java.util.Map;
import java.util.HashMap;

import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.UnreadConversationCountListener;
import io.intercom.android.sdk.activities.IntercomMessengerActivity;

public class IntercomEventEmitter extends ReactContextBaseJavaModule {

    private static final String MODULE_NAME = "IntercomEventEmitter";
    public static final String TAG = "Intercom Event";
    private static final String UNREAD_CHANGE_NOTIFICATION = "IntercomUnreadConversationCountDidChangeNotification";
    private static final String WINDOW_DID_SHOW_NOTIFICATION = "IntercomWindowDidShowNotification";
    private static final String WINDOW_DID_HIDE_NOTIFICATION = "IntercomWindowDidHideNotification";

    private boolean mRegistered = false;

    public IntercomEventEmitter(ReactApplicationContext reactContext) {
        super(reactContext);
        Intercom.client().addUnreadConversationCountListener(unreadConversationCountListener);
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("UNREAD_CHANGE_NOTIFICATION", UNREAD_CHANGE_NOTIFICATION);
        constants.put("WINDOW_DID_SHOW_NOTIFICATION", WINDOW_DID_SHOW_NOTIFICATION);
        constants.put("WINDOW_DID_HIDE_NOTIFICATION", WINDOW_DID_HIDE_NOTIFICATION);
        return constants;
    }

    @ReactMethod
    private void registerShowingListener() {
        if (mRegistered) {
            return;
        }
        Activity activity = this.getCurrentActivity();
        if (activity != null) {
            mRegistered = true;
            activity.getApplication().registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
        }
    }

    @ReactMethod
    private void unregisterShowingListener() {
        if (!mRegistered) {
            return;
        }
        Activity activity = this.getCurrentActivity();
        if (activity != null) {
            mRegistered = false;
            activity.getApplication().unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
        }
    }

    private void handleUpdateUnreadCount() {
        WritableMap params = Arguments.createMap();
        params.putInt("count", Intercom.client().getUnreadConversationCount());
        sendEvent(UNREAD_CHANGE_NOTIFICATION, params);
    }

    private void sendEvent(String eventName, @Nullable WritableMap params) {
        if (getReactApplicationContext().hasActiveCatalystInstance()) {
            try {
                getReactApplicationContext()
                        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit(eventName, params);
            } catch (Exception e) {
                Log.e(TAG, "sendEvent called before bundle loaded");
            }
        }
    }

    private final UnreadConversationCountListener unreadConversationCountListener = new UnreadConversationCountListener() {
        @Override
        public void onCountUpdate(int conversationCount) {
            handleUpdateUnreadCount();
        }
    };

    private final Application.ActivityLifecycleCallbacks activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (activity.getClass().equals(IntercomMessengerActivity.class)) {
                sendEvent(WINDOW_DID_SHOW_NOTIFICATION, null);
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            sendEvent(WINDOW_DID_HIDE_NOTIFICATION, null);
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    };
}