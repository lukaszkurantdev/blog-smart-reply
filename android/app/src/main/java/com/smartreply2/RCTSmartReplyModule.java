package com.smartreply2;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableNativeArray;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.nl.smartreply.SmartReply;
import com.google.mlkit.nl.smartreply.SmartReplyGenerator;
import com.google.mlkit.nl.smartreply.SmartReplySuggestion;
import com.google.mlkit.nl.smartreply.SmartReplySuggestionResult;
import com.google.mlkit.nl.smartreply.TextMessage;

import java.util.ArrayList;
import java.util.List;


public class RCTSmartReplyModule extends ReactContextBaseJavaModule {
    RCTSmartReplyModule(ReactApplicationContext context) {
        super(context);
    }

    public String getName() {
        return "SmartReplyModule";
    }

    @ReactMethod
    public void getSmartReply(ReadableArray messages, Promise promise) {
        List<TextMessage> conversation = new ArrayList<>();

        for(int i = 0; i < messages.size(); i++) {
            ReadableMap message = messages.getMap(i);

            if(message.getBoolean("isLocalUser")) {
                conversation.add(TextMessage.createForLocalUser(
                        message.getString("text"), System.currentTimeMillis()
                ));

            } else {
                conversation.add(TextMessage.createForRemoteUser(
                        message.getString("text"), System.currentTimeMillis(), message.getString("userId")));
            }
        }

        SmartReplyGenerator smartReply = SmartReply.getClient();
        smartReply.suggestReplies(conversation)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        SmartReplySuggestionResult result = (SmartReplySuggestionResult) o;

                        if (result.getStatus() == SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE) {
                            promise.reject("failure", "unsupported language");
                        } else if (result.getStatus() == SmartReplySuggestionResult.STATUS_SUCCESS) {
                            WritableArray suggestions = new WritableNativeArray();

                            for (SmartReplySuggestion suggestion : result.getSuggestions()) {
                                suggestions.pushString(suggestion.getText());
                            }

                            promise.resolve(suggestions);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                      promise.reject("failure", "unknown error");
                    }
                });
    }
}
