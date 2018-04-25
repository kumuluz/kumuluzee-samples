package client;

import com.kumuluz.ee.grpc.client.GrpcChannelConfig;
import com.kumuluz.ee.grpc.client.GrpcChannels;
import com.kumuluz.ee.grpc.client.GrpcClient;
import com.kumuluz.ee.grpc.client.JWTClientCredentials;
import io.grpc.stub.StreamObserver;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.net.ssl.SSLException;
import java.util.logging.Logger;

@ApplicationScoped
public class UserServiceClient {

    private final static Logger logger = Logger.getLogger(UserServiceClient.class.getName());
    private final String JWT_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9." +
            "eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0IiwiaWF0IjoxNTE2MjM5MDIyfQ." +
            "VhfWc4uNtTNicztGHvnXyRMhnmIrXZ947EjO9ECV3G6pzPCYqjzwkdTgykW-" +
            "FWbQsSJH6aVnryK0DoLrO8f4XEsblj_Ind1CffXYcqjyZxwkPy4r5SxA--QvewsUsWfC1_I55J-Z6kh7oHm5Z_7vasudOFAXukmY5uBg_adDJN4";
    private UserGrpc.UserStub stub;

    @PostConstruct
    public void init() {
        try {
            GrpcChannels clientPool = GrpcChannels.getInstance();
            GrpcChannelConfig config = clientPool.getGrpcClientConfig("client1");
            GrpcClient client = new GrpcClient(config);
            stub = UserGrpc.newStub(client.getChannel()).withCallCredentials(new JWTClientCredentials(JWT_TOKEN));
        } catch (SSLException e) {
            logger.warning(e.getMessage());
        }
    }

    public void getUser(Integer id) {
        UserService.UserRequest request = UserService.UserRequest.newBuilder()
                .setId(id)
                .build();

        stub.getUser(request, new StreamObserver<UserService.UserResponse>() {
            @Override
            public void onNext(UserService.UserResponse userResponse) {
                logger.info(userResponse.getName() + " " + userResponse.getSurname());
            }

            @Override
            public void onError(Throwable throwable) {
                logger.warning("Error retrieving user");
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                logger.info("Completed");
            }
        });
    }
}
