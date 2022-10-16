package cc.towerdefence.api.playertracker.controller;

import cc.towerdefence.api.playertracker.service.PlayerTrackerService;
import cc.towerdefence.api.service.GetPlayerServerRequest;
import cc.towerdefence.api.service.GetPlayerServerResponse;
import cc.towerdefence.api.service.GetPlayerServersRequest;
import cc.towerdefence.api.service.GetPlayerServersResponse;
import cc.towerdefence.api.service.GetServerPlayerCountResponse;
import cc.towerdefence.api.service.GetServerPlayersResponse;
import cc.towerdefence.api.service.OnlinePlayer;
import cc.towerdefence.api.service.OnlineServer;
import cc.towerdefence.api.service.PlayerDisconnectRequest;
import cc.towerdefence.api.service.PlayerLoginRequest;
import cc.towerdefence.api.service.PlayerTrackerGrpc;
import cc.towerdefence.api.service.ServerIdRequest;
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
    public void proxyPlayerLogin(PlayerLoginRequest request, StreamObserver<Empty> responseObserver) {
        this.playerTrackerService.proxyPlayerLogin(request);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void serverPlayerLogin(PlayerLoginRequest request, StreamObserver<Empty> responseObserver) {
        this.playerTrackerService.serverPlayerLogin(request);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void proxyPlayerDisconnect(PlayerDisconnectRequest request, StreamObserver<Empty> responseObserver) {
        this.playerTrackerService.proxyPlayerDisconnect(request);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void getPlayerServer(GetPlayerServerRequest request, StreamObserver<GetPlayerServerResponse> responseObserver) {
        OnlineServer server = this.playerTrackerService.getPlayerServer(request);
        GetPlayerServerResponse.Builder response = GetPlayerServerResponse.newBuilder();

        if (server != null)
            response.setServer(server);

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getPlayerServers(GetPlayerServersRequest request, StreamObserver<GetPlayerServersResponse> responseObserver) {
        responseObserver.onNext(GetPlayerServersResponse.newBuilder().putAllPlayerServers(this.playerTrackerService.getPlayerServers(request)).build());
        responseObserver.onCompleted();
    }

    @Override
    public void getServerPlayerCount(ServerIdRequest request, StreamObserver<GetServerPlayerCountResponse> responseObserver) {
        responseObserver.onNext(GetServerPlayerCountResponse.newBuilder().setPlayerCount(this.playerTrackerService.getServerPlayerCount(request)).build());
        responseObserver.onCompleted();
    }

    @Override
    public void getServerPlayers(ServerIdRequest request, StreamObserver<GetServerPlayersResponse> responseObserver) {
        List<OnlinePlayer> onlinePlayers = this.playerTrackerService.getServerPlayers(request).stream()
                .map(onlinePlayer -> OnlinePlayer.newBuilder().setPlayerId(onlinePlayer.getId().toString()).setUsername(onlinePlayer.getUsername()).build())
                .toList();

        responseObserver.onNext(GetServerPlayersResponse.newBuilder().addAllOnlinePlayers(onlinePlayers).build());
        responseObserver.onCompleted();
    }
}
