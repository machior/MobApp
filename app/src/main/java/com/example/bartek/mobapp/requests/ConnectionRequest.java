package com.example.bartek.mobapp.requests;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bartek on 26-Nov-16.
 */

public class ConnectionRequest extends StringRequest
{
//    private static final String LOGIN_REQUEST_URL = "http://92.222.84.121/MobApp/LoginRequest.php";
//    private static final String REGISTER_REQUEST_URL = "http://92.222.84.121/MobApp/RegisterRequest.php";
//    private static final String PULL_REQUEST_URL = "http://92.222.84.121/MobApp/PullRequest.php";
//    private static final String PUSH_REQUEST_URL = "http://92.222.84.121/MobApp/PushRequest.php";
    private static final String REQUEST_URL = "http://92.222.84.121/MobApp/requestHandling.php";
    private static final String LOGIN_REQUEST = "LoginRequest";
    private static final String REGISTER_REQUEST = "RegisterRequest";
    private static final String PULL_REQUEST = "PullRequest";
    private static final String PUSH_REQUEST = "PushRequest";
    private Map<String, String> params;

    /**
     * Connection constructor
     * @param params        parameters passed in map form
     * @param request    URL adequate to action
     * @param listener      response listener
     */
    private ConnectionRequest(Map<String, String> params, String request, Response.Listener<String> listener){
        super(Request.Method.POST, REQUEST_URL, listener, null);
        params.put("request", request);
//        super(Request.Method.POST, requestURL, listener, null);
        this.params = params;
        System.out.println(params);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

    public enum CONNECT_ACTION {
        LOGIN, REGISTRATION, PULL, PUSH
    }

    public static ConnectionRequest connectionAction(CONNECT_ACTION action, Map<String, String> params, Response.Listener<String> listener)
    {
        String actionURL;
//        if( action.equals(CONNECT_ACTION.LOGIN) ){    actionURL = LOGIN_REQUEST_URL;  }
//        else if( action.equals(CONNECT_ACTION.REGISTRATION) ){    actionURL = REGISTER_REQUEST_URL;  }
//        else if( action.equals(CONNECT_ACTION.PULL) ){    actionURL = PULL_REQUEST_URL;  }
//        else if( action.equals(CONNECT_ACTION.PUSH) ){    actionURL = PUSH_REQUEST_URL;  }
//        else return null;
        if( action.equals(CONNECT_ACTION.LOGIN) ){    actionURL = LOGIN_REQUEST;  }
        else if( action.equals(CONNECT_ACTION.REGISTRATION) ){    actionURL = REGISTER_REQUEST;  }
        else if( action.equals(CONNECT_ACTION.PULL) ){    actionURL = PULL_REQUEST;  }
        else if( action.equals(CONNECT_ACTION.PUSH) ){    actionURL = PUSH_REQUEST;  }
        else return null;

        return new ConnectionRequest(params, actionURL, listener);
    }
}
