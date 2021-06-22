package com.example.myapplication.enjoyRetrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class ServiceMethod {

    private final HttpUrl baseUrl;
    private final Call.Factory callFactory;
    private final String httpMethod;
    private final boolean hasBody;
    private final String relativeUrl;
    private ParameterHandler[] parameterHandlers;
    private FormBody.Builder formBuild;
    private HttpUrl.Builder urlBuilder;

    public ServiceMethod(Builder builder) {
        baseUrl = builder.retrofit.baseUrl;
        callFactory = builder.retrofit.callFactory;
        httpMethod = builder.httpMethod;
        relativeUrl = builder.relativeUrl;
        hasBody = builder.hasBody;
        parameterHandlers = builder.parameterHandlers;

        if (hasBody) {
            formBuild = new FormBody.Builder();
        }

    }

    public Object invoke(Object[] args) {
        for (int i = 0; i < parameterHandlers.length; i++) {
            ParameterHandler handler = parameterHandlers[i];
            handler.apply(ServiceMethod.this, args[i].toString());
        }

        HttpUrl url;
        if (urlBuilder == null) {
            urlBuilder = baseUrl.newBuilder(relativeUrl);
        }
        url = urlBuilder.build();
        FormBody formBody = null;
        if (formBuild != null) {
            formBody = formBuild.build();
        }

        Request request = new Request.Builder().url(url)
                .method(httpMethod, formBody)
                .build();
        return callFactory.newCall(request);
    }

    public void addQueryParameter(String key, String value) {
        if (urlBuilder == null) {
            urlBuilder = baseUrl.newBuilder(relativeUrl);
        }
        urlBuilder.addQueryParameter(key, value);
    }

    public void addFiledParameter(String key, String value) {
        formBuild.add(key, value);
    }

    public static final class Builder {
        private final Annotation[] methodaAnnotations;
        private final Annotation[][] parameterAnnotations;
        private String httpMethod;
        private boolean hasBody;
        private String relativeUrl;
        private ParameterHandler[] parameterHandlers;

        private EnjoyRetrofit retrofit;

        public Builder(EnjoyRetrofit retrofit, Method method) {
            this.retrofit = retrofit;
            methodaAnnotations = method.getAnnotations();
            parameterAnnotations = method.getParameterAnnotations();
        }

        public ServiceMethod build() {
//            解析方法上的注解
            for (Annotation methodaAnnotation : methodaAnnotations) {
                if (methodaAnnotation instanceof POST) {
                    this.httpMethod = "POST";
                    this.hasBody = true;
                    this.relativeUrl = ((POST) methodaAnnotation).value();
                } else if (methodaAnnotation instanceof GET) {
                    this.httpMethod = "GET";
                    this.hasBody = false;
                    this.relativeUrl = ((GET) methodaAnnotation).value();
                }
            }
            parameterHandlers = new ParameterHandler[parameterAnnotations.length];
//            方法参数的注解
            for (int i = 0; i < parameterAnnotations.length; i++) {
                Annotation[] annotations = parameterAnnotations[i];
                for (Annotation annotation : annotations) {
                    if (annotation instanceof Field) {
//                       请求参数的 key
                        String value = ((Field) annotation).value();
                        parameterHandlers[i] = new ParameterHandler.FieldParameterHandler(value);

                    } else if (annotation instanceof Query) {
                        String value = ((Query) annotation).value();
                        parameterHandlers[i] = new ParameterHandler.QueryParameterHandler(value);

                    }
                }
            }
            return new ServiceMethod(this);
        }
    }
}
