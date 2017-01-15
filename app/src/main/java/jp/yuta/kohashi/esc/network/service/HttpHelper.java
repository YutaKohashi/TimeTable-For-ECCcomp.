package jp.yuta.kohashi.esc.network.service;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.anprosit.android.promise.Callback;
import com.anprosit.android.promise.NextTask;
import com.anprosit.android.promise.Promise;
import com.anprosit.android.promise.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import jp.yuta.kohashi.esc.util.RegexManager;
import jp.yuta.kohashi.esc.util.preference.PrefManager;

/**
 * Created by yutakohashi on 2016/11/15.
 */

/**
 * ヘルパ内で保存処理を行う
 */
public class HttpHelper {
    private static final String TAG = HttpHelper.class.getSimpleName();

    private static String mLastResponseHtml;
    private static Context mContext;
    private static List<String> teacherHtmls;

    public static void init(Context context) {
        HttpBase.init();
        mLastResponseHtml = "";
        mContext = context;
    }

    /************************************   public   ********************************************************/

    /***
     * 時間割を取得,保存
     *
     * @param userId
     * @param password
     * @param successCallbacks
     */
    public static void getTImeTable(String userId, String password, final SuccessCallbacks successCallbacks) {
        requestTimeTable(userId, password, new AccessListCallbacks() {
            @Override
            public void callback(String html, List<String> htmls, boolean bool) {
                if (bool) {
                    PrefManager.saveTimeTable(html, htmls);
                }
                successCallbacks.callback(bool);
            }
        });
    }


    /***
     * 出席率を取得、保存
     *
     * @param userId
     * @param password
     * @param successCallbacks
     */
    public static void getAttendanceRate(String userId, String password, final SuccessCallbacks successCallbacks) {
        requestAttendanceRate(userId, password, new AccessCallbacks() {
            @Override
            public void callback(String html, boolean bool) {
                if (bool) {
                    PrefManager.saveAttendanceRate(html);
                    PrefManager.saveAttendanceAllRateData(html);
                }
                successCallbacks.callback(bool);
            }
        });
    }


    /**
     * 時間割と出席率を取得
     *
     * @param userId
     * @param password
     * @param successCallBacks
     */
    public static void getTimeAttend(final String userId, final String password, final SuccessCallbacks successCallBacks) {
        getTImeTable(userId, password, new SuccessCallbacks() {
            @Override
            public void callback(boolean bool) {
                if (bool) {
                    getAttendanceRate(userId, password, new SuccessCallbacks() {
                        @Override
                        public void callback(boolean bool) {
                            successCallBacks.callback(bool);
                        }
                    });
                } else {
                    successCallBacks.callback(false);
                }
            }
        });
    }


    /**
     * 学校からのお知らせ
     *
     * @param userId
     * @param password
     * @param successCallbacks
     */
    public static void getSchoolNews(final String userId, final String password, final SuccessCallbacks successCallbacks) {
        requestNews(userId, password, new AccessCallbacks() {
            @Override
            public void callback(String html, boolean bool) {
                if (bool) {
                    PrefManager.saveSchoolNews(html);
                }
                successCallbacks.callback(bool);
            }
        });
    }

    /**
     * 担任からのお知らせ
     *
     * @param userId
     * @param password
     * @param successCallbacks
     */
    public static void getTanninNews(final String userId, final String password, final SuccessCallbacks successCallbacks) {
        requestNews(userId, password, new AccessCallbacks() {
            @Override
            public void callback(String html, boolean bool) {
                if (bool) {
                    PrefManager.saveTanninNews(html);
                }
                successCallbacks.callback(bool);
            }
        });
    }

    /**
     * 担任・学校からのお知らせを取得するメソッド
     *
     * @param userId
     * @param password
     * @param successCallbacks
     */
    public static void getSchoolTanninNews(final String userId, final String password, final SuccessCallbacks successCallbacks) {
        getSchoolNews(userId, password, new SuccessCallbacks() {
            @Override
            public void callback(boolean bool) {
                if (bool) {
                    getTanninNews(userId, password, new SuccessCallbacks() {
                        @Override
                        public void callback(boolean bool) {
                            successCallbacks.callback(bool);
                        }
                    });
                } else {
                    successCallbacks.callback(false);
                }
            }
        });
    }

