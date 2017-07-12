package com.gazbert.bxbot.ui.server.domain.exchange;

import com.google.common.base.MoreObjects;

/**
 * Created by gazbert on 11/07/17.
 */
public class BaseResponse {

    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("data", data)
                .toString();
    }
}
