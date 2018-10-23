package resulthelper;

import android.content.Intent;

/**
 * Author: zhangsiqi
 * Email: zsq901021@sina.com
 * Date: 2018/9/21
 * Time: 16:09
 * Desc:
 */
public class ActivityResultInfo {

    public ActivityResultInfo(int resultCode, Intent data) {
        this.resultCode = resultCode;
        this.data = data;
    }

    private int resultCode;
    private Intent data;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public Intent getData() {
        return data;
    }

    public void setData(Intent data) {
        this.data = data;
    }
}