    /**
     * ニュース詳細を取得
     * @param userId
     * @param password
     * @param url
     * @param accessCallbacks
     */
    public static void getNewsDetail(final String userId, final String password, String url, final AccessCallbacks accessCallbacks) {
        requestNewsDetail(userId, password, url, new AccessCallbacks() {
            @Override
            public void callback(String html, boolean bool) {
                accessCallbacks.callback(html,bool);
            }
        });
    }

    /************************************   private   ********************************************************/

    //**
    //region 時間割
    //**

    /***
     * 時間割のリクエストメソッド
     *
     * @param userId
     * @param password
     * @param listCallbacks
     */
    private static void requestTimeTable(final String userId,
                                         final String password,
                                         final AccessListCallbacks listCallbacks) {

        if (teacherHtmls != null) teacherHtmls.clear();

        Promise.with(mContext, Void.class).thenOnAsyncThread(new Task<Void, Void>() {
            @Override
            public void run(Void aVoid, NextTask<Void> nextTask) {

                HttpResultClass result = loginToESC(userId, password);

                //failure
                if (!result.getBool()) nextTask.fail(new Bundle(), new Exception());

                mLastResponseHtml = result.getString();
                List<String> urls = getTeacherNameUrls(mLastResponseHtml);

                teacherHtmls = requestTeacherName(urls);

                //先生名取得失敗
                if (teacherHtmls == null) nextTask.fail(new Bundle(), new Exception());

                Log.d(TAG, mLastResponseHtml);
                nextTask.run(null);
            }
        }).setCallback(new Callback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listCallbacks.callback(mLastResponseHtml, teacherHtmls, true);
            }

            @Override
            public void onFailure(Bundle bundle, Exception e) {
                log(e);
                listCallbacks.callback(mLastResponseHtml, teacherHtmls, false);
            }
        }).create().execute(null);
    }

    /**
     * 引数のhtmlソースから先生名のurlを取り出すメソッド
     *
     * @param html
     * @return
     */
    private static List<String> getTeacherNameUrls(String html) {
        List<String> urls = new ArrayList<>();

        html = RegexManager.replaceCRLF(html, true);
        Matcher m = RegexManager.getGroupValues("<li class=\"letter\"><a href=\"(.+?)\">投書</a>", html);
        while (m.find()) {
            String url = m.group(1);
            urls.add(url);
        }
        return urls;
    }


    /***
     * 先生名のhtmlソースを取得するメソッド
     *
     * @param urls
     * @return
     */
    private static List<String> requestTeacherName(final List<String> urls) {

        final List<String> htmls = new ArrayList<>();

        for (String url : urls) {
            HttpResultClass result = HttpBase.httpGet(url, RequestURL.ESC_TO_PAGE);
            if (!result.getBool()) return null;

            htmls.add(result.getString());
        }
        return htmls;
    }

    //**
    //endregion
    //**


    //**
    //region 出席率
    //**

    /***
     * 出席率リクエスト　メソッド
     *
     * @param userId
     * @param password
     * @param accessCallbacks
     */
    private static void requestAttendanceRate(final String userId, final String password, final AccessCallbacks accessCallbacks) {

        Promise.with(mContext, Void.class).thenOnAsyncThread(new Task<Void, Void>() {
            @Override
            public void run(Void aVoid, NextTask<Void> nextTask) {

                //ログイン
                HttpResultClass result = loginToYS(userId, password);

                mLastResponseHtml = result.getString();
                //failure
                if (!result.getBool()) nextTask.fail(new Bundle(), new Exception());

                //出席率ページへリクエスト
                Map<String, String> body = CreateRequestBody.createPostDataForRatePage(mLastResponseHtml);
                result = HttpBase.httpPost(RequestURL.YS_TO_RATE_PAGE, body, RequestURL.ESC_LOGIN);
                mLastResponseHtml = result.getString();
                Log.d(TAG, mLastResponseHtml);

                //failure
                if (!result.getBool()) nextTask.fail(new Bundle(), new Exception());
                //TODO　遷移チェック


                nextTask.run(null);

            }
        }).setCallback(new Callback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                accessCallbacks.callback(mLastResponseHtml, true);
            }

            @Override
            public void onFailure(Bundle bundle, Exception e) {
                log(e);
                accessCallbacks.callback(mLastResponseHtml, false);
            }
        }).create().execute(null);
    }


    //**
    //endregion
    //**


    //**
    //region お知らせ
    //**

    private static void requestNews(final String userId, final String password, final AccessCallbacks accessCallbacks) {

        Promise.with(mContext, Void.class).thenOnAsyncThread(new Task<Void, Void>() {
            @Override
            public void run(Void aVoid, NextTask<Void> nextTask) {

                //ログイン
                HttpResultClass result = loginToESC(userId, password);

                //failure
                if (!result.getBool()) nextTask.fail(new Bundle(), new Exception());

                mLastResponseHtml = result.getString();

                Log.d(TAG, mLastResponseHtml);
                nextTask.run(null);
            }
        }).setCallback(new Callback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                accessCallbacks.callback(mLastResponseHtml, true);
            }

            @Override
            public void onFailure(Bundle bundle, Exception e) {
                log(e);
                accessCallbacks.callback(mLastResponseHtml, false);
            }
        }).create().execute(null);
    }

    private static void requestNewsDetail(final String userId, final String password, final String url, final AccessCallbacks accessCallbacks) {
        Promise.with(mContext, Void.class).thenOnAsyncThread(new Task<Void, Void>() {
            @Override
            public void run(Void aVoid, NextTask<Void> nextTask) {

                //ログイン
                HttpResultClass result = loginToESC(userId, password);
                if (!result.getBool()) nextTask.fail(new Bundle(), new Exception());

                mLastResponseHtml = result.getString();

                result = HttpBase.httpGet(url,RequestURL.ESC_TO_PAGE);
                if (!result.getBool()) nextTask.fail(new Bundle(), new Exception());

                mLastResponseHtml = result.getString();
                Log.d(TAG, mLastResponseHtml);
                nextTask.run(null);
            }
        }).setCallback(new Callback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                accessCallbacks.callback(mLastResponseHtml, true);
            }

            @Override
            public void onFailure(Bundle bundle, Exception e) {
                log(e);
                accessCallbacks.callback(mLastResponseHtml, false);
            }
        }).create().execute(null);
    }

    //**
    //endregion
    //**


    //**
    //region ログイン
    //**

    /***
     * EccStudentCommunication にログインするメソッド
     *
     * @param userId
     * @param password
     * @return
     */
    private static HttpResultClass loginToESC(String userId, String password) {

        HttpResultClass result = HttpBase.httpGet(RequestURL.ESC_TO_PAGE,
                RequestURL.DEFAULT_REFERRER);

        if (!result.getBool()) return result;
        //TODO　遷移チェック

        mLastResponseHtml = result.getString();
        //create requestBody with map
        Map<String, String> body = CreateRequestBody.createPostDataForEscLogin(userId, password, mLastResponseHtml);
        //login
        result = HttpBase.httpPost(RequestURL.ESC_LOGIN, body, RequestURL.ESC_TO_PAGE);
        //TODO　遷移チェック

        return result;
    }

    /***
     * 山口学園学生サービスにログインするメソッド
     *
     * @param userId
     * @param password
     * @return
     */
    private static HttpResultClass loginToYS(String userId, String password) {
        HttpResultClass result = HttpBase.httpGet(RequestURL.YS_TO_PAGE, RequestURL.DEFAULT_REFERRER);
        mLastResponseHtml = result.getString();

        //failure
        if (!result.getBool()) return result;
        //TODO　遷移チェック

        Map<String, String> body = CreateRequestBody.createPostDataForYSLogin(userId, password, mLastResponseHtml);
        result = HttpBase.httpPost(RequestURL.YS_LOGIN, body, RequestURL.YS_TO_PAGE);
        mLastResponseHtml = result.getString();
        //TODO　遷移チェック

        return result;
    }


    //**
    //endregion
    //**


    /***
     * print log
     *
     * @param e
     */
    private static void log(Exception e) {
        Log.d(TAG, e.toString());
    }


    //**
    //region インターフェース
    //**

    public interface AccessCallbacks {
        void callback(String html, boolean bool);
    }

    public interface AccessListCallbacks {
        void callback(String html, List<String> htmls, boolean bool);
    }

    public interface SuccessCallbacks {
        void callback(boolean bool);
    }

    //**
    //endregion
    //**
}
