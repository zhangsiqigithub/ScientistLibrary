package resulthelper;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.v4.app.Fragment;

import io.reactivex.Observable;

/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/9/21
 * Time: 15:57
 * Desc:
 */
public class OnActivityResult {

    private static final String FRAGMENT_TAG = "OnResultFragment";
    private OnActivityResultProxyFragment mFragment;

    public OnActivityResult(Fragment fragment) {
        this(fragment.getActivity());
    }

    public OnActivityResult(Activity activity) {
        mFragment = getOnResultFragment(activity);
    }

    private OnActivityResultProxyFragment getOnResultFragment(Activity activity) {
        OnActivityResultProxyFragment fragment = findFragmentByTag(activity);
        if (fragment == null) {
            fragment = new OnActivityResultProxyFragment();
            FragmentManager fragmentManager = activity.getFragmentManager();
            fragmentManager.beginTransaction().add(fragment, FRAGMENT_TAG)
                    .commitAllowingStateLoss();
            activity.getFragmentManager().executePendingTransactions();
        }
        return fragment;
    }

    private OnActivityResultProxyFragment findFragmentByTag(Activity activity) {
        return (OnActivityResultProxyFragment) activity.getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    }

    public Observable<ActivityResultInfo> startActivityForResult(Intent intent, int requestCode) {
        return mFragment.startForResult(intent, requestCode);
    }

    public Observable<ActivityResultInfo> startActivityForResult(Class<?> cls, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(mFragment.getActivity(), cls);
        return mFragment.startForResult(intent, requestCode);
    }

    public void startActivityForResult(Intent intent, int requestCode, Callback callback) {
        mFragment.startForResult(intent, requestCode, callback);
    }
    public void startActivityForResult(Class<?> cls, int requestCode, Callback callback) {
        Intent intent = new Intent(mFragment.getActivity(), cls);
        mFragment.startForResult(intent, requestCode, callback);
    }

    public interface Callback {
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }
}
