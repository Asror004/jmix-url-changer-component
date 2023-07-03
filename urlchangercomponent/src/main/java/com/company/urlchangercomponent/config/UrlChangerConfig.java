package com.company.urlchangercomponent.config;

import com.vaadin.flow.component.button.Button;
import io.jmix.flowui.view.StandardView;

import java.util.Map;
import java.util.function.Consumer;

public class UrlChangerConfig {
    private Button button;
    private Consumer<Button> consumer;
    private Map<String, String> queryParams;
    private final StandardView view;
    private final Class<? extends StandardView> openViewInDialog;

    public UrlChangerConfig(Button button, Consumer<Button> consumer, Map<String, String> queryParams, StandardView view, Class<? extends StandardView> openViewInDialog) {
        this.button = button;
        this.consumer = consumer;
        this.queryParams = queryParams;
        this.view = view;
        this.openViewInDialog = openViewInDialog;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public Consumer<Button> getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer<Button> consumer) {
        this.consumer = consumer;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public Class<? extends StandardView> getOpenViewInDialog() {
        return openViewInDialog;
    }

    public StandardView getView() {
        return view;
    }
}
