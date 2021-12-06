package com.fidelreactlibrary;

import android.content.Context;

import com.facebook.react.bridge.ReadableMap;
import com.fidelreactlibrary.adapters.abstraction.ConstantsProvider;
import com.fidelreactlibrary.fakes.CallbackInputSpy;
import com.fidelreactlibrary.fakes.CallbackSpy;
import com.fidelreactlibrary.fakes.ConstantsProviderStub;
import com.fidelreactlibrary.fakes.DataProcessorSpy;
import com.fidelreactlibrary.fakes.ReactContextMock;
import com.fidelreactlibrary.fakes.ReadableMapStub;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static com.fidelreactlibrary.helpers.AssertHelpers.assertMapContainsMap;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class FidelModuleTests {
    
    private FidelModule sut;
    private DataProcessorSpy<ReadableMap> setupAdapterSpy;
    private List<ConstantsProvider> constantsProviderListStub;
    private CallbackInputSpy callbackInputSpy;
    
    @Before
    public final void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        ReactContextMock reactContext = new ReactContextMock(context);
        setupAdapterSpy = new DataProcessorSpy();
        constantsProviderListStub = new ArrayList<>();
        ConstantsProvider constantsProvider = new ConstantsProviderStub("testModuleConstantKey", 345);
        constantsProviderListStub.add(constantsProvider);
        callbackInputSpy = new CallbackInputSpy();
        sut = new FidelModule(reactContext,
                setupAdapterSpy,
                constantsProviderListStub,
                callbackInputSpy);
    }
    
    @After
    public final void tearDown() {
        sut = null;
        setupAdapterSpy = null;
        constantsProviderListStub = null;
    }

    @Test
    public void test_WhenGettingConstants_ReturnConstantsFromFirstConstantsProvider() {
        assertMapContainsMap(sut.getConstants(), constantsProviderListStub.get(0).getConstants());
    }

    @Test
    public void test_WhenAskedToSetup_ForwardSetupDataToSetupAdapter() {
        ReadableMapStub fakeMap = new ReadableMapStub();
        sut.setup(fakeMap);
        assertEquals(setupAdapterSpy.dataToProcess, fakeMap);
    }

    @Test
    public void test_WhenAddedOnResultListener_SendCallbackToInput() {
        CallbackSpy callback = new CallbackSpy();
        sut.onResult(callback);
        assertEquals(callbackInputSpy.receivedCallback, callback);
    }
}
