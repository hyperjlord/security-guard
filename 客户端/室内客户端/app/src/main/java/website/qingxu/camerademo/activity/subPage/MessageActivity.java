package website.qingxu.camerademo.activity.subPage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import website.qingxu.camerademo.R;


public class MessageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
//        1.得到控件
//        找控件需要两步，第一步设置id，第二步通过findViewById找到控件
        final EditText et_name = (EditText) findViewById(R.id.et_name);
        final EditText et_message = (EditText) findViewById(R.id.et_message);
        Button bt_send = (Button) findViewById(R.id.bt_send_message);
        //第二步点击事件
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //3.事件处理
                //3.1获取相关信息
                String name = et_name.getText().toString();
                String message = et_message.getText().toString();
                Uri nameUri = Uri.parse("smsto:" + name);
                Intent returnIt = new Intent();
                returnIt.setAction(Intent.ACTION_SENDTO);//发短信的action
                returnIt.setData(nameUri);
                //1.获取短信管理器
                //3.2发送短信
                SmsManager sm = SmsManager.getDefault();
                //写第⼀个参数是电话号码,
                //第⼆个参数是消息中⼼,一般为空即可
                //第三个参数是短信内容,
                //第四,第五个参数是⼴播,是否发送成功,是否接收成功的⼴播,⼀般工作中填空即可不需要填写
                sm.sendTextMessage(name, null, message, null, null);
                System.out.println("发送信息" + message + "给" + name);
                Toast.makeText(MessageActivity.this,"发送短信成功", Toast.LENGTH_LONG).show();
                startActivity(returnIt);
            }
        });
    }
}
