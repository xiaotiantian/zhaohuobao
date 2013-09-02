package com.bfz.zhbao.utils;


public abstract class StringRemoteService extends RemoteService {
    @Override
    public Object onExecute() throws Exception {

        try {

            String result = null;

            if (formFiles != null) {
             


                result = HttpClientUtil.getStringByPostMultipart(postUrl, params, formFiles, getPostCallbackHandler(), 1000);
            } else {
                if (requestMethod == REQUEST_METHOD_GET) {
                   
                    result = HttpClientUtil.getStringByGet(postUrl, params, 1000);
                } else {
                 
                    result = HttpClientUtil.getStringByPost(postUrl, params, 1000);
                }

            }

           

            return result;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    protected HttpClientUtil.HttpClientPostCallbackHandler getPostCallbackHandler() {
        return null;
    }

}
