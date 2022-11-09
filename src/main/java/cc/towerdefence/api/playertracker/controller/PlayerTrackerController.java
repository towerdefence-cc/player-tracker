package cc.towerdefence.api.playertracker.controller;

import cc.towerdefence.api.model.common.PlayerProto;
import cc.towerdefence.api.playertracker.service.PlayerTrackerService;
import cc.towerdefence.api.service.PlayerTrackerGrpc;
import cc.towerdefence.api.service.PlayerTrackerProto;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.util.List;

@GrpcService
@Controller

@RequiredArgsConstructor
public class PlayerTrackerController extends PlayerTrackerGrpc.PlayerTrackerImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerTrackerController.class);

    private final PlayerTrackerService playerTrackerService;

    @Override
    public void proxyPlayerLogin(PlayerTrackerProto.PlayerLoginRequest request, StreamObserver<Empty> responseObserver) {
        this.playerTrackerService.proxyPlayerLogin(request);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void serverPlayerLogin(PlayerTrackerProto.PlayerLoginRequest request, StreamObserver<Empty> responseObserver) {
        this.playerTrackerService.serverPlayerLogin(request);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void proxyPlayerDisconnect(PlayerTrackerProto.PlayerDisconnectRequest request, StreamObserver<Empty> responseObserver) {
        this.playerTrackerService.proxyPlayerDisconnect(request);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void getPlayerServer(PlayerProto.PlayerRequest request, StreamObserver<PlayerTrackerProto.GetPlayerServerResponse> responseObserver) {
        PlayerTrackerProto.OnlineServer server = this.playerTrackerService.getPlayerServer(request);
        PlayerTrackerProto.GetPlayerServerResponse.Builder response = PlayerTrackerProto.GetPlayerServerResponse.newBuilder();

        if (server != null)
            response.setServer(server);

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getPlayerServers(PlayerProto.PlayersRequest request, StreamObserver<PlayerTrackerProto.GetPlayerServersResponse> responseObserver) {
        responseObserver.onNext(PlayerTrackerProto.GetPlayerServersResponse.newBuilder()
                .putAllPlayerServers(this.playerTrackerService.getPlayerServers(request))
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void getServerPlayerCount(PlayerTrackerProto.ServerIdRequest request, StreamObserver<PlayerTrackerProto.GetServerPlayerCountResponse> responseObserver) {
        responseObserver.onNext(PlayerTrackerProto.GetServerPlayerCountResponse.newBuilder().setPlayerCount(this.playerTrackerService.getServerPlayerCount(request)).build());
        responseObserver.onCompleted();
    }

    @Override
    public void getServerPlayers(PlayerTrackerProto.ServerIdRequest request, StreamObserver<PlayerTrackerProto.GetServerPlayersResponse> responseObserver) {
        List<PlayerTrackerProto.OnlinePlayer> onlinePlayers = this.playerTrackerService.getServerPlayers(request).stream()
                .map(onlinePlayer -> PlayerTrackerProto.OnlinePlayer.newBuilder().setPlayerId(onlinePlayer.getId().toString()).setUsername(onlinePlayer.getUsername()).build())
                .toList();

        responseObserver.onNext(PlayerTrackerProto.GetServerPlayersResponse.newBuilder().addAllOnlinePlayers(onlinePlayers).build());
        responseObserver.onCompleted();
    }
}
