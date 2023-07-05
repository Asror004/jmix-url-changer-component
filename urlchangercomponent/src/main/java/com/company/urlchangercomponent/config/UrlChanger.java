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

/**
 * This class mainly serves to change the url when the dialog is opened.
 * Open dialogs can only be view classes
 */
public class UrlChanger {
    /**
     * DialogWindows.class is needed to open a dialog
     */
    private final DialogWindows dialogWindows;
    /**
     * Bean should have a scope session
     */
    private final LinkedHashMap<Class<? extends StandardView>, Map<String, String>> paramsBean;
    private UrlChangerConfig config;

    public UrlChanger(List<UrlChangerConfig> configs, DialogWindows dialogWindows,
                      LinkedHashMap<Class<? extends StandardView>, Map<String, String>> paramsBean) {
        this.dialogWindows = dialogWindows;
        this.paramsBean = paramsBean;
        configs.forEach(this::go);
    }

    public UrlChanger(UrlChangerConfig config, DialogWindows dialogWindows,
                      LinkedHashMap<Class<? extends StandardView>, Map<String, String>> paramsBean) {
        this.dialogWindows = dialogWindows;
        this.paramsBean = paramsBean;
        go(config);
    }

    @SuppressWarnings("unchecked")
    private void go(UrlChangerConfig config) {
        this.config = config;

        Button button = config.getButton();

        if (Objects.nonNull(config.getOpenViewInDialog()))
            paramsBean.put(config.getOpenViewInDialog(), (Map<String, String>) config.getQueryParams().clone());

        button.addClickListener(listener -> button.getUI().ifPresent(ui -> {
            if (Objects.isNull(config.getOpenViewInDialog())) {
                paramsBean.get(config.getView().getClass()).putAll(config.getQueryParams());
            }

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

    /**
     * To add new query parameters to the current URL
     * @param queryParams New query parameters to add to the URL
     * @param referer The current URL
     * @return The newly changed URL
     */
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

    /**
     * To open a new dialog
     * @param config UrlChangerConfig.class to get views
     */
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

    /**
     * To remove unnecessary parameters from the URL for now
     * @param referer Current URL
     * @param key The Query parameter to delete
     * @return The newly changed URL
     */
    private String removeKeyInReferer(String referer, String key) {
        return referer.replaceAll(key + "=[a-zA-Z0-9=]*&?", "");
    }

    /**
     * This method is needed to register popup dialog views. You must use this method only on the first view!
     * @param view First view
     * @param openViews Open dialog views together with query parameters
     */
    public void initViewsDialog(StandardView view, Map<Class<? extends StandardView>, List<String>> openViews) {
        Map<String, String> headers = getHeaders(null);

        openViews.forEach((openView, params) -> {
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

    /**
     * This method is for cases where the URL changes but the dialog does not open.
     * This method can be used in any dialog view.
     * @param params The method accepts a map with key of String and some Runnable action to its value.
     * Key of the map is responsible for query parameters in the current url, and the Runnable action associated with the query params is run whenever there is that key.
     */
    public void initView(Map<String, Runnable> params) {
        Map<String, String> headers = getHeaders(null);

        params.forEach((key, value) -> {
            if (Objects.nonNull(value) && headers.containsKey(key))
                value.run();
        });
    }

    /**
     * This method returns any given URL and query parameters as a Map view
     * @param url If no URL is given, URL will be equal to current URL
     * @return Parameters map
     */
    private Map<String, String> getHeaders(String url) {
        if (Objects.isNull(url))
            url = VaadinService.getCurrentRequest().getHeader("referer");

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
