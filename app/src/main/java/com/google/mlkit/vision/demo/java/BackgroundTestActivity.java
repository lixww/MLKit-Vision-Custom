package com.google.mlkit.vision.demo.java;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.annotation.KeepName;
import com.google.mlkit.vision.demo.R;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.accessibility.AccessibilityNodeProviderCompat;

@KeepName
public class BackgroundTestActivity extends AppCompatActivity {

    ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_background);

//        Callable<Integer> task = () -> {
//            int number = 5;
//            return number * number;
//        };

        executorService.submit(() -> {
            //get the name of current thread and process
            TextView textView = (TextView) getLayoutInflater().inflate(android.R.layout.simple_gallery_item, null);
            textView.setText("This is inflated dynamically");
//            textView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            ViewCompat.setAccessibilityDelegate(textView, new AccessibilityDelegateCompat() {

//                @Override
//                public void onInitializeAccessibilityNodeInfo(@NonNull View host, @NonNull AccessibilityNodeInfoCompat info) {
//                    super.onInitializeAccessibilityNodeInfo(host, info);
//                    // This would not work
//                    Rect newBounds = new Rect();
//                    info.getBoundsInScreen(newBounds);
//                    log("Old Bounds: " + newBounds);
//                    newBounds.set(newBounds.left, newBounds.top - 100, newBounds.right, newBounds.bottom + 100);
//                    log("New Bounds: " + newBounds);
//                    info.setBoundsInScreen(newBounds);
//                }

                @Nullable
                @Override
                public AccessibilityNodeProviderCompat getAccessibilityNodeProvider(@NonNull View host) {
                    return new AccessibilityNodeProviderCompat() {
                        @Nullable
                        @Override
                        public AccessibilityNodeInfoCompat createAccessibilityNodeInfo(int virtualViewId) {
                            if (virtualViewId == View.NO_ID) {
                                AccessibilityNodeInfoCompat info = AccessibilityNodeInfoCompat.obtain(textView);
                                onInitializeAccessibilityNodeInfo(textView, info);

                                Rect newBounds = new Rect();
                                info.getBoundsInScreen(newBounds);
                                log("Old Bounds: " + newBounds);
                                newBounds.set(newBounds.left, newBounds.top - 100, newBounds.right, newBounds.bottom + 100);
                                log("New Bounds: " + newBounds);
                                info.setBoundsInScreen(newBounds);
                                return info;
                            }
                            return null;
                        }

                        @Override
                        public boolean performAction(int virtualViewId, int action, @Nullable Bundle arguments) {
                            return textView.performAccessibilityAction(action, arguments);
                        }
                    };
                }


            });
            log("Thread name: " + Thread.currentThread().getName());
            log("Inflated TextView");
            runOnUiThread(() -> {
                log("Thread name: " + Thread.currentThread().getName());
                log("Adding TextView to layout on uiThread");
                LinearLayout container = (LinearLayout) findViewById(R.id.test_background_layout);
                container.addView(textView);
            });
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

    private void log(String message) {
        Log.d("abc", message);
    }
}
