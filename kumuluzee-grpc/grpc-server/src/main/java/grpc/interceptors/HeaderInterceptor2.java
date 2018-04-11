package grpc.interceptors;

import io.grpc.*;

import java.util.logging.Logger;

public class HeaderInterceptor2 implements ServerInterceptor {

    private static final Logger logger = Logger.getLogger(HeaderInterceptor.class.getName());

    static final Metadata.Key<String> HEADER_KEY = Metadata.Key.of("server_header_key", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata,
                                                                 ServerCallHandler<ReqT, RespT> serverCallHandler) {

        logger.info("Header received from client " + serverCall.getAuthority() + ": interceptor 2");
        return serverCallHandler.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(serverCall) {
            @Override
            public void sendHeaders(Metadata headers) {
                headers.put(HEADER_KEY, "respondValue");
                super.sendHeaders(headers);
            }
        }, metadata);
    }

}
