
package com.fidelreactlibrary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.bridge.JavaScriptModule;
import com.fidelreactlibrary.adapters.CountryAdapter;
import com.fidelreactlibrary.adapters.FidelCountryAdapter;
import com.fidelreactlibrary.adapters.FidelOptionsAdapter;
import com.fidelreactlibrary.adapters.FidelSetupAdapter;
import com.fidelreactlibrary.adapters.ImageFromReadableMapAdapter;
import com.fidelreactlibrary.adapters.LinkResultDataConverter;
import com.fidelreactlibrary.adapters.abstraction.ConstantsProvider;
import com.fidelreactlibrary.events.CallbackActivityEventListener;

public class FidelPackage implements ReactPackage {
    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        ImageFromReadableMapAdapter imageAdapter =
                new ImageFromReadableMapAdapter(reactContext);
        CountryAdapter countryAdapter =
                new FidelCountryAdapter();
        FidelSetupAdapter setupAdapter =
                new FidelSetupAdapter();
        FidelOptionsAdapter optionsAdapter =
                new FidelOptionsAdapter(imageAdapter, countryAdapter);
        imageAdapter.bitmapOutput = optionsAdapter;
        List<ConstantsProvider> constantsProviderList =
                new ArrayList<>();
        constantsProviderList.add(optionsAdapter);
        LinkResultDataConverter linkResultConverter =
                new LinkResultDataConverter();
        CallbackActivityEventListener activityEventListener =
                new CallbackActivityEventListener(linkResultConverter);
        reactContext.addActivityEventListener(activityEventListener);
      return Arrays.<NativeModule>asList(
              new FidelModule(reactContext,
                      setupAdapter,
                      optionsAdapter,
                      constantsProviderList,
                      activityEventListener));
    }

    // Deprecated from RN 0.47
    public List<Class<? extends JavaScriptModule>> createJSModules() {
      return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
      return Collections.emptyList();
    }
}