package com.firebase.client.core;

import com.firebase.client.snapshot.ChildKey;

public class Constants {
    public static final ChildKey DOT_INFO;
    public static final ChildKey DOT_INFO_AUTHENTICATED;
    public static final ChildKey DOT_INFO_CONNECTED;
    public static final ChildKey DOT_INFO_SERVERTIME_OFFSET;
    public static final String WIRE_PROTOCOL_VERSION = "5";

    static {
        DOT_INFO = ChildKey.fromString(".info");
        DOT_INFO_SERVERTIME_OFFSET = ChildKey.fromString("serverTimeOffset");
        DOT_INFO_AUTHENTICATED = ChildKey.fromString("authenticated");
        DOT_INFO_CONNECTED = ChildKey.fromString("connected");
    }
}
