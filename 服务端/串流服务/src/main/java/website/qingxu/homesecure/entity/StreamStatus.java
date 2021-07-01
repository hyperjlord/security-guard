package website.qingxu.homesecure.entity;

import lombok.Data;

@Data
public class StreamStatus {
    private long wardId;
    private String RTMPurl;
    private String HTTPurl;
    private boolean isSuccess;
}