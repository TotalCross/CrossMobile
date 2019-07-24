/*
 * (c) 2019 by Panayotis Katsaloulis
 *
 * CrossMobile is free software; you can redistribute it and/or modify
 * it under the terms of the CrossMobile Community License as published
 * by the CrossMobile team.
 *
 * CrossMobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * CrossMobile Community License for more details.
 *
 * You should have received a copy of the CrossMobile Community
 * License along with CrossMobile; if not, please contact the
 * CrossMobile team at https://crossmobile.tech/contact/
 */
package org.crossmobile.backend.android.notifications;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import crossmobile.ios.uikit.UIApplication;
import org.crossmobile.bridge.Native;

import java.util.HashMap;
import java.util.Map;

public class CrossMobileFirebaseMessagingService extends FirebaseMessagingService {
    public static final String NAME = "CrossMobileFirebaseMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        System.out.println(remoteMessage.getData());
        Map<String, Object> userInfo = new HashMap<>();
        if (remoteMessage.getNotification() != null) {
            String remoteMessageBody;
            if ((remoteMessageBody = remoteMessage.getNotification().getBody()) != null)
                userInfo.put("body", remoteMessageBody);

            String remoteMessageClickAction;
            if ((remoteMessageClickAction = remoteMessage.getNotification().getClickAction()) != null)
                userInfo.put("clickAction", remoteMessageClickAction);

            String remoteMessageColor;
            if ((remoteMessageColor = remoteMessage.getNotification().getColor()) != null)
                userInfo.put("color", remoteMessageColor);

            String remoteMessageIcon;
            if ((remoteMessageIcon = remoteMessage.getNotification().getIcon()) != null)
                userInfo.put("icon", remoteMessageIcon);

            String remoteMessageSound;
            if ((remoteMessageSound = remoteMessage.getNotification().getSound()) != null)
                userInfo.put("sound", remoteMessageSound);

            String remoteMessageTag;
            if ((remoteMessageTag = remoteMessage.getNotification().getTag()) != null)
                userInfo.put("tag", remoteMessageTag);

            String remoteMessageTitle;
            if ((remoteMessageTitle = remoteMessage.getNotification().getTitle()) != null)
                userInfo.put("title", remoteMessageTitle);

        }
        if (remoteMessage.getData() != null)
            userInfo.putAll(remoteMessage.getData());
        // Handle data payload of FCM messages.
        Native.system().debug("FCM Message Id: " + remoteMessage.getMessageId(), null);
        Native.system().debug("FCM Notification Message: " + remoteMessage.getNotification(), null);
        Native.system().debug("FCM Data Message: " + remoteMessage.getData(), null);
        Native.system().debug("userInfo " + userInfo, null);
        if (UIApplication.sharedApplication() != null && UIApplication.sharedApplication().delegate() != null)
            UIApplication.sharedApplication().delegate().didReceiveRemoteNotification(UIApplication.sharedApplication(), userInfo);

    }
}
