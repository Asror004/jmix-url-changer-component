package com.company.urlchangercomponent.config;

import io.jmix.flowui.view.StandardView;

import java.util.Map;

public class ViewWithParameters {
    private Class<? extends StandardView> openView;
    private Map<String, String> queryParams;

    public ViewWithParameters(Class<? extends StandardView> openView, Map<String, String> queryParams) {
        this.openView = openView;
        this.queryParams = queryParams;
    }

    public Class<? extends StandardView> getOpenView() {
        return openView;
    }

    public void setOpenView(Class<? extends StandardView> openView) {
        this.openView = openView;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }
}
