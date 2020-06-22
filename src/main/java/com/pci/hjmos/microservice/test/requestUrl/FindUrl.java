package com.pci.hjmos.microservice.test.requestUrl;

import com.alibaba.fastjson.JSONObject;
import com.pci.hjmos.framework.core.utils.JsonUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * @author zyting
 * @sinne 2020-06-22
 */
public class FindUrl {
    public static void main(String[] args) throws IOException {

//        try {
//            String str;
//            URL u = new URL("https://www.baidu.com");
//            InputStream is = u.openStream();
//            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
//            BufferedReader br = new BufferedReader(isr);
//            if (br.ready()) {
//                while ((str = br.readLine()) != null) {
//                    System.out.println(str);
//                }
//            }
//            br.close();
//            isr.close();
//            is.close();
//        } catch (MalformedURLException e) {
//            // url地址错误
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Map<String,Object> param = new HashMap<>();
        JSONObject param = new JSONObject();
        param.put("srcId", "2020021914090001");
        param.put("platType", "A00");
        param.put("devId", "44011600002000000005");
        param.put("user", "admin");
        param.put("acl", "");
        param.put("ts", 1111111111111L);
        param.put("reserve", "");
        param.put("sessionId", System.currentTimeMillis());
        param.put("devApiMode", 99);
        param.put("devVsProtocol", 1);
        param.put("devVsUrl", "rtsp://admin:Suntek123@172.28.51.164:554");
        param.put("devVsEncode", 0);
        param.put("devVsPkg", 0);
        param.put("devVsTCP", 0);
        param.put("devVsFrame", 0);
        param.put("devUser", "admin");
        param.put("devPassword", "Suntek123");
        param.put("devIPv4", "172.28.51.164");
        param.put("devIPv6", "");
        param.put("devPort", "554");
        param.put("devAsEncode", 99);
        param.put("vsType", 1);
        param.put("vsIp", "172.25.20.131");
        param.put("vsPort", "9090");
        param.put("vsIndex", 0);
        param.put("vsProtocol", 2);
        param.put("vsPkg", 2);
        param.put("vsEncode", 0);
        param.put("vsTCP", 0);
        param.put("vsDirect", 1);
        param.put("vsFrame", 0);
        param.put("asEncode", 2);
        param.put("startTime", "");
        param.put("endTime", "");
        param.put("recSaveType", 1);
        param.put("recType", 1);
        param.put("RecordTraceStation", "");
        param.put("devChannel", "0");
        param.put("channelId", "44011600002000000009");
        param.put("platformId", "");
        param.put("clientId", "111111");

//        String sr = JsonUtils.map2String(param);
        //发送 POST 请求
        String str = HttpRequest.sendPost("http://172.25.21.118:8389/vsp/v1/a00/video/addVideoStream",param);
        System.out.println(str);

        JSONObject o = (JSONObject)JsonUtils.json2Obj(str);
        JSONObject data = o.getJSONObject("data");
        String vsUrl = data.getString("vsUrl");
        JSONObject vsUrlMap = data.getJSONObject("vsUrlMap");
        String http_hls = vsUrlMap.getString("http-hls");
        System.out.println("http_hls: "+http_hls);
        System.out.println("vsUrl: "+vsUrl);

    }

}
