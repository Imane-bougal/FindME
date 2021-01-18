package com.imaneb.findme.utils;


import android.widget.EditText;

import com.jakewharton.rxbinding3.widget.RxTextView;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class RxBindingHelper {

    public static Observable<String> getObservableFrom(EditText editText) {
        return RxTextView.textChanges(editText).skip(1).map(new Function<CharSequence, String>() {
                    @Override
                    public String apply(CharSequence charSequence) throws Exception {
                        return charSequence.toString();
                    }
        });
    }
}
