package com.fno.rpc.handler;

import com.fno.rpc.entity.RpcRequest;
import com.fno.rpc.entity.RpcResponse;
import com.fno.rpc.enumeration.ResponseCode;
import com.fno.rpc.provider.DefaultServiceProvider;
import com.fno.rpc.provider.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    public static final ServiceProvider serviceProvider;

    static {
        serviceProvider = new DefaultServiceProvider();
    }

    public Object handle(RpcRequest rpcRequest) {
        Object result = null;
        RpcResponse rpcResponse = new RpcResponse();
        Object service = serviceProvider.getService(rpcRequest.getInterfaceName());
        try {
            result = invoke(rpcRequest, service);
        } catch (InvocationTargetException | IllegalAccessException e) {
            logger.error("调用时有错误发生:{}", e);
        }
        return result;
    }

    private Object invoke(RpcRequest rpcRequest, Object service) throws InvocationTargetException, IllegalAccessException {
        Method method;
        try {
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
        } catch (NoSuchMethodException e) {
            return RpcResponse.fail(ResponseCode.NO_SUCH_METHOD, rpcRequest.getRequestId());
        }
        return method.invoke(service, rpcRequest.getParameters());
    }
}
