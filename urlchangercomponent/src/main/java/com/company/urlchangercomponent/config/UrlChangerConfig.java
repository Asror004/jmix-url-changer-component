package com.company.urlchangercomponent.config;

import com.vaadin.flow.component.button.Button;
import io.jmix.flowui.view.StandardView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;

public class UrlChangerConfig {
    private Button button;
    private Consumer<Button> consumer;
    private LinkedHashMap<String ,String> queryParams;
    private final StandardView view;
    private final Class<? extends StandardView> openViewInDialog;

    public UrlChangerConfig(Button button, Consumer<Button> consumer, LinkedHashMap<String, String> queryParams, StandardView view, Class<? extends StandardView> openViewInDialog) {
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

    public LinkedHashMap<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(LinkedHashMap<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public Class<? extends StandardView> getOpenViewInDialog() {
        return openViewInDialog;
    }

    public StandardView getView() {
        return view;
    }


    public List<String> getParamKeyList(){
        return new ArrayList<>(queryParams.keySet());
    }
}
