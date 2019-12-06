package cn.huangchengxi.funnytrip.utils;

import android.content.Context;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.huangchengxi.funnytrip.activity.home.MainActivity;
import cn.huangchengxi.funnytrip.application.MainApplication;
import cn.huangchengxi.funnytrip.databean.ClocksResultBean;
import cn.huangchengxi.funnytrip.databean.FriendBean;
import cn.huangchengxi.funnytrip.databean.FriendRequestResultBean;
import cn.huangchengxi.funnytrip.databean.MomentsBean;
import cn.huangchengxi.funnytrip.databean.NotesResultBean;
import cn.huangchengxi.funnytrip.databean.PortraitUrlBean;
import cn.huangchengxi.funnytrip.databean.RoutesResultBean;
import cn.huangchengxi.funnytrip.databean.SearchResultBean;
import cn.huangchengxi.funnytrip.databean.TipsResultBean;
import cn.huangchengxi.funnytrip.databean.UserInformationBean;
import cn.huangchengxi.funnytrip.databean.UserPropertiesBean;
import cn.huangchengxi.funnytrip.item.ClockItem;
import cn.huangchengxi.funnytrip.item.FriendItem;
import cn.huangchengxi.funnytrip.item.FriendRequestItem;
import cn.huangchengxi.funnytrip.item.FriendSearchResultItem;
import cn.huangchengxi.funnytrip.item.MomentItem;
import cn.huangchengxi.funnytrip.item.NoteItem;
import cn.huangchengxi.funnytrip.item.PositionItem;
import cn.huangchengxi.funnytrip.item.RouteItem;
import cn.huangchengxi.funnytrip.item.TipsItem;
import cn.huangchengxi.funnytrip.utils.sqlite.LocalUsersUpdate;
import cn.huangchengxi.funnytrip.utils.sqlite.SqliteHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocketListener;

public class HttpHelper {
    private HttpHelper(){}
    public static final String SERVER_HOST="http://192.168.43.184:8080/FunnyTripServer";
    public static final String SERVER_SOCKET="ws://192.168.43.184:8080/FunnyTripServer";

