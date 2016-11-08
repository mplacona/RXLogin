package uk.co.placona.rxlogin;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.login_et)
    EditText mLogin;

    @BindView(R.id.password_et)
    EditText mPassword;

    @BindView(R.id.login_bt)
    Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Drawable invalidField = ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.presence_busy);
        Drawable validField = ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.presence_online);

        Observable<CharSequence> loginObservable = RxTextView.textChanges(mLogin);
        loginObservable
                .map(this::isValidLogin)
                .subscribe(isValid -> mLogin.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null, (isValid ? validField : invalidField), null));

        Observable<CharSequence> passwordObservable = RxTextView.textChanges(mPassword);
        passwordObservable
                .map(this::isValidPassword)
                .subscribe(isValid -> mPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null, (isValid ? validField : invalidField), null));

        Observable<Boolean> combinedObservables = Observable.combineLatest(loginObservable, passwordObservable, (o1, o2) -> isValidLogin(o1) && isValidPassword(o2));
        combinedObservables.subscribe(isVisible -> mLoginButton.setVisibility(isVisible ? View.VISIBLE : View.GONE));
    }

    private boolean isValidPassword(CharSequence value) {
        return value.toString().matches("^(?=.*\\d).{4,8}$");
    }

    private boolean isValidLogin(CharSequence value) {
        return value.length() >= 5;
    }


}
