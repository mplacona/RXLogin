package uk.co.placona.rxlogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import uk.co.placona.rxlogin.api.ApiClient;
import uk.co.placona.rxlogin.api.ApiService;
import uk.co.placona.rxlogin.models.User;

public class LoggedActivity extends AppCompatActivity {
    private static final String TAG = "LoggedActivity";

    @BindView(R.id.greeting_tv)
    TextView mGreeting;

    @BindView(R.id.logout_bt)
    Button mLogoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged);

        ButterKnife.bind(this);
        ApiService service = ApiClient.getRetrofitInstance().create(ApiService.class);

        Observable<Response<User>> call = service.user();
        call.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<User>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Response<User> userResponse) {
                        if(userResponse.code() != 200){
                            Toast.makeText(LoggedActivity.this, "Error with Auth", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        mGreeting.setText(String.format("Hello %s", userResponse.body().getName()));
                    }
                });

        RxView.clicks(mLogoutButton).subscribe(aVoid -> {
            mLogoutButton.setEnabled(false);
            Observable<Response<Void>> logout = service.logout();
            logout.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<Void>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e.getCause());
                    }

                    @Override
                    public void onNext(Response<Void> voidResponse) {
                        if(voidResponse.code() != 200){
                            Toast.makeText(LoggedActivity.this, "Error with Auth", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        startActivity(new Intent(LoggedActivity.this, MainActivity.class));
                    }
                });
        });
    }
}
