package jp.rdlabo.capacitor.plugin.screenshotevent;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "ScreenshotEvent")
public class ScreenshotEventPlugin extends Plugin {

    private ScreenshotEvent implementation;

    @Override
    public void load() {
        execute(
            () -> {
                System.out.println("ScreenshotEvent start");
                implementation = new ScreenshotEvent(getContext(), getActivity());
                implementation.setEventListener(this::onEvent);
            }
        );
    }

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }

    @PluginMethod
    public void startWatchEvent(PluginCall call) {
        System.out.println("ScreenshotEvent startWatchEvent Start");
        implementation.startWatchEvent();
    }

    @PluginMethod
    public void removeWatchEvent(PluginCall call) {
        System.out.println("ScreenshotEvent removeWatchEvent Start");
        //implementation.removeWatchEvent();
        JSObject ret = new JSObject();
        ret.put("path", "teste");
        notifyListeners("userDidTakeScreenshot", ret);
    }

    @PluginMethod
    void onEvent(String event, String path) {
        JSObject ret = new JSObject();
        ret.put("path", path);
        notifyListeners("userDidTakeScreenshot", ret);
    }
}
