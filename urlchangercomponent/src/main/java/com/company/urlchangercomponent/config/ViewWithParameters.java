package com.company.urlchangercomponent.config;

import io.jmix.flowui.view.StandardView;

import java.util.List;
import java.util.Map;

public class ViewWithParameters {
    private Class<? extends StandardView> openView;
        private List<String> queryParams;

    public ViewWithParameters(Class<? extends StandardView> openView, List<String> queryParams) {
        this.openView = openView;
        this.queryParams = queryParams;
    }

    public Class<? extends StandardView> getOpenView() {
        return openView;
    }

    public List<String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(List<String> queryParams) {
        this.queryParams = queryParams;
    }

    public void setOpenView(Class<? extends StandardView> openView) {
        this.openView = openView;
    }


}
