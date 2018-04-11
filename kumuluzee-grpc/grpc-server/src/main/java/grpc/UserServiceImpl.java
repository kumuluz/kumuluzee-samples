package grpc;

import com.kumuluz.ee.grpc.annotations.GrpcInterceptor;
import com.kumuluz.ee.grpc.annotations.GrpcService;
import io.grpc.stub.StreamObserver;
import beans.UserBean;
import entity.User;

import javax.enterprise.inject.spi.CDI;
import java.util.logging.Logger;

@GrpcService(interceptors = {
        @GrpcInterceptor(name = "grpc.interceptors.HeaderInterceptor2"),
        @GrpcInterceptor(name = "grpc.interceptors.HeaderInterceptor")},
        secured = true)
public class UserServiceImpl extends UserGrpc.UserImplBase {

    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    private UserBean userBean;

    @Override
    public void getUser(UserService.UserRequest request, StreamObserver<UserService.UserResponse> responseObserver) {

        userBean = CDI.current().select(UserBean.class).get();
        User user = userBean.getUser(request.getId());
        UserService.UserResponse response;

        if (user != null) {
            response = UserService.UserResponse.newBuilder()
                    .setId(user.getId())
                    .setName(user.getName())
                    .setSurname(user.getSurname())
                    .build();
            responseObserver.onNext(response);
        }

        responseObserver.onCompleted();
    }
}
