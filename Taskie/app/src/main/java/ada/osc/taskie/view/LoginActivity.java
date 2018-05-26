package ada.osc.taskie.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import ada.osc.taskie.R;
import ada.osc.taskie.model.LoginResponse;
import ada.osc.taskie.model.RegistrationToken;
import ada.osc.taskie.networking.ApiService;
import ada.osc.taskie.networking.RetrofitUtil;
import ada.osc.taskie.util.NetworkUtil;
import ada.osc.taskie.util.SharedPrefsUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.user_email)
    EditText mUserEmail;
    @BindView(R.id.user_password)
    EditText mUserPwd;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        ButterKnife.bind(this);

        checkIsLoged();
    }

    @OnClick(R.id.button_login)
    void onLoginButtonClick(){
        loginUser();
    }


    private void loginUser() {
        if(!NetworkUtil.isOnline(getApplicationContext())){
            toastMessage("Not connected");
            return;
        }
        Retrofit retrofit = RetrofitUtil.createRetrofit(getApplicationContext());

        ApiService apiService = retrofit.create(ApiService.class);

        RegistrationToken registrationToken = new RegistrationToken();
        registrationToken.mEmail = mUserEmail.getText().toString();
        registrationToken.mPassword = mUserPwd.getText().toString();

        final Call<LoginResponse> loginCall = apiService.loginUser(registrationToken);
        loginCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    SharedPrefsUtil.storePreferencesField(LoginActivity.this, SharedPrefsUtil.TOKEN, loginResponse.mToken);
                    startNotesActivity();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

            }
        });
    }

    private void startNotesActivity() {
        Intent intent = new Intent();
        intent.setClass(this, TasksActivity.class);
        startActivity(intent);
    }

    private void checkIsLoged() {
        //may not work if token has expiration time
        if(!SharedPrefsUtil.getPreferencesField(this,
                SharedPrefsUtil.TOKEN).isEmpty()) {
            startNotesActivity();
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
