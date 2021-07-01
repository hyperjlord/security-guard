package website.qingxu.homesecure.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class Command {
    private int code;
    private JSONObject params;
    public static class CommandCode{
        private CommandCode(){}
        public static final int KEEP_STREAM = 0;
        public static final int PULL_STREAM = 1;
    }
}
