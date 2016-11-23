package uk.co.placona.rxlogin;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import uk.co.placona.rxlogin.api.ApiClient;
import uk.co.placona.rxlogin.api.ApiService;
import uk.co.placona.rxlogin.models.LoginRequest;
import uk.co.placona.rxlogin.models.User;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.login_et)
    EditText mLogin;

    @BindView(R.id.password_et)
    EditText mPassword;

    @BindView(R.id.login_bt)
    Button mLoginButton;

    @BindDrawable(android.R.drawable.presence_busy)
    Drawable mInvalidField;

    @BindDrawable(android.R.drawable.presence_online)
    Drawable mValidField;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ApiService service = ApiClient.getRetrofitInstance().create(ApiService.class);

        ButterKnife.bind(this);

        Observable<CharSequence> loginObservable = RxTextView.textChanges(mLogin);
        loginObservable
                .map(this::isValidLogin)
                .subscribe(isValid -> mLogin.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null, (isValid ? mValidField : mInvalidField), null));

        Observable<CharSequence> passwordObservable = RxTextView.textChanges(mPassword);
        passwordObservable
                .map(this::isValidPassword)
                .subscribe(isValid -> mPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null, (isValid ? mValidField : mInvalidField), null));

        Observable<Boolean> combinedObservables = Observable.combineLatest(loginObservable, passwordObservable, (o1, o2) -> isValidLogin(o1) && isValidPassword(o2));
        combinedObservables.subscribe(isVisible -> mLoginButton.setVisibility(isVisible ? View.VISIBLE : View.GONE));

        // Listen to button clicks
        RxView.clicks(mLoginButton).subscribe(aVoid -> {
            mLoginButton.setEnabled(false);
            LoginRequest loginRequest = new LoginRequest(mLogin.getText().toString(), mPassword.getText().toString());
            Observable<Response<User>> call = service.login(loginRequest.getLogin(), loginRequest.getPassword());
            call.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Response<User>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "onError: " + e.getMessage());
                            mLoginButton.setEnabled(true);
                        }

                        @Override
                        public void onNext(Response<User> userResponse) {
                            if(userResponse.code() == 200){
                                startActivity(new Intent(MainActivity.this, LoggedActivity.class));
                            }else{
                                Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                                mLoginButton.setEnabled(true);
                            }

                        }
                    });
            //mLoginButton.setEnabled(false);
        });
    }

    private boolean isValidPassword(CharSequence value) {
        return value.toString().matches("^(?=.*\\d).{4,8}$");
    }

    private boolean isValidLogin(CharSequence value) {
        //return value.length() >= 5;
        return value.toString().matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
    }


}
