package website.qingxu.camerademo.activity.subPage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import website.qingxu.camerademo.R;


public class PhoneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
//        1.得到控件
        final EditText et_phone = (EditText) findViewById(R.id.et_phone);
        Button bt_phone = (Button) findViewById(R.id.bt_phone);
        //第二步点击事件
        bt_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取输入的电话号码
                String phone = et_phone.getText().toString();
                //创建打电话的意图
                Intent intent = new Intent();
                //设置拨打电话的动作
                intent.setAction(Intent.ACTION_CALL);
                //开启打电话的意图
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);
            }
        });
    }
}
