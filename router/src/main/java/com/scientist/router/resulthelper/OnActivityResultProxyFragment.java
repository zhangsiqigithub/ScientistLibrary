package com.scientist.router.resulthelper;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/9/21
 * Time: 15:53
 * Desc:
 */
public class OnActivityResultProxyFragment extends Fragment{

    private SparseArray<PublishSubject<ActivityResultInfo>> mSubjects = new SparseArray<>();
    private SparseArray<OnActivityResult.Callback> mCallbacks = new SparseArray<>();

    public OnActivityResultProxyFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void startForResult(Intent intent, int requestCode, OnActivityResult.Callback callback) {
        mCallbacks.put(requestCode, callback);
        startActivityForResult(intent, requestCode);
    }

    public Observable<ActivityResultInfo> startForResult(Intent intent, int requestCode) {
        PublishSubject<ActivityResultInfo> subject = PublishSubject.create();
        mSubjects.put(requestCode, subject);
        return subject.doOnSubscribe(disposable -> startActivityForResult(intent, requestCode));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        PublishSubject<ActivityResultInfo> subject = mSubjects.get(requestCode);
        if (subject != null) {
            subject.onNext(new ActivityResultInfo(resultCode, data));
            subject.onComplete();
            mSubjects.delete(requestCode);
        }

        OnActivityResult.Callback callback = mCallbacks.get(requestCode);
        if (callback != null) {
            callback.onActivityResult(requestCode, resultCode, data);
            mCallbacks.delete(requestCode);
        }
    }
}
