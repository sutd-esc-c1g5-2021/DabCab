package com.hatsumi.bluentry_declaration.ui.home;

import android.util.MutableBoolean;

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
/*
    public MutableBoolean getTempDeclaration2_Done() {
        return tempDeclaration2_Done;
    }

    public MutableBoolean getTempDeclaration1_Done() {
        return tempDeclaration1_Done;
    }*/
}