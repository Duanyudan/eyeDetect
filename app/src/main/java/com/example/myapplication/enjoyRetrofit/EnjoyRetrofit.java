package com.example.myapplication.enjoyRetrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.http.POST;

public class EnjoyRetrofit {
    private Map<Method, ServiceMethod> serviceMethodMap = new ConcurrentHashMap<>();
    HttpUrl baseUrl;
    Call.Factory callFactory;

    public <T> T create(Class<T> service) {
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                ServiceMethod serviceMethod = loadServiceMethod(method);
                return serviceMethod.invoke(args);
            }
        });

    }

    private ServiceMethod loadServiceMethod(Method method) {
        ServiceMethod result = serviceMethodMap.get(method);
        if (result != null) {
            return result;
        }
        synchronized (serviceMethodMap) {
            result = serviceMethodMap.get(method);
            if (result == null) {
                result = new ServiceMethod.Builder(this, method).build();
                serviceMethodMap.put(method, result);
            }
        }
        return result;
    }

    public EnjoyRetrofit(HttpUrl baseUrl,
                         Call.Factory callFactory) {
        this.baseUrl = baseUrl;
        this.callFactory = callFactory;
    }


    public static final class Builder {
        HttpUrl baseUrl;
        Call.Factory callFactory;

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = HttpUrl.get(baseUrl);
            return this;
        }

        public Builder callFactory(Call.Factory callFactory) {
            this.callFactory = callFactory;
            return this;
        }

        public EnjoyRetrofit build() {
            if (baseUrl == null) {
                throw new IllegalArgumentException("baseUrl is required");
            }
            if (callFactory == null) {
                callFactory = new OkHttpClient();
            }
            return new EnjoyRetrofit(baseUrl, callFactory);
        }
    }
}
