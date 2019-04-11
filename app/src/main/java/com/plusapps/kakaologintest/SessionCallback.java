package com.plusapps.kakaologintest;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

public class SessionCallback implements ISessionCallback {


    private final Context mContext;

    public SessionCallback(Context context) {
        mContext = context;
    }

    // 로그인에 성공한 상태

    @Override

    public void onSessionOpened() {

        Log.d("kakaobanana", "onSessionOpened");
        requestMe();

    }



    // 로그인에 실패한 상태

    @Override

    public void onSessionOpenFailed(KakaoException exception) {

        Log.e("SessionCallback :: ", "onSessionOpenFailed : " + exception.getMessage());

    }



    // 사용자 정보 요청

    public void requestMe() {

        // 사용자정보 요청 결과에 대한 Callback

        UserManagement.requestMe(new MeResponseCallback() {

            // 세션 오픈 실패. 세션이 삭제된 경우,

            @Override

            public void onSessionClosed(ErrorResult errorResult) {

                Log.e("SessionCallback :: ", "onSessionClosed : " + errorResult.getErrorMessage());
                ((Activity)mContext).finish();

            }



            // 회원이 아닌 경우,

            @Override

            public void onNotSignedUp() {

                Log.e("SessionCallback :: ", "onNotSignedUp");
                ((Activity)mContext).finish();

            }



            // 사용자정보 요청에 성공한 경우,

            @Override

            public void onSuccess(UserProfile userProfile) {



                Log.e("SessionCallback :: ", "onSuccess");



                String nickname = userProfile.getNickname();

                String email = userProfile.getEmail();

                String profileImagePath = userProfile.getProfileImagePath();

                String thumnailPath = userProfile.getThumbnailImagePath();

                String UUID = userProfile.getUUID();

                long id = userProfile.getId();



                Log.e("Profile : ", nickname + "");

                Log.e("Profile : ", email + "");

                Log.e("Profile : ", profileImagePath  + "");

                Log.e("Profile : ", thumnailPath + "");

                Log.e("Profile : ", UUID + "");

                Log.e("Profile : ", id + "");


                if (email == null || email.isEmpty()) {

                    Toast.makeText(mContext, "'[선택] 카카오 계정(이메일)'에 체크해주세요", Toast.LENGTH_SHORT).show();

                    reconnectKakaoAccount();
                }





            }



            // 사용자 정보 요청 실패

            @Override

            public void onFailure(ErrorResult errorResult) {

                Log.e("SessionCallback :: ", "onFailure : " + errorResult.getErrorMessage());
                ((Activity)mContext).finish();

            }

        });

    }

    private void reconnectKakaoAccount() {
        // requestUnlink를 해야 다음 카카오 로그인시 "개인정보 동의화면"이 뜸
        UserManagement.requestUnlink(new UnLinkResponseCallback() {

            @Override
            public void onSuccess(Long result) {
                Log.e("kakaobanana", "requestUnlink onSuccess");
                signupKakaoAccount();

            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.e("kakaobanana", "requestUnlink onSessionClosed");
                ((Activity)mContext).finish();
            }

            @Override
            public void onNotSignedUp() {
                Log.e("kakaobanana", "requestUnlink onNotSignedUp");
                ((Activity)mContext).finish();
            }

        });


    }



    private void signupKakaoAccount() {
        // requestUnlink를 해야 다음 카카오 로그인시 "개인정보 동의화면"이 뜸
        //session이 closed됨
        //unlink하면 자동으로 그렇게 되나?
//            UserManagement.requestSignup(new SignupResponseCallback() {
//
//                @Override
//                public void onSuccess(Long result) {
//                    Log.e("kakaobanana", "signupKakaoAccount onSuccess");
//                    signupKakaoAccount();
//
//                }
//
//                @Override
//                public void onSessionClosed(ErrorResult errorResult) {
//                    Log.e("kakaobanana", "signupKakaoAccount onSessionClosed " + errorResult.getErrorMessage());
//                    finish();
//                }
//
//                @Override
//                public void onNotSignedUp() {
//                    Log.e("kakaobanana", "signupKakaoAccount onNotSignedUp");
//                    finish();
//                }
//
//            }, null);


        // 이건 제대로 작동
        Session session = Session.getCurrentSession();

        Session.getCurrentSession().removeCallback(this);

        session.addCallback(new SessionCallback(mContext));

        session.open(AuthType.KAKAO_LOGIN_ALL, (Activity)mContext);

    }

}

