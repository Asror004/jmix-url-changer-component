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
    private final HashMap<Class<? extends StandardView>, Map<String, String>> paramsBean;
    private UrlChangerConfig config;

    public UrlChanger(List<UrlChangerConfig> configs, DialogWindows dialogWindows,
                      HashMap<Class<? extends StandardView>, Map<String, String>> paramsBean) {
        this.dialogWindows = dialogWindows;
        this.paramsBean = paramsBean;
        configs.forEach(this::go);
    }

    public UrlChanger(UrlChangerConfig config, DialogWindows dialogWindows,
                      HashMap<Class<? extends StandardView>, Map<String, String>> paramsBean) {
        this.dialogWindows = dialogWindows;
        this.paramsBean = paramsBean;
        go(config);
    }

    private void go(UrlChangerConfig config) {
        this.config = config;

        Button button = config.getButton();
        paramsBean.put(config.getOpenViewInDialog(), config.getQueryParams());

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

        Map<String, String> headers = getHeaders(referer);

        for (String key : queryParams.keySet()) {
            if (headers.containsKey(key))
                referer = removeKeyInReferer(referer, key);

            sj.add(key + "=" + queryParams.get(key));
        }

        return referer.concat(sj.toString());
    }

    private void openDialogView(UrlChangerConfig config) {

        getWindowBuilder(config.getView(), config.getOpenViewInDialog())
                .withAfterCloseListener(afterCloseEvent -> close(new ArrayList<>(paramsBean.get(config.getOpenViewInDialog()).keySet()),
                        config.getView().getUI()))
                .open();
    }

    private WindowBuilder<? extends StandardView> getWindowBuilder(StandardView view, Class<? extends StandardView> openView) {
        return dialogWindows.view(view, openView);
    }

    private void close(List<String> keyList, Optional<UI> ui) {
        String referer = VaadinService.getCurrentRequest().getHeader("referer");

        for (String key : keyList) {
            referer = removeKeyInReferer(referer, key);
        }

        String clearedUrl = referer;
        ui.ifPresent(uiEl -> uiEl.getPage().getHistory().pushState(null, clearedUrl));
    }

    private String removeKeyInReferer(String referer, String key) {
        return referer.replaceAll(key + "=[a-zA-Z0-9=]*&?", "");
    }

    public void initViews(List<Class<? extends StandardView>> openViews) {
        String url = VaadinService.getCurrentRequest().getHeader("referer");
        Map<String, String> headers = getHeaders(url);

        StandardView view = config.getView();

        openViews.forEach(openView -> {
            List<String> params = new ArrayList<>(paramsBean.get(openView).keySet());

            for (String key : params) {
                if (headers.containsKey(key)) {

                    getWindowBuilder(view, openView).withAfterCloseListener(closeEvent ->
                            close(params, view.getUI())
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
