package website.qingxu.homesecure.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import website.qingxu.homesecure.controller.qo.LoginQO;
import website.qingxu.homesecure.controller.vo.LoginVO;
import website.qingxu.homesecure.controller.vo.StateCode;
import website.qingxu.homesecure.vo.AccountCameraListVO;
import website.qingxu.homesecure.vo.CameraListVO;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class CameraControllerTest {
    @Resource
    private CameraController cameraController;
    @Resource
    private RestTemplate restTemplate;

    private String token;

    @Before
    public void setUp() throws Exception {
        LoginQO loginQO = new LoginQO("18019087170", "111111");
        LoginVO loginVO = restTemplate.postForObject("http://ACCOUNT-PROVIDER/v0.0.1/account/login",
                loginQO,
                LoginVO.class);
        assert loginVO != null;
        if (loginVO.getStateCode() == StateCode.SUCCESS) {
            token = loginVO.getToken();
        }
        else {
            token = "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwiaWF0IjoxNjIzNDA3NDg0fQ.WI7upy7NYgKzx5NlScpvT4hC3ek4yiAYwAG4cKlGD5g";
        }
    }

    @Test
    public void getMyCameraList() {
        CameraListVO res = cameraController.getMyCameraList(token);
        System.out.println(res);
        assertNotNull(res);
    }

    @Test
    public void getAccountCameraList() {
        AccountCameraListVO res = cameraController.getAccountCameraList(token);
        System.out.println(res);
        assertNotNull(res);
    }

    @Test
    public void getWardCameraList() {
        CameraListVO res = cameraController.getWardCameraList(token, 1);
        System.out.println(res);
        assertNotNull(res);
    }

    @After
    public void tearDown() throws Exception {
        token = null;
    }
}