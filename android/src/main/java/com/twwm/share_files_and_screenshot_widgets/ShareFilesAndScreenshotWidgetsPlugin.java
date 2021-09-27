package com.twwm.share_files_and_screenshot_widgets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** ShareFilesAndScreenshotWidgetsPlugin */
public class ShareFilesAndScreenshotWidgetsPlugin implements FlutterPlugin, ActivityAware, MethodCallHandler {

    private final String PROVIDER_AUTH_EXT = ".fileprovider.share_files_and_screenshot_widgets";
    private Registrar _registrar;
    private Activity activity;
    private MethodChannel channel;

    private ShareFilesAndScreenshotWidgetsPlugin(Registrar registrar) {
        this._registrar = registrar;
    }

    public ShareFilesAndScreenshotWidgetsPlugin() {
        this.onDetachedFromEngine(null);
    }

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "channel:share_files_and_screenshot_widgets");
        channel.setMethodCallHandler(new ShareFilesAndScreenshotWidgetsPlugin(registrar));
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (call.method.equals("text")) {
            text(call.arguments);
        }
        if (call.method.equals("file")) {
            file(call.arguments);
        }
        if (call.method.equals("files")) {
            files(call.arguments);
        }
    }

    private void text(Object arguments) {
        @SuppressWarnings("unchecked")
        HashMap<String, String> argsMap = (HashMap<String, String>) arguments;
        String title = argsMap.get("title");
        String text = argsMap.get("text");
        String mimeType = argsMap.get("mimeType");

        Context activeContext = activity != null ? activity.getApplicationContext() : _registrar.activeContext();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.setType(mimeType);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);

        Intent chooserIntent = Intent.createChooser(shareIntent, title);
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activeContext.startActivity(chooserIntent);
    }

    private void file(Object arguments) {
        @SuppressWarnings("unchecked")
        HashMap<String, String> argsMap = (HashMap<String, String>) arguments;
        String title = argsMap.get("title");
        String name = argsMap.get("name");
        String mimeType = argsMap.get("mimeType");
        String text = argsMap.get("text");

        Context activeContext = activity != null ? activity.getApplicationContext() : _registrar.activeContext();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.setType(mimeType);
        File file = new File(activeContext.getCacheDir(), name );
        String fileProviderAuthority = activeContext.getPackageName() + PROVIDER_AUTH_EXT;
        Uri contentUri = FileProvider.getUriForFile(activeContext, fileProviderAuthority, file);
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        // add optional text
        if (!text.isEmpty()) shareIntent.putExtra(Intent.EXTRA_TEXT, text);

        Intent chooserIntent = Intent.createChooser(shareIntent, title);
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activeContext.startActivity(chooserIntent);
    }

    private void files(Object arguments) {
        @SuppressWarnings("unchecked")
        HashMap<String, Object> argsMap = (HashMap<String, Object>) arguments;
        String title = (String) argsMap.get("title");

        @SuppressWarnings("unchecked")
        ArrayList<String> names = (ArrayList<String>) argsMap.get("names");
        String mimeType = (String) argsMap.get("mimeType");
        String text = (String) argsMap.get("text");

        Context activeContext = activity != null ? activity.getApplicationContext() : _registrar.activeContext();

        Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.setType(mimeType);

        ArrayList<Uri> contentUris = new ArrayList<>();

        for (String name : names) {
            File file = new File(activeContext.getCacheDir(), name);
            String fileProviderAuthority = activeContext.getPackageName() + PROVIDER_AUTH_EXT;
            contentUris.add(FileProvider.getUriForFile(activeContext, fileProviderAuthority, file));
        }

        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, contentUris);
        // add optional text
        if (!text.isEmpty()) shareIntent.putExtra(Intent.EXTRA_TEXT, text);

        Intent chooserIntent = Intent.createChooser(shareIntent, title);
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activeContext.startActivity(chooserIntent);
    }

    /* FlutterPlugin implementation */

    @Override
    public void onAttachedToEngine(FlutterPluginBinding binding) {
        this.channel = new MethodChannel(binding.getBinaryMessenger(), "channel:share_files_and_screenshot_widgets");
    }

    @Override
    public void onDetachedFromEngine(FlutterPluginBinding binding) {
        this._registrar = null;
        this.activity = null;
        this.channel = null;
    }

    /* ActivityAware implementation */

    @Override
    public void onAttachedToActivity(ActivityPluginBinding activityPluginBinding) {
        this.initPluginFromPluginBinding(activityPluginBinding);
    }
    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding activityPluginBinding) {
        this.initPluginFromPluginBinding(activityPluginBinding);
    }
    private void initPluginFromPluginBinding (ActivityPluginBinding activityPluginBinding) {
        this.activity = activityPluginBinding.getActivity();

        this.channel.setMethodCallHandler(this);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        this.releaseResources();
    }
    @Override
    public void onDetachedFromActivity() {
        this.releaseResources();
    }
    private void releaseResources() {
        this.activity.finish();
    }

}
