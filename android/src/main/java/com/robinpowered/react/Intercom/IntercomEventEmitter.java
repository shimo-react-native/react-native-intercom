package com.robinpowered.react.Intercom;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
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

public class IntercomEventEmitter extends ReactContextBaseJavaModule {

    private static final String MODULE_NAME = "IntercomEventEmitter";
    public static final String TAG = "Intercom Event";
    private static final String UNREAD_CHANGE_NOTIFICATION = "IntercomUnreadConversationCountDidChangeNotification";
    private static final String WINDOW_DID_SHOW_NOTIFICATION = "IntercomWindowDidShowNotification";
    private static final String WINDOW_DID_HIDE_NOTIFICATION = "IntercomWindowDidHideNotification";

    public IntercomEventEmitter(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Override
    public void initialize() {
        super.initialize();
        Intercom.client().addUnreadConversationCountListener(new UnreadConversationCountListener() {
            @Override
            public void onCountUpdate(int conversationCount) {
                handleUpdateUnreadCount();
            }
        });

        IntercomState.getInstance().setIntercomStateListener(new IntercomState.IntercomStateListener() {
            @Override
            public void onIntercomStateChanged(String intercomState) {
                if (intercomState.equals(IntercomState.INTERCOM_STATE_ACTIVE)) {
                    sendEvent(WINDOW_DID_SHOW_NOTIFICATION, null);
                } else if (intercomState.equals(IntercomState.INTERCOM_STATE_BACKGROUND)) {
                    sendEvent(WINDOW_DID_HIDE_NOTIFICATION, null);
                }
            }
        });
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
    private void getIntercomState(Callback callback) {
        callback.invoke(IntercomState.getInstance().getIntercomState());
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
}