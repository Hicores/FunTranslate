package fun.project.translate.abstractApi;

import java.util.HashMap;

public class CommonHttpApi {
    public static class ApiInfo{
        /*
        可以Replace的参数 url,headers,urlParams
        变量
        $SOURCE_TEXT$
        $TIME_STAMP$
        $TIME_STAMP11$
        $TOKEN$
        $TOKEN2$
        $TOKEN3$

        resultParseText内容可以自定义组合

        $"xxxx"dawhawihawoiehwae""

         */
        public static final int METHOD_GET = 1;
        public static final int METHOD_POST= 2;
        public int method;
        public String url;
        public HashMap<String,String> headers = new HashMap<String,String>();

        public static final int BODY_TYPE_JSON = 1;
        public static final int BODY_TYPE_QUERY = 2;
        public int bodyType;
        public HashMap<String,String> body;

        public static final int RESULT_TYPE_JSON = 1;
        public int resultType;
        public String resultParseText;



        public String generateCode;
        public long limitPerSecond;
    }
}
