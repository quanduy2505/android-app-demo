package com.facebook.share.internal;

import com.facebook.internal.Validate;
import com.facebook.share.model.GameRequestContent;
import com.facebook.share.model.GameRequestContent.ActionType;

public class GameRequestValidation {
    public static void validate(GameRequestContent content) {
        int i;
        int i2 = 0;
        Validate.notNull(content.getMessage(), ShareConstants.WEB_DIALOG_PARAM_MESSAGE);
        if (content.getObjectId() != null) {
            i = 1;
        } else {
            i = 0;
        }
        if (content.getActionType() == ActionType.ASKFOR || content.getActionType() == ActionType.SEND) {
            i2 = 1;
        }
        if ((i ^ i2) != 0) {
            throw new IllegalArgumentException("Object id should be provided if and only if action type is send or askfor");
        }
        int mutex = 0;
        if (content.getRecipients() != null) {
            mutex = 0 + 1;
        }
        if (content.getSuggestions() != null) {
            mutex++;
        }
        if (content.getFilters() != null) {
            mutex++;
        }
        if (mutex > 1) {
            throw new IllegalArgumentException("Parameters to, filters and suggestions are mutually exclusive");
        }
    }
}
