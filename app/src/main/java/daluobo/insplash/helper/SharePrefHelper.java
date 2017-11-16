package daluobo.insplash.helper;

import daluobo.insplash.common.AppConstant;
import daluobo.insplash.common.MyApplication;
import daluobo.insplash.util.SharePrefUtil;

/**
 * Created by daluobo on 2017/11/16.
 */

public class SharePrefHelper {

    public static String getAccessToken() {
        return SharePrefUtil.getPreference(MyApplication.getInstance(), AppConstant.SharePref.ACCESS_TOKEN);
    }

    public static String getRefreshToken() {
        return SharePrefUtil.getPreference(MyApplication.getInstance(), AppConstant.SharePref.REFRESH_TOKEN);
    }
}