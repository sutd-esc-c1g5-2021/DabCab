package com.hatsumi.bluentry_declaration.ui.declaration;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DeclarationViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DeclarationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Declaration");
    }

    public LiveData<String> getText() {
        return mText;
    }
}