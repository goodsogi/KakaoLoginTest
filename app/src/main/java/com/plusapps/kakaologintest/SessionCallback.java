package com.plusapps.kakaologintest;

import android.app.Activity;
import android.content.Context;

import com.kakao.auth.AccessTokenCallback;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.auth.authorization.accesstoken.AccessToken;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.exception.KakaoException;

import java.util.ArrayList;
import java.util.List;

public class SessionCallback implements ISessionCallback {


    private final Context mContext;

    public SessionCallback(Context context) {
        mContext = context;
    }

    // 로그인에 성공한 상태

    @Override

    public void onSessionOpened() {

        JeffLogger.kakaoLog( "onSessionOpened");
        requestMe();

    }


    // 로그인에 실패한 상태

    @Override

    public void onSessionOpenFailed(KakaoException exception) {

        JeffLogger.kakaoLog( "onSessionOpenFailed : " + exception.getMessage());

    }


    // 사용자 정보 요청

    public void requestMe() {

        List<String> keys = new ArrayList<>();
        keys.add("properties.nickname");
        keys.add("properties.profile_image");
        keys.add("kakao_account.email");

        // 사용자정보 요청 결과에 대한 Callback

        UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                JeffLogger.kakaoLog(message);
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                JeffLogger.kakaoLog("onSessionClosed : " + errorResult.getErrorMessage());
                ((Activity) mContext).finish();
            }

            @Override
            public void onSuccess(MeV2Response response) {
                JeffLogger.kakaoLog("user id : " + response.getId());
                JeffLogger.kakaoLog("email: " + response.getKakaoAccount().getEmail());
                JeffLogger.kakaoLog("profile image: " + response.getProfileImagePath());
                JeffLogger.kakaoLog("thumbnail image: " + response.getThumbnailImagePath());

                String email = response.getKakaoAccount().getEmail();



                if (email == null || email.isEmpty()) {

                    requestUserEmailAgreement(response.getKakaoAccount());

                } else {
                    unlinkKakaoAccount();

                    saveUserProfileOnServer(response);

                }

            }


        });



    }

    private void saveUserProfileOnServer(MeV2Response meV2Response) {



    }

    private void startMainActivity() {


    }



    private void unlinkKakaoAccount() {
        // requestUnlink를 해야 다음 카카오 로그인시 "개인정보 동의화면"이 뜸
        UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {

            @Override
            public void onSuccess(Long result) {
                JeffLogger.kakaoLog( "unlinkKakaoAccount onSuccess");


            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                JeffLogger.kakaoLog( "unlinkKakaoAccount onSessionClosed");
                ((Activity) mContext).finish();
            }

            @Override
            public void onNotSignedUp() {
                JeffLogger.kakaoLog( "unlinkKakaoAccount onNotSignedUp");
                ((Activity) mContext).finish();
            }

        });


    }

    private void requestUserEmailAgreement(UserAccount account) {
        List<String> neededScopes = new ArrayList<>();
        if (account.emailNeedsAgreement().getBoolean()) {
            neededScopes.add("account_email");
        }

        Session.getCurrentSession().updateScopes((Activity) mContext, neededScopes, new
                AccessTokenCallback() {
                    @Override
                    public void onAccessTokenReceived(AccessToken accessToken) {
                        // 유저에게 성공적으로 동의를 받음. 토큰을 재발급 받게 됨.
                        //이 토큰을 Session 등에 지정할 필요는 없는 듯
                        //access token으로 서버통신시 사용해서 필요한 데이터를 가져올 수 있는 듯
                        //refresh token으로 access token의 유효기간이 만료되기 전 새로 유효한 access token을 발급받는 듯
                        JeffLogger.kakaoLog( "requestUserEmailAgreement onAccessTokenReceived: " + accessToken);
                        requestMe();

                    }

                    @Override
                    public void onAccessTokenFailure(ErrorResult errorResult) {
                        // 동의 얻기 실패
                        JeffLogger.kakaoLog( "requestUserEmailAgreement onAccessTokenFailure: " + errorResult.getErrorMessage());
                        ((Activity) mContext).finish();
                    }
                });
    }


}

