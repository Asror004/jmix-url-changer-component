package com.company.urlchangercomponent.config;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.server.VaadinService;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.view.StandardView;
import io.jmix.flowui.view.builder.WindowBuilder;

import java.util.*;
import java.util.function.Consumer;

public class UrlChanger {
    private final DialogWindows dialogWindows;

    public UrlChanger(List<UrlChangerConfig> configs, DialogWindows dialogWindows) {
        this.dialogWindows = dialogWindows;
        configs.forEach(this::go);
    }

    public UrlChanger(UrlChangerConfig config, DialogWindows dialogWindows) {
        this.dialogWindows = dialogWindows;
        go(config);
    }

    private void go(UrlChangerConfig config) {
        Button button = config.getButton();

        button.addClickListener(listener -> button.getUI().ifPresent(ui -> {
            Page page = ui.getPage();
            String referer = VaadinService.getCurrentRequest().getHeader("referer");

            page.getHistory().replaceState(null, getUrl(config.getQueryParams(), referer));

            if (Objects.nonNull(config.getOpenViewInDialog()))
                openDialogView(config);

            Consumer<Button> consumer = config.getConsumer();

            if (Objects.nonNull(consumer))
                consumer.accept(button);
        }));
    }

    private String getUrl(Map<String, String> queryParams, String referer) {
        referer += (!referer.contains("?")) ? "?" : "&";

        StringJoiner sj = new StringJoiner("&");

        queryParams.keySet().forEach(key -> sj.add(key + "=" + queryParams.get(key)));

        return referer.concat(sj.toString());
    }

    private void openDialogView(UrlChangerConfig config) {

        getWindowBuilder(config.getView(), config.getOpenViewInDialog())
                .withAfterCloseListener(afterCloseEvent -> close(config.getParamKeyList(), config.getView().getUI()))
                .open();
    }

    private WindowBuilder<? extends StandardView> getWindowBuilder(StandardView view, Class<? extends StandardView> openView) {
        return dialogWindows.view(view, openView);
    }

    private void close(List<String> keyList, Optional<UI> ui) {
        String referer = VaadinService.getCurrentRequest().getHeader("referer");

        for (String key : keyList) {
            referer = referer.replaceAll(key + "=[a-zA-Z0-9=]*&?" ,"");
        }
        String clearedUrl = referer;
        ui.ifPresent(uiEl -> uiEl.getPage().getHistory().pushState(null, clearedUrl));
    }
    public void initViews(StandardView view, List<ViewWithParameters> openViews) {
        String url = VaadinService.getCurrentRequest().getHeader("referer");
        Map<String, String> headers = getHeaders(url);

        openViews.forEach(openView -> {
            for (String key : openView.getQueryParams()) {
                if (headers.containsKey(key)) {
                    getWindowBuilder(view, openView.getOpenView()).withAfterCloseListener(closeEvent ->
                            close(openView.getQueryParams(), view.getUI())
                    ).open();
                    break;
                }
            }
        });
    }

    private Map<String, String> getHeaders(String url) {
        Map<String, String> res = new HashMap<>();
        String[] split = url.substring(url.indexOf("?") + 1).split("&");

        Arrays.stream(split).forEach(s -> {
            String[] innerSplit = s.split("=");
            String key = innerSplit[0];
            String value = "";
            if (innerSplit.length > 1)
                value = innerSplit[1];
            res.put(key, value);
        });
        return res;

    }
}
