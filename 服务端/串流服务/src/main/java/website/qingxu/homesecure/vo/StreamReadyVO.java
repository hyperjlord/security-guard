package website.qingxu.homesecure.vo;

import lombok.Data;

@Data
public class StreamReadyVO extends AbstractResult {
    private String RTMPurl;
    private String HTTPurl;
}