    public static void sendOKHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
    public static void sendOKHttpRequestWithData(String host,String json,Callback callback){
        try{
            OkHttpClient client=new OkHttpClient();
            RequestBody body=RequestBody.create(json.getBytes("utf-8"));
            Request request=new Request.Builder().url(host).method("POST",body).build();
            client.newCall(request).enqueue(callback);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void sendOKHttpRequestWithDataAndSession(String host,String data,String session,Callback callback){
        try{
            OkHttpClient client=new OkHttpClient();
            RequestBody body=RequestBody.create(data.getBytes("utf-8"));
            Request request=new Request.Builder().url(host).method("POST",body).addHeader("Cookie","JSESSIONID="+session+"; Path=/FunnyTripServer; HttpOnly").build();
            client.newCall(request).enqueue(callback);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void registerRequest(String email, String password, String validateCode, Callback callback){
        String data="{" +
                "\"email\":\""+email+"\"," +
                "\"password\":\""+password +"\"," +
                "\"validate\":\""+validateCode+"\"" +
                "}";
        sendOKHttpRequestWithData(SERVER_HOST+"/Register",data,callback);
    }
    public static void sendValidateCode(String email,Callback callback){
        String data="{" +
                "\"email\":\""+email+"\"" +
                "}";
        sendOKHttpRequestWithData(SERVER_HOST+"/FetchEmailValidate",data,callback);
    }
    public static void sendLoginRequest(String email,String password,Callback callback) {
        String data = "{" +
                "\"email\":\"" + email + "\"," +
                "\"password\":\"" + password + "\"" +
                "}";
        sendOKHttpRequestWithData(SERVER_HOST + "/Login", data, callback);
    }
    public static void sendChangePassword(String email,String newPassword,String validate,Callback callback){
        String data="{" +
                "\"email\":\""+email+"\"," +
                "\"new_password\":\""+newPassword+"\"," +
                "\"validate\":\""+validate+"\"" +
                "}";
        sendOKHttpRequestWithData(SERVER_HOST+"/ChangePassword",data,callback);
    }
    //uses session
    public static void fetchUserInformation(final String myID, final String fetchID, final Context context, final OnFetchUserInformation onFetchUserInformation){
        String data="{" +
                "\"from\":\""+myID+"\"," +
                "\"fetch\":\""+fetchID+"\"" +
                "}";
        String session=((MainApplication)context.getApplicationContext()).getJSESSIONID();

        sendOKHttpRequestWithDataAndSession(SERVER_HOST + "/FetchUserInfomation", data, session, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (onFetchUserInformation!=null){
                    onFetchUserInformation.onReturnFail();
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try{
                    String jsonStr=response.body().string();
                    Log.e("information",jsonStr);
                    JSONObject json=new JSONObject(jsonStr);
                    String result=json.getString("result");
                    if (result.equals("ok")){
                        String uid=json.getString("uid");
                        String portrait_url=json.getString("portrait_url");
                        String nickname=json.getString("nickname");
                        String mail=json.getString("email");
                        String addr=json.getString("location");
                        int gender_code=json.getInt("gender");
                        long birthday=json.getLong("birthday");
                        String homelandStr=json.getString("homeland");
                        String careerStr=json.getString("career");
                        String interestStr=json.getString("interest");
                        boolean showAddFriend=json.getBoolean("allow_new_friend");
                        boolean showSendMsg=json.getBoolean("accept_message");
                        long registerTime=json.getLong("sign_up_time");
                        UserInformationBean bean=new UserInformationBean(uid,portrait_url,nickname,mail,addr,homelandStr,careerStr,interestStr,gender_code,birthday,registerTime,showAddFriend,showSendMsg);

                        LocalUsersUpdate.InsertOrUpdate(context,uid,nickname,portrait_url);

                        if (onFetchUserInformation!=null){
                            onFetchUserInformation.onReturnSuccess(bean);
                        }
                    }else if (result.equals("not_login")){
                        //restore session
                        restoreLoginSession(context, new OnLoginRestore() {
                            @Override
                            public void onSuccess() {
                                fetchUserInformation(myID,fetchID,context,onFetchUserInformation);
                            }
                            @Override
                            public void onFailure() {
                                if (onFetchUserInformation!=null){
                                    onFetchUserInformation.onReturnFail();
                                }
                            }
                        });
                    }else{
                        if (onFetchUserInformation!=null){
                            onFetchUserInformation.onReturnFail();
                        }
                    }
                }catch (Exception e){
                    Log.e("friendException",e.toString());
                    if (onFetchUserInformation!=null){
                        onFetchUserInformation.onReturnFail();
                    }
                }
            }
        });
    }
    public interface OnFetchUserInformation{
        void onReturnSuccess(UserInformationBean bean);
        void onReturnFail();
    }

    public static void restoreLoginSession(final Context context, final OnLoginRestore onLoginRestore){
        String email=((MainApplication)context.getApplicationContext()).getCurrentAccount();
        String password=((MainApplication)context.getApplicationContext()).getCurrentPassword();

        sendLoginRequest(email, password, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (onLoginRestore!=null){
                    onLoginRestore.onFailure();
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONObject json=new JSONObject(response.body().string());
                    String result=json.getString("result");
                    if (result.equals("error")){
                        if (onLoginRestore!=null){
                            onLoginRestore.onFailure();
                        }
                    }else if (result.equals("ok")){
                        String cookie=response.header("Set-Cookie");
                        Pattern pattern=Pattern.compile("^.*JSESSIONID=(.+);.*$");
                        Matcher matcher=pattern.matcher(cookie);
                        if (matcher.find()){
                            ((MainApplication)context.getApplicationContext()).setJSESSIONID(matcher.group(1));
                        }
                        String uid=json.getString("uid");
                        ((MainApplication)context.getApplicationContext()).setUID(uid);

                        if (onLoginRestore!=null){
                            onLoginRestore.onSuccess();
                        }
                    }else{
                        if (onLoginRestore!=null){
                            onLoginRestore.onFailure();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    if (onLoginRestore!=null){
                        onLoginRestore.onFailure();
                    }
                }
            }
        });
    }
    public interface OnLoginRestore{
        void onSuccess();
        void onFailure();
    }

    public static void fetchMyInformation(final String myID,final Context context, final OnFetchUserInformation onFetchUserInformation){
        String session=((MainApplication)context.getApplicationContext()).getJSESSIONID();

        String data="{" +
                "\"uid\":\""+myID+"\"" +
                "}";
        sendOKHttpRequestWithDataAndSession(SERVER_HOST + "/FetchMyInformation", data, session, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (onFetchUserInformation!=null){
                    onFetchUserInformation.onReturnFail();
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try{
                    String jsonStr=response.body().string();
                    Log.e("information",jsonStr);
                    JSONObject json=new JSONObject(jsonStr);
                    String result=json.getString("result");
                    if (result.equals("ok")){
                        String uid=json.getString("uid");
                        String portrait_url=json.getString("portrait_url");
                        String nickname=json.getString("nickname");
                        String mail=json.getString("email");
                        String addr=json.getString("location");
                        int gender_code=json.getInt("gender");
                        long birthday=json.getLong("birthday");
                        String homelandStr=json.getString("homeland");
                        String careerStr=json.getString("career");
                        String interestStr=json.getString("interest");
                        long registerTime=json.getLong("sign_up_time");
                        UserInformationBean bean=new UserInformationBean(uid,portrait_url,nickname,mail,addr,homelandStr,careerStr,interestStr,gender_code,birthday,registerTime);

                        LocalUsersUpdate.InsertOrUpdate(context,uid,nickname,portrait_url);

                        if (onFetchUserInformation!=null){
                            onFetchUserInformation.onReturnSuccess(bean);
                        }
                    }else if (result.equals("not_login")){
                        //restore session
                        restoreLoginSession(context, new OnLoginRestore() {
                            @Override
                            public void onSuccess() {
                                fetchMyInformation(myID, context, onFetchUserInformation);
                            }
                            @Override
                            public void onFailure() {
                                if (onFetchUserInformation!=null){
                                    onFetchUserInformation.onReturnFail();
                                }
                            }
                        });
                    }else{
                        if (onFetchUserInformation!=null){
                            onFetchUserInformation.onReturnFail();
                        }
                    }
                }catch (Exception e){
                    Log.e("friendException",e.toString());
                    if (onFetchUserInformation!=null){
                        onFetchUserInformation.onReturnFail();
                    }
                }
            }
        });
    }
    public static void changeNickname(final String myID,final String newNickname,final Context context,final OnAfterChangeName onAfterChangeName){
        String session=((MainApplication)context.getApplicationContext()).getJSESSIONID();

        String data="{" +
                "\"uid\":\""+myID+"\"," +
                "\"name\":\""+newNickname+"\"" +
                "}";
        sendOKHttpRequestWithDataAndSession(SERVER_HOST + "/ChangeNickname", data, session, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (onAfterChangeName!=null){
                    onAfterChangeName.onReturnFailure();
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String body=response.body().string();
                    JSONObject json=new JSONObject(body);
                    String result=json.getString("result");
                    if (result.equals("ok")){
                        if (onAfterChangeName!=null){
                            onAfterChangeName.onReturnSuccess();
                        }
                    }else if (result.equals("not_login")){
                        restoreLoginSession(context, new OnLoginRestore() {
                            @Override
                            public void onSuccess() {
                                changeNickname(myID, newNickname, context, onAfterChangeName);
                            }
                            @Override
                            public void onFailure() {
                                if (onAfterChangeName!=null){
                                    onAfterChangeName.onReturnFailure();
                                }
                            }
                        });
                    }
                    else{
                        if (onAfterChangeName!=null){
                            onAfterChangeName.onReturnFailure();
                        }
                    }
                }catch (Exception e){
                    Log.e("change name",e.toString());
                    if (onAfterChangeName!=null){
                        onAfterChangeName.onReturnFailure();
                    }
                }
            }
        });
    }
    public interface OnAfterChangeName{
        void onReturnSuccess();
        void onReturnFailure();
    }
    public static void changeMoreInformation(final String uid, final Context context, final int gender, final long birthday, final String homeland, final String location, final String career, final String interests, final OnAfterChangeMore onAfterChangeMore){
        String session=((MainApplication)context.getApplicationContext()).getJSESSIONID();

        String data="{" +
                "\"uid\":\""+uid+"\"," +
                "\"gender\":"+gender+"," +
                "\"birthday\":"+birthday+"," +
                "\"homeland\":\""+homeland+"\"," +
                "\"location\":\""+location+"\"," +
                "\"career\":\""+career+"\"," +
                "\"interest\":\""+interests+"\"" +
                "}";
        sendOKHttpRequestWithDataAndSession(SERVER_HOST + "/ChangeMoreInformation", data, session, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (onAfterChangeMore!=null){
                    onAfterChangeMore.onReturnFailure();
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String body=response.body().string();
                    JSONObject json=new JSONObject(body);
                    String result=json.getString("result");
                    if (result.equals("ok")){
                        if (onAfterChangeMore!=null){
                            onAfterChangeMore.onReturnSuccess();
                        }
                    }else if (result.equals("not_login")){
                        restoreLoginSession(context, new OnLoginRestore() {
                            @Override
                            public void onSuccess() {
                                changeMoreInformation(uid, context, gender, birthday, homeland, location, career, interests, onAfterChangeMore);
                            }
                            @Override
                            public void onFailure() {
                                if (onAfterChangeMore!=null){
                                    onAfterChangeMore.onReturnFailure();
                                }
                            }
                        });
                    }else{
                        if (onAfterChangeMore!=null){
                            onAfterChangeMore.onReturnFailure();
                        }
                    }
                }catch (Exception e){
                    Log.e("change more",e.toString());
                    if (onAfterChangeMore!=null){
                        onAfterChangeMore.onReturnFailure();
                    }
                }
            }
        });
    }
    public interface OnAfterChangeMore{
        void onReturnFailure();
        void onReturnSuccess();
    }
    public static void uploadPortrait(final String fileName, final Context context, final OnPortraitProcess onPortraitProcess){
        String session=((MainApplication)context.getApplicationContext()).getJSESSIONID();
        File file=new File(fileName);
        RequestBody body=RequestBody.create(MediaType.parse("image/*"),file);
        MultipartBody multipartBody=new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file",file.getName(),body).build();
        Request request=new Request.Builder().url(SERVER_HOST+"/UploadPortrait").addHeader("Cookie","JSESSIONID="+session+"; Path=/FunnyTripServer; HttpOnly").post(multipartBody).build();
        OkHttpClient client=new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (onPortraitProcess!=null){
                    onPortraitProcess.onReturnFailure();
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String body=response.body().string();
                    JSONObject json=new JSONObject(body);
                    String result=json.getString("result");
                    if (result.equals("ok")){
                        String url=json.getString("url");
                        PortraitUrlBean bean=new PortraitUrlBean(url);
                        if (onPortraitProcess!=null){
                            onPortraitProcess.onReturnSuccess(bean);
                        }
                    }else if (result.equals("not_login")){
                        restoreLoginSession(context, new OnLoginRestore() {
                            @Override
                            public void onSuccess() {
                                uploadPortrait(fileName, context, onPortraitProcess);
                            }
                            @Override
                            public void onFailure() {
                                if (onPortraitProcess!=null){
                                    onPortraitProcess.onReturnFailure();
                                }
                            }
                        });
                    }else{
                        if (onPortraitProcess!=null){
                            onPortraitProcess.onReturnFailure();
                        }
                    }
                }catch (Exception e){
                    Log.e("upload portrait",e.toString());
                    if (onPortraitProcess!=null){
                        onPortraitProcess.onReturnFailure();
                    }
                }
            }
        });
    }
    public interface OnPortraitProcess{
        void onReturnSuccess(PortraitUrlBean bean);
        void onReturnFailure();
    }
    public static void updateUserProperties(final String uid, final String property, final boolean value, final Context context, final OnUpdatePropertiesResult onUpdatePropertiesResult){
        String session=((MainApplication)context.getApplicationContext()).getJSESSIONID();
        String data="{" +
                "\"uid\":\""+uid+"\"," +
                "\"property\":\""+property+"\"," +
                "\"value\":"+value +
                "}";
        sendOKHttpRequestWithDataAndSession(SERVER_HOST + "/ChangeUserProperties", data, session, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (onUpdatePropertiesResult!=null){
                    onUpdatePropertiesResult.onReturnFailure();
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String body=response.body().string();
                    JSONObject json=new JSONObject(body);
                    String result=json.getString("result");
                    if (result.equals("ok")){
                        if (onUpdatePropertiesResult!=null){
                            onUpdatePropertiesResult.onReturnSuccess();
                        }
                    }else if (result.equals("not_login")){
                        restoreLoginSession(context, new OnLoginRestore() {
                            @Override
                            public void onSuccess() {
                                updateUserProperties(uid, property, value, context, onUpdatePropertiesResult);
                            }
                            @Override
                            public void onFailure() {
                                if (onUpdatePropertiesResult!=null){
                                    onUpdatePropertiesResult.onReturnFailure();
                                }
                            }
                        });
                    }else{
                        if (onUpdatePropertiesResult!=null){
                            onUpdatePropertiesResult.onReturnFailure();
                        }
                    }
                }catch (Exception e){
                    Log.e("change property",e.toString());
                    if (onUpdatePropertiesResult!=null){
                        onUpdatePropertiesResult.onReturnFailure();
                    }
                }
            }
        });
    }
    public interface OnUpdatePropertiesResult{
        void onReturnSuccess();
        void onReturnFailure();
    }
    public static void fetchUserProperties(final String uid, final Context context, final OnPropertiesResult onPropertiesResult){
        String session=((MainApplication)context.getApplicationContext()).getJSESSIONID();

        String data="{" +
                "\"uid\":\""+uid+"\"" +
                "}";
        sendOKHttpRequestWithDataAndSession(SERVER_HOST + "/FetchUserProperties", data, session, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (onPropertiesResult!=null){
                    onPropertiesResult.onReturnFailure();
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String body=response.body().string();
                    JSONObject pro=new JSONObject(body);
                    String result=pro.getString("result");
                    if (result.equals("ok")){
                        boolean showAddressb=pro.getBoolean("show_address");
                        boolean showCareerb=pro.getBoolean("show_career");
                        boolean showGenderb=pro.getBoolean("show_gender");
                        boolean showHomelandb=pro.getBoolean("show_homeland");
                        boolean showInterestb=pro.getBoolean("show_interest");
                        boolean showBirthdayb=pro.getBoolean("show_birthday");
                        boolean allowViewMomentsb=pro.getBoolean("allow_view_moments");
                        boolean acceptMessageb=pro.getBoolean("accept_message");
                        boolean acceptNewFriendb=pro.getBoolean("accept_new_friend");
                        boolean acceptTeamb=pro.getBoolean("accept_team");
                        boolean allowCommentForUnknownb=pro.getBoolean("allow_comment_unknown");
                        boolean allowViewMomentsForUnknownb=pro.getBoolean("allow_view_moments_for_unknown");

                        UserPropertiesBean bean=new UserPropertiesBean(showAddressb,showCareerb,showGenderb,showHomelandb,showInterestb,showBirthdayb,acceptNewFriendb,acceptMessageb,acceptTeamb,allowViewMomentsb,allowViewMomentsForUnknownb,allowCommentForUnknownb);
                        if (onPropertiesResult!=null){
                            onPropertiesResult.onReturnSuccess(bean);
                        }
                    }else if (result.equals("not_login")){
                        restoreLoginSession(context, new OnLoginRestore() {
                            @Override
                            public void onSuccess() {
                                fetchUserProperties(uid, context, onPropertiesResult);
                            }
                            @Override
                            public void onFailure() {
                                if (onPropertiesResult!=null){
                                    onPropertiesResult.onReturnFailure();
                                }
                            }
                        });
                    }else{
                        if (onPropertiesResult!=null){
                            onPropertiesResult.onReturnFailure();
                        }
                    }
                }catch (Exception e){
                    Log.e("fetch properties",e.toString());
                    if (onPropertiesResult!=null){
                        onPropertiesResult.onReturnFailure();
                    }
                }
            }
        });
    }
    public interface OnPropertiesResult{
        void onReturnSuccess(UserPropertiesBean bean);
        void onReturnFailure();
    }
    public static void fetchSearchResult(final String uid, final String keyword, final Context context, final OnSearchDataBack onSearchDataBack){
        String session=((MainApplication)context.getApplicationContext()).getJSESSIONID();

        String data="{" +
                "\"uid\":\""+uid+"\"," +
                "\"keyword\":\""+keyword+"\"" +
                "}";
        sendOKHttpRequestWithDataAndSession(SERVER_HOST + "/SearchUsers", data, session, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (onSearchDataBack!=null){
                    onSearchDataBack.onReturnFailure();
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String body=response.body().string();
                    JSONObject json=new JSONObject(body);
                    String result=json.getString("result");
                    if (result.equals("ok")){
                        List<FriendSearchResultItem> list=new ArrayList<>();
                        JSONArray array=json.getJSONArray("data");
                        for (int i=0;i<array.length();i++){
                            JSONObject object=array.getJSONObject(i);
                            String uid=object.getString("uid");
                            String name=object.getString("name");
                            String portraitUrl=object.getString("portrait_url");
                            FriendSearchResultItem item=new FriendSearchResultItem(uid,name,portraitUrl);
                            list.add(item);
                        }
                        SearchResultBean bean=new SearchResultBean(list);
                        if (onSearchDataBack!=null){
                            onSearchDataBack.onReturnResultList(bean);
                        }
                    }else if (result.equals("more_specific")){
                        if (onSearchDataBack!=null){
                            onSearchDataBack.onReturnMoreSpecific();
                        }
                    }else if (result.equals("not_login")){
                        restoreLoginSession(context, new OnLoginRestore() {
                            @Override
                            public void onSuccess() {
                                fetchSearchResult(uid, keyword, context, onSearchDataBack);
                            }
                            @Override
                            public void onFailure() {
                                if (onSearchDataBack!=null){
                                    onSearchDataBack.onReturnFailure();
                                }
                            }
                        });
                    }else{
                        if (onSearchDataBack!=null){
                            onSearchDataBack.onReturnFailure();
                        }
                    }
                }catch (Exception e){
                    Log.e("search users",e.toString());
                    if (onSearchDataBack!=null){
                        onSearchDataBack.onReturnFailure();
                    }
                }
            }
        });
    }
    public interface OnSearchDataBack{
        void onReturnMoreSpecific();
        void onReturnFailure();
        void onReturnResultList(SearchResultBean bean);
    }
    public static void fetchMyFriends(final String uid, final Context context, final OnFetchFriendResult onFetchFriendResult){
        String session=((MainApplication)context.getApplicationContext()).getJSESSIONID();
        String data="{" +
                "\"uid\":\""+uid+"\"" +
                "}";
        sendOKHttpRequestWithDataAndSession(SERVER_HOST + "/FetchMyFriends", data, session, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (onFetchFriendResult!=null){
                    onFetchFriendResult.onReturnFailure();
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String body=response.body().string();
                    JSONObject json=new JSONObject(body);
                    String result=json.getString("result");
                    if (result.equals("ok")){
                        List<FriendItem> list=new ArrayList<>();
                        JSONArray array=json.getJSONArray("data");
                        for (int i=0;i<array.length();i++){
                            JSONObject object=array.getJSONObject(i);
                            String uid=object.getString("uid");
                            String name=object.getString("name");
                            String portraitUrl=object.getString("portrait_url");
                            FriendItem item=new FriendItem(uid,name,portraitUrl);
                            list.add(item);
                        }
                        FriendBean bean=new FriendBean(list);
                        if (onFetchFriendResult!=null){
                            onFetchFriendResult.onReturnSuccess(bean);
                        }
                    }else if (result.equals("not_login")){
                        restoreLoginSession(context, new OnLoginRestore() {
                            @Override
                            public void onSuccess() {
                                fetchMyFriends(uid, context, onFetchFriendResult);
                            }
                            @Override
                            public void onFailure() {
                                if (onFetchFriendResult!=null){
                                    onFetchFriendResult.onReturnFailure();
                                }
                            }
                        });
                    }
                    else{
                        if (onFetchFriendResult!=null){
                            onFetchFriendResult.onReturnFailure();
                        }
                    }
                }catch (Exception e){
                    Log.e("my friends",e.toString());
                    if (onFetchFriendResult!=null){
                        onFetchFriendResult.onReturnFailure();
                    }
                }
            }
        });
    }
    public interface OnFetchFriendResult{
        void onReturnFailure();
        void onReturnSuccess(FriendBean bean);
    }
    public static void fetchMoments(final String myUid, final String hisUid, final long timeLimit, final Context context, final OnMomentsResult onMomentsResult){
        String session=((MainApplication)context.getApplicationContext()).getJSESSIONID();

        String data="{" +
                "\"uid\":\""+myUid+"\"," +
                "\"specific_uid\":\""+hisUid+"\"," +
                "\"time_limit\":"+timeLimit +
                "}";
        sendOKHttpRequestWithDataAndSession(SERVER_HOST + "/FetchUsersMoments", data, session, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (onMomentsResult!=null){
                    onMomentsResult.onReturnFailure();
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String body=response.body().string();
                    Log.e("moments body",body);
                    JSONObject json=new JSONObject(body);
                    String result=json.getString("result");
                    if (result.equals("ok")){
                        JSONArray array=json.getJSONArray("data");
                        List<MomentItem> newList=new ArrayList<>();
                        for (int i=0;i<array.length();i++){
                            JSONObject moment=array.getJSONObject(i);
                            String momentId=moment.getString("moment_id");
                            String uid=moment.getString("uid");
                            String name=moment.getString("name");
                            String portraitUrl=moment.getString("portrait_url");
                            String content=moment.getString("content");
                            String picUrl=moment.getString("pic_url");
                            long time=moment.getLong("create_time");
                            int likeCount=moment.getInt("like_count");
                            MomentItem item=new MomentItem(momentId,uid,name,time,content,picUrl,portraitUrl,likeCount);
                            newList.add(item);
                        }
                        MomentsBean bean=new MomentsBean(newList);
                        if (onMomentsResult!=null){
                            onMomentsResult.onReturnSuccess(bean);
                        }
                    }else if (result.equals("not_allow")){
                        restoreLoginSession(context, new OnLoginRestore() {
                            @Override
                            public void onSuccess() {
                                fetchMoments(myUid, hisUid, timeLimit, context, onMomentsResult);
                            }
                            @Override
                            public void onFailure() {
                                if (onMomentsResult!=null){
                                    onMomentsResult.onReturnFailure();
                                }
                            }
                        });
                    }else{
                        if (onMomentsResult!=null){
                            onMomentsResult.onReturnFailure();
                        }
                    }
                }catch (Exception e){
                    Log.e("fetch moments",e.toString());
                    if (onMomentsResult!=null){
                        onMomentsResult.onReturnFailure();
                    }
                }
            }
        });
    }
    public interface OnMomentsResult{
        void onReturnFailure();
        void onReturnSuccess(MomentsBean bean);
    }
    public static void commitShare(final String content, final String imgPath, final Context context, final OnCommitShareResult onCommitShareResult){
        String session=((MainApplication)context.getApplicationContext()).getJSESSIONID();
        if (imgPath!=null){
            File file=new File(imgPath);
            OkHttpClient client=new OkHttpClient.Builder().build();
            RequestBody requestBody=RequestBody.create(MediaType.parse("image/*"),file);
            MultipartBody multipartBody=new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("content",content)
                    .addFormDataPart("file",file.getName(),requestBody)
                    .build();
            Request request=new Request.Builder()
                    .url(SERVER_HOST+"/CommitMoment")
                    .addHeader("Cookie","JSESSIONID="+session+"; Path=/FunnyTripServer; HttpOnly")
                    .post(multipartBody)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    if (onCommitShareResult!=null){
                        onCommitShareResult.onReturnFailure();
                    }
                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try{
                        String body=response.body().string();
                        JSONObject json=new JSONObject(body);
                        String result=json.getString("result");
                        if (result.equals("ok")) {
                            if (onCommitShareResult!=null){
                                onCommitShareResult.onReturnSuccess();
                            }
                        }else if (result.equals("not_login")){
                            restoreLoginSession(context, new OnLoginRestore() {
                                @Override
                                public void onSuccess() {
                                    commitShare(content, imgPath, context, onCommitShareResult);
                                }
                                @Override
                                public void onFailure() {
                                    if (onCommitShareResult!=null){
                                        onCommitShareResult.onReturnFailure();
                                    }
                                }
                            });
                        }else{
                            if (onCommitShareResult!=null){
                                onCommitShareResult.onReturnFailure();
                            }
                        }
                    }catch (Exception e){
                        Log.e("commit exception",e.toString());
                        if (onCommitShareResult!=null){
                            onCommitShareResult.onReturnFailure();
                        }
                    }
                }
            });
        }else{
            OkHttpClient client=new OkHttpClient.Builder().build();
            MultipartBody multipartBody=new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("content",content)
                    .build();
            Request request=new Request.Builder()
                    .url(SERVER_HOST+"/CommitMoment")
                    .addHeader("Cookie","JSESSIONID="+session+"; Path=/FunnyTripServer; HttpOnly")
                    .post(multipartBody)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    if (onCommitShareResult!=null){
                        onCommitShareResult.onReturnFailure();
                    }
                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try{
                        String body=response.body().string();
                        JSONObject json=new JSONObject(body);
                        String result=json.getString("result");
                        if (result.equals("ok")) {
                            if (onCommitShareResult!=null){
                                onCommitShareResult.onReturnSuccess();
                            }
                        }else if (result.equals("not_login")){
                            restoreLoginSession(context, new OnLoginRestore() {
                                @Override
                                public void onSuccess() {
                                    commitShare(content, imgPath, context, onCommitShareResult);
                                }
                                @Override
                                public void onFailure() {
                                    if (onCommitShareResult!=null){
                                        onCommitShareResult.onReturnFailure();
                                    }
                                }
                            });
                        }else{
                            if (onCommitShareResult!=null){
                                onCommitShareResult.onReturnFailure();
                            }
                        }
                    }catch (Exception e){
                        Log.e("commit exception",e.toString());
                        if (onCommitShareResult!=null){
                            onCommitShareResult.onReturnFailure();
                        }
                    }
                }
            });
        }
    }
    public interface OnCommitShareResult{
        void onReturnFailure();
        void onReturnSuccess();
    }
    public static void commitLike(final String uid, final String momentID, final Context context, final OnLikeResult onLikeResult){
        String session=((MainApplication)context.getApplicationContext()).getJSESSIONID();
        String data="{" +
                "\"uid\":\""+uid+"\"," +
                "\"moment_id\":\""+momentID+"\"" +
                "}";
        Log.e("momentId",momentID);
        Log.e("momentId",data);
        sendOKHttpRequestWithDataAndSession(SERVER_HOST + "/CommitLike", data, session, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (onLikeResult!=null){
                    onLikeResult.onReturnFailure();
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String body=response.body().string();
                    JSONObject json=new JSONObject(body);
                    String result=json.getString("result");
                    if (result.equals("ok")){
                        if (onLikeResult!=null){
                            onLikeResult.onReturnSuccess();
                        }
                    }else if (result.equals("exist")){
                        if (onLikeResult!=null){
                            onLikeResult.onReturnAlreadyLike();
                        }
                    }
                    else if (result.equals("not_login")){
                        restoreLoginSession(context, new OnLoginRestore() {
                            @Override
                            public void onSuccess() {
                                commitLike(uid, momentID, context, onLikeResult);
                            }
                            @Override
                            public void onFailure() {
                                if (onLikeResult!=null){
                                    onLikeResult.onReturnFailure();
                                }
                            }
                        });
                    }
                    else{
                        if (onLikeResult!=null){
                            onLikeResult.onReturnFailure();
                        }
                    }
                }catch (Exception e){
                    Log.e("like exception",e.toString());
                    if (onLikeResult!=null){
                        onLikeResult.onReturnFailure();
                    }
                }
            }
        });
    }
    public interface OnLikeResult{
        void onReturnSuccess();
        void onReturnFailure();
        void onReturnAlreadyLike();
    }
    public static void connectToMessagePusher(WebSocketListener listener){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(SERVER_SOCKET+"/MessagePusher").build();
        client.newWebSocket(request,listener);
    }
    public static void fetchFriendRequest(final String myId, final Context context, final OnFetchFriendRequestResult onFetchFriendRequestResult){
        String session=((MainApplication)context.getApplicationContext()).getJSESSIONID();
        String data="{" +
                "\"uid\":\""+myId+"\"" +
                "}";
        sendOKHttpRequestWithDataAndSession(SERVER_HOST + "/FetchFriendRequest", data, session, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (onFetchFriendRequestResult!=null){
                    onFetchFriendRequestResult.onReturnFailure();
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String body=response.body().string();
                    JSONObject json=new JSONObject(body);
                    String result=json.getString("result");
                    if (result.equals("not_login")){
                        restoreLoginSession(context, new OnLoginRestore() {
                            @Override
                            public void onSuccess() {
                                fetchFriendRequest(myId, context, onFetchFriendRequestResult);
                            }
                            @Override
                            public void onFailure() {
                                if (onFetchFriendRequestResult!=null){
                                    onFetchFriendRequestResult.onReturnFailure();
                                }
                            }
                        });
                    }else if (result.equals("ok")){
                        JSONArray array=json.getJSONArray("data");
                        List<FriendRequestItem> list=new ArrayList<>();
                        for (int i=0;i<array.length();i++){
                            JSONObject object=array.getJSONObject(i);
                            String portraitUrl=object.getString("from_portrait");
                            String fromUID=object.getString("from_uid");
                            String fromName=object.getString("from_name");
                            long time=object.getLong("time");
                            String message=object.getString("message");
                            boolean isFriend=object.getBoolean("is_friend");
                            FriendRequestItem item=new FriendRequestItem(fromUID,portraitUrl,fromName,message,time,isFriend);
                            list.add(item);
                        }
                        FriendRequestResultBean bean=new FriendRequestResultBean(list);
                        if (onFetchFriendRequestResult!=null){
                            onFetchFriendRequestResult.onReturnSuccess(bean);
                        }
                    }else{
                        if (onFetchFriendRequestResult!=null){
                            onFetchFriendRequestResult.onReturnFailure();
                        }
                    }
                }catch (Exception e){
                    Log.e(" friend invitation","exception");
                    if (onFetchFriendRequestResult!=null){
                        onFetchFriendRequestResult.onReturnFailure();
                    }
                }
            }
        });
    }
    public interface OnFetchFriendRequestResult{
        void onReturnFailure();
        void onReturnSuccess(FriendRequestResultBean bean);
    }
    public static void fetchMyTips(final String MyId, final long timeLimit, final Context context, final OnTipsResult onTipsResult){
        String session=((MainApplication)context.getApplicationContext()).getJSESSIONID();

        String data="{" +
                "\"uid\":\""+MyId+"\"," +
                "\"time_limit\":"+timeLimit +
                "}";
        sendOKHttpRequestWithDataAndSession(SERVER_HOST + "/FetchMyTips", data, session, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (onTipsResult!=null){
                    onTipsResult.onReturnFailure();
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String body=response.body().string();
                    JSONObject json=new JSONObject(body);
                    Log.e("tips",body);
                    String result=json.getString("result");
                    if (result.equals("ok")){
                        JSONArray array=json.getJSONArray("data");
                        List<TipsItem> newList=new ArrayList<>();
                        for (int i=0;i<array.length();i++){
                            JSONObject obj=array.getJSONObject(i);
                            String url=obj.getString("url");
                            String name=obj.getString("tip_name");
                            String tipId=obj.getString("tip_id");
                            long time=obj.getLong("create_time");
                            TipsItem item=new TipsItem(tipId,name,url,time);
                            newList.add(item);
                        }
                        TipsResultBean bean=new TipsResultBean(newList);
                        if (onTipsResult!=null){
                            onTipsResult.onReturnSuccess(bean);
                        }
                    }else if (result.equals("not_login")){
                        restoreLoginSession(context, new OnLoginRestore() {
                            @Override
                            public void onSuccess() {
                                fetchMyTips(MyId, timeLimit, context, onTipsResult);
                            }
                            @Override
                            public void onFailure() {
                                if (onTipsResult!=null){
                                    onTipsResult.onReturnFailure();
                                }
                            }
                        });
                    }
                    else{
                        if (onTipsResult!=null){
                            onTipsResult.onReturnFailure();
                        }
                    }
                }catch (Exception e){
                    Log.e("tips exception",e.toString());
                    if (onTipsResult!=null){
                        onTipsResult.onReturnFailure();
                    }
                }
            }
        });
    }
    public interface OnTipsResult{
        void onReturnFailure();
        void onReturnSuccess(TipsResultBean bean);
    }
    public static void commitTips(final String uid, final String url, final String name, final Context context, final OnCommonResult onCommonResult){
        String session=((MainApplication)context.getApplicationContext()).getJSESSIONID();

        String data="{" +
                "\"uid\":\""+uid+"\"," +
                "\"url\":\""+url+"\"," +
                "\"tip_name\":\""+name+"\"" +
                "}";
        sendOKHttpRequestWithDataAndSession(SERVER_HOST + "/CommitTip", data, session, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (onCommonResult!=null){
                    onCommonResult.onReturnFailure();
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String body=response.body().string();
                    JSONObject json=new JSONObject(body);
                    Log.e("returnStar",body);
                    String result=json.getString("result");
                    if (result.equals("ok")){
                        if (onCommonResult!=null){
                            onCommonResult.onReturnSuccess();
                        }
                    }else if (result.equals("not_login")){
                        restoreLoginSession(context, new OnLoginRestore() {
                            @Override
                            public void onSuccess() {
                                commitTips(uid, url, name, context, onCommonResult);
                            }
                            @Override
                            public void onFailure() {
                                if (onCommonResult!=null){
                                    onCommonResult.onReturnFailure();
                                }
                            }
                        });
                    }else{
                        if (onCommonResult!=null){
                            onCommonResult.onReturnFailure();
                        }
                    }
                }catch (Exception e){
                    Log.e("star tip exception",e.toString());
                    if (onCommonResult!=null){
                        onCommonResult.onReturnFailure();
                    }
                }
            }
        });
    }
    public interface OnCommonResult{
        void onReturnFailure();
        void onReturnSuccess();
    }
    public static void deleteTip(final String tipID, final String uid, final Context context, final OnCommonResult onCommonResult){
        String session=((MainApplication)context.getApplicationContext()).getJSESSIONID();

        String data="{" +
                "\"uid\":\""+uid+"\"," +
                "\"tip_id\":"+tipID+"" +
                "}";
        sendOKHttpRequestWithDataAndSession(SERVER_HOST + "/DeleteTips", data, session, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (onCommonResult!=null){
                    onCommonResult.onReturnFailure();
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String body=response.body().string();
                    JSONObject json=new JSONObject(body);
                    String result=json.getString("result");
                    if (result.equals("ok")){
                        if (onCommonResult!=null){
                            onCommonResult.onReturnSuccess();
                        }
                    }else if (result.equals("not_login")){
                        restoreLoginSession(context, new OnLoginRestore() {
                            @Override
                            public void onSuccess() {
                                deleteTip(tipID, uid, context, onCommonResult);
                            }
                            @Override
                            public void onFailure() {
                                if (onCommonResult!=null){
                                    onCommonResult.onReturnFailure();
                                }
                            }
                        });
                    }else{
                        if (onCommonResult!=null){
                            onCommonResult.onReturnFailure();
                        }
                    }
                }catch (Exception e){
                    if (onCommonResult!=null){
                        onCommonResult.onReturnFailure();
                    }
                }
            }
        });
    }
    public static void commitClock(final String uid, final String location, final long time, final double latitude, final double longitude, final Context context, final OnCommonResult onCommonResult){
        String session=((MainApplication)context.getApplicationContext()).getJSESSIONID();

        String data="{" +
                "\"uid\":\""+uid+"\"," +
                "\"clock_time\":"+time+"," +
                "\"location\":\""+location+"\"," +
                "\"latitude\":"+latitude+"," +
                "\"longitude\":"+longitude +
                "}";
        sendOKHttpRequestWithDataAndSession(SERVER_HOST + "/CommitClock", data, session, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (onCommonResult!=null){
                    onCommonResult.onReturnFailure();
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String body=response.body().string();
                    JSONObject json=new JSONObject(body);
                    String result=json.getString("result");
                    if (result.equals("ok")){
                        if (onCommonResult!=null){
                            onCommonResult.onReturnSuccess();
                        }
                    }else if (result.equals("not_login")){
                       restoreLoginSession(context, new OnLoginRestore() {
                           @Override
                           public void onSuccess() {
                               commitClock(uid, location, time, latitude, longitude, context, onCommonResult);
                           }
                           @Override
                           public void onFailure() {
                               if (onCommonResult!=null){
                                   onCommonResult.onReturnFailure();
                               }
                           }
                       });
                    }else{
                        if (onCommonResult!=null){
                            onCommonResult.onReturnFailure();
                        }
                    }
                }catch (Exception e){
                    if (onCommonResult!=null){
                        onCommonResult.onReturnFailure();
                    }
                }
            }
        });
    }
    public static void fetchClocks(final String uid, final long timeLimit, final Context context, final OnClocksResult onClocksResult){
        String session=((MainApplication)context.getApplicationContext()).getJSESSIONID();

        String data="{" +
                "\"uid\":\""+uid+"\"," +
                "\"time_limit\":"+timeLimit +
                "}";
        sendOKHttpRequestWithDataAndSession(SERVER_HOST + "/FetchClocks", data, session, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (onClocksResult!=null){
                    onClocksResult.onReturnFailure();
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try{
                    JSONObject json=new JSONObject(response.body().string());
                    String result=json.getString("result");
                    if (result.equals("ok")){
                        JSONArray array=json.getJSONArray("data");
                        List<ClockItem> list=new ArrayList<>();
                        for (int i=0;i<array.length();i++){
                            JSONObject obj=array.getJSONObject(i);
                            String clockID=obj.getString("clock_id");
                            String location=obj.getString("location");
                            double latitude=obj.getDouble("latitude");
                            double longitude=obj.getDouble("longitude");
                            long time=obj.getLong("clock_time");
                            ClockItem item=new ClockItem(clockID,location,time,latitude,longitude);
                            list.add(item);
                        }
                        ClocksResultBean bean=new ClocksResultBean(list);
                        if (onClocksResult!=null){
                            onClocksResult.onReturnSuccess(bean);
                        }
                    }else if (result.equals("not_login")){
                        restoreLoginSession(context, new OnLoginRestore() {
                            @Override
                            public void onSuccess() {
                                fetchClocks(uid, timeLimit, context, onClocksResult);
                            }

                            @Override
                            public void onFailure() {
                                if (onClocksResult!=null){
                                    onClocksResult.onReturnFailure();
                                }
                            }
                        });
                    }else{
                        if (onClocksResult!=null){
                            onClocksResult.onReturnFailure();
                        }
                    }
                }catch (Exception e){
                    Log.e("fetch clocks",e.toString());
                    if (onClocksResult!=null){
                        onClocksResult.onReturnFailure();
                    }
                }
            }
        });
    }
    public interface OnClocksResult{
        void onReturnFailure();
        void onReturnSuccess(ClocksResultBean bean);
    }
    public static void deleteClock(final String uid, final String id, final Context context, final OnCommonResult onCommonResult){
        String session=((MainApplication)context.getApplicationContext()).getJSESSIONID();
        String data="{" +
                "\"uid\":\""+uid+"\"," +
                "\"clock_id\":\""+id+"\"" +
                "}";
        sendOKHttpRequestWithDataAndSession(SERVER_HOST + "/DeleteClocks", data, session, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (onCommonResult!=null){
                    onCommonResult.onReturnFailure();
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try{
                    JSONObject json=new JSONObject(response.body().string());
                    String result=json.getString("result");
                    if (result.equals("ok")){
                        if (onCommonResult!=null){
                            onCommonResult.onReturnSuccess();
                        }
                    }else if (result.equals("not_login")){
                        restoreLoginSession(context, new OnLoginRestore() {
                            @Override
                            public void onSuccess() {
                                deleteClock(uid, id, context, onCommonResult);
                            }
                            @Override
                            public void onFailure() {
                                if (onCommonResult!=null){
                                    onCommonResult.onReturnFailure();
                                }
                            }
                        });
                    }else{
                        if (onCommonResult!=null){
                            onCommonResult.onReturnFailure();
                        }
                    }
                }catch (Exception e){
                    Log.e("delete clock",e.toString());
                    if (onCommonResult!=null){
                        onCommonResult.onReturnFailure();
                    }
                }
            }
        });
    }
    public static void commitNote(final String content, final Context context, final OnCommonResult onCommonResult){
        String uid=((MainApplication)context.getApplicationContext()).getUID();
        String session=((MainApplication)context.getApplicationContext()).getJSESSIONID();
        String data="{" +
                "\"uid\":\""+uid+"\"," +
                "\"content\":\""+content+"\"" +
                "}";
        sendOKHttpRequestWithDataAndSession(SERVER_HOST + "/CommitNotes", data, session, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (onCommonResult!=null){
                    onCommonResult.onReturnFailure();
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try{
                    JSONObject json=new JSONObject(response.body().string());
                    String result=json.getString("result");
                    if (result.equals("ok")){
                        if (onCommonResult!=null){
                            onCommonResult.onReturnSuccess();
                        }
                    }else if (result.equals("not_login")){
                        restoreLoginSession(context, new OnLoginRestore() {
                            @Override
                            public void onSuccess() {
                                commitNote(content, context, onCommonResult);
                            }
                            @Override
                            public void onFailure() {
                                if (onCommonResult!=null){
                                    onCommonResult.onReturnFailure();
                                }
                            }
                        });
                    }
                }catch (Exception e){
                    if (onCommonResult!=null){
                        onCommonResult.onReturnFailure();
                    }
                }
            }
        });
    }
    public static void deleteNote(final String noteID, final Context context, final OnCommonResult onCommonResult){
        String uid=((MainApplication)context.getApplicationContext()).getUID();
        String session=((MainApplication)context.getApplicationContext()).getJSESSIONID();
        String data="{" +
                "\"uid\":\""+uid+"\"," +
                "\"note_id\":\""+noteID+"\"" +
                "}";
        sendOKHttpRequestWithDataAndSession(SERVER_HOST + "/DeleteNotes", data, session, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (onCommonResult!=null){
                    onCommonResult.onReturnFailure();
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try{
                    JSONObject json=new JSONObject(response.body().string());
                    String result=json.getString("result");
                    if (result.equals("ok")){
                        if (onCommonResult!=null){
                            onCommonResult.onReturnSuccess();
                        }
                    }else if (result.equals("not_login")){
                        restoreLoginSession(context, new OnLoginRestore() {
                            @Override
                            public void onSuccess() {
                                deleteNote(noteID, context, onCommonResult);
                            }
                            @Override
                            public void onFailure() {
                                if (onCommonResult!=null){
                                    onCommonResult.onReturnFailure();
                                }
                            }
                        });
                    }
                }catch (Exception e){
                    if (onCommonResult!=null){
                        onCommonResult.onReturnFailure();
                    }
                }
            }
        });
    }
    public static void fetchNotes(final long timeLimit, final Context context, final OnNotesResult onNotesResult){
        String uid=((MainApplication)context.getApplicationContext()).getUID();
        String session=((MainApplication)context.getApplicationContext()).getJSESSIONID();
        String data="{" +
                "\"uid\":\""+uid+"\"," +
                "\"time_limit\":"+timeLimit +
                "}";
        sendOKHttpRequestWithDataAndSession(SERVER_HOST + "/FetchNotes", data, session, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (onNotesResult!=null){
                    onNotesResult.onReturnFailure();
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try{
                    JSONObject json=new JSONObject(response.body().string());
                    String result=json.getString("result");
                    if (result.equals("ok")){
                        JSONArray array=json.getJSONArray("data");
                        List<NoteItem> list=new ArrayList<>();
                        for (int i=0;i<array.length();i++){
                            JSONObject object=array.getJSONObject(i);
                            String noteID=object.getString("note_id");
                            String content=object.getString("content");
                            long time=object.getLong("create_time");
                            NoteItem item=new NoteItem(noteID,time,content);
                            list.add(item);
                        }
                        NotesResultBean bean=new NotesResultBean(list);
                        if (onNotesResult!=null){
                            onNotesResult.onReturnSuccess(bean);
                        }
                    }else if (result.equals("not_login")){
                        restoreLoginSession(context, new OnLoginRestore() {
                            @Override
                            public void onSuccess() {
                                fetchNotes(timeLimit, context, onNotesResult);
                            }

                            @Override
                            public void onFailure() {
                                if (onNotesResult!=null){
                                    onNotesResult.onReturnFailure();
                                }
                            }
                        });
                    }else{
                        if (onNotesResult!=null){
                            onNotesResult.onReturnFailure();
                        }
                    }
                }catch (Exception e){
                    Log.e("note_exception",e.toString());
                    if (onNotesResult!=null){
                        onNotesResult.onReturnFailure();
                    }
                }
            }
        });
    }
    public interface OnNotesResult{
        void onReturnFailure();
        void onReturnSuccess(NotesResultBean bean);
    }
    public static void updateNote(final String content,final String id, final Context context, final OnCommonResult onCommonResult){
        String uid=((MainApplication)context.getApplicationContext()).getUID();
        String session=((MainApplication)context.getApplicationContext()).getJSESSIONID();
        String data="{" +
                "\"uid\":\""+uid+"\"," +
                "\"note_id\":\""+id+"\"," +
                "\"content\":\""+content+"\"" +
                "}";
        sendOKHttpRequestWithDataAndSession(SERVER_HOST + "/UpdateNote", data, session, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (onCommonResult!=null){
                    onCommonResult.onReturnFailure();
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try{
                    JSONObject json=new JSONObject(response.body().string());
                    String result=json.getString("result");
                    if (result.equals("ok")){
                        if (onCommonResult!=null){
                            onCommonResult.onReturnSuccess();
                        }
                    }else if (result.equals("not_login")){
                        restoreLoginSession(context, new OnLoginRestore() {
                            @Override
                            public void onSuccess() {
                                updateNote(content, id, context, onCommonResult);
                            }
                            @Override
                            public void onFailure() {
                                if (onCommonResult!=null){
                                    onCommonResult.onReturnFailure();
                                }
                            }
                        });
                    }
                }catch (Exception e){
                    if (onCommonResult!=null){
                        onCommonResult.onReturnFailure();
                    }
                }
            }
        });
    }
    public static void commitRoute(final String name, final List<PositionItem> positions,final String routeID, final Context context, final OnCommonResult onCommonResult){
        String uid=((MainApplication)context.getApplicationContext()).getUID();
        String session=((MainApplication)context.getApplicationContext()).getJSESSIONID();
        String array="[";
        for (int i=0;i<positions.size();i++){
            PositionItem item=positions.get(i);

            String position="{" +
                    "\"latitude\":"+item.getLatitude()+"," +
                    "\"longitude\":"+item.getLongitude()+"," +
                    "\"place_name\":\""+item.getName()+"\"," +
                    "\"pos_index\":"+i +
                    "}";
            array+=position+(i==positions.size()-1?"]":",");
        }
        String data="{" +
                "\"uid\":\""+uid+"\"," +
                "\"name\":\""+name+"\"," +
                "\"route_id\":\""+routeID+"\"," +
                "\"positions\":"+array +
                "}";
        sendOKHttpRequestWithDataAndSession(SERVER_HOST + "/CommitRoute", data, session, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (onCommonResult!=null){
                    onCommonResult.onReturnFailure();
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try{
                    JSONObject json=new JSONObject(response.body().string());
                    String result=json.getString("result");
                    if (result.equals("ok")){
                        if (onCommonResult!=null){
                            onCommonResult.onReturnSuccess();
                        }
                    }else if (result.equals("not_login")){
                        restoreLoginSession(context, new OnLoginRestore() {
                            @Override
                            public void onSuccess() {
                                commitRoute(name, positions, routeID, context, onCommonResult);
                            }
                            @Override
                            public void onFailure() {
                                if (onCommonResult!=null){
                                    onCommonResult.onReturnFailure();
                                }
                            }
                        });
                    }else{
                        if (onCommonResult!=null){
                            onCommonResult.onReturnFailure();
                        }
                    }
                }catch (Exception e){
                    if (onCommonResult!=null){
                        onCommonResult.onReturnFailure();
                    }
                }
            }
        });
    }
    public static void fetchRoutes(final long timeLimit, final Context context, final OnRoutesResult onRoutesResult){
        String uid=((MainApplication)context.getApplicationContext()).getUID();
        String session=((MainApplication)context.getApplicationContext()).getJSESSIONID();
        String data="{" +
                "\"uid\":\""+uid+"\"," +
                "\"time_limit\":"+timeLimit +
                "}";
        sendOKHttpRequestWithDataAndSession(SERVER_HOST + "/FetchRoutes", data, session, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (onRoutesResult!=null){
                    onRoutesResult.onReturnFailure();
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONObject json=new JSONObject(response.body().string());
                    String result=json.getString("result");
                    if (result.equals("ok")){
                        List<RouteItem> routeItems=new ArrayList<>();
                        JSONArray datas=json.getJSONArray("data");
                        for (int i=0;i<datas.length();i++){
                            JSONObject route=datas.getJSONObject(i);
                            String routeID=route.getString("route_id");
                            String routeName=route.getString("route_name");
                            long time=route.getLong("time");
                            JSONArray positions=route.getJSONArray("data");
                            ArrayList<PositionItem> positionItems=new ArrayList<>();
                            for (int j=0;j<positions.length();j++){
                                JSONObject position=positions.getJSONObject(j);
                                double latitude=position.getDouble("latitude");
                                double longitude=position.getDouble("longitude");
                                String placeName=position.getString("place_name");
                                int index=position.getInt("pos_index");
                                PositionItem positionItem=new PositionItem(index+"",placeName,latitude,longitude);
                                positionItems.add(positionItem);
                            }
                            RouteItem item=new RouteItem(positionItems,routeID,routeName,time);
                            routeItems.add(item);
                        }
                        if (onRoutesResult!=null){
                            RoutesResultBean bean=new RoutesResultBean(routeItems);
                            onRoutesResult.onReturnSuccess(bean);
                        }
                    }else if (result.equals("not_login")){
                        restoreLoginSession(context, new OnLoginRestore() {
                            @Override
                            public void onSuccess() {
                                fetchRoutes(timeLimit, context, onRoutesResult);
                            }
                            @Override
                            public void onFailure() {
                                if (onRoutesResult!=null){
                                    onRoutesResult.onReturnFailure();
                                }
                            }
                        });
                    }else{
                        if (onRoutesResult!=null){
                            onRoutesResult.onReturnFailure();
                        }
                    }
                }catch (Exception e){
                    if (onRoutesResult!=null){
                        onRoutesResult.onReturnFailure();
                    }
                }
            }
        });
    }
    public interface OnRoutesResult{
        void onReturnFailure();
        void onReturnSuccess(RoutesResultBean bean);
    }
    public static void deleteRoute(final String routeID, final Context context, final OnCommonResult onCommonResult){
        String uid=((MainApplication)context.getApplicationContext()).getUID();
        String session=((MainApplication)context.getApplicationContext()).getJSESSIONID();
        String data="{" +
                "\"uid\":\""+uid+"\"," +
                "\"route_id\":\""+routeID+"\"" +
                "}";
        sendOKHttpRequestWithDataAndSession(SERVER_HOST + "/DeleteRoutes", data, session, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (onCommonResult!=null){
                    onCommonResult.onReturnFailure();
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try{
                    JSONObject json=new JSONObject(response.body().string());
                    String result=json.getString("result");
                    if (result.equals("ok")) {
                        if (onCommonResult!=null){
                            onCommonResult.onReturnSuccess();
                        }
                    }else if (result.equals("not_login")){
                        restoreLoginSession(context, new OnLoginRestore() {
                            @Override
                            public void onSuccess() {
                                deleteRoute(routeID, context, onCommonResult);
                            }

                            @Override
                            public void onFailure() {
                                if (onCommonResult!=null){
                                    onCommonResult.onReturnFailure();
                                }
                            }
                        });
                    }else{
                        if (onCommonResult!=null){
                            onCommonResult.onReturnFailure();
                        }
                    }
                }catch (Exception e){
                    if (onCommonResult!=null){
                        onCommonResult.onReturnFailure();
                    }
                }
            }
        });
    }
}
