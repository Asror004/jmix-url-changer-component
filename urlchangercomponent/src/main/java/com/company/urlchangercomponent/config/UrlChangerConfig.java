package com.company.urlchangercomponent.config;

import com.vaadin.flow.component.button.Button;
import io.jmix.flowui.view.StandardView;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;

public class UrlChangerConfig {
    /**
     * When this button is clicked, the URL will change
     */
    private Button button;
    /**
     * Additional user codes executed when the button is clicked
     */
    private Consumer<Button> consumer;
    /**
     * Newly added query parameters
     */
    private LinkedHashMap<String ,String> queryParams;
    /**
     * If the dialog opens, it opens to this view.
     * It should never be null because it is always used!
     */
    private final StandardView view;
    /**
     * The dialog view that should open. If the dialog does not open, you can null it.
     */
    private final Class<? extends StandardView> openViewInDialog;

    public UrlChangerConfig(Button button, Consumer<Button> consumer, LinkedHashMap<String, String> queryParams, @NonNull StandardView view, Class<? extends StandardView> openViewInDialog) {
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
