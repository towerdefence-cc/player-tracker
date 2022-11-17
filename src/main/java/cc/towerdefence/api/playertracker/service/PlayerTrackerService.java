package cc.towerdefence.api.playertracker.service;

import cc.towerdefence.api.playertracker.model.OnlinePlayer;
import cc.towerdefence.api.playertracker.repository.PlayerRepository;
import cc.towerdefence.api.service.PlayerTrackerProto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerTrackerService {
    private final PlayerRepository playerRepository;
    private final MongoTemplate playerRepositoryTemplate;

    public void proxyPlayerLogin(PlayerTrackerProto.PlayerLoginRequest request) {
        Query existsQuery = new Query(Criteria.where("_id").is(UUID.fromString(request.getPlayerId())));

        Update update = new Update()
                .set("_id", UUID.fromString(request.getPlayerId()))
                .set("proxyId", request.getServerId())
                .set("username", request.getPlayerName());

        this.playerRepositoryTemplate.upsert(existsQuery, update, OnlinePlayer.class);
    }

    public void serverPlayerLogin(PlayerTrackerProto.PlayerLoginRequest request) {
        Query existsQuery = new Query(Criteria.where("_id").is(UUID.fromString(request.getPlayerId())));

        Update update = new Update()
                .set("_id", UUID.fromString(request.getPlayerId()))
                .set("serverId", request.getServerId())
                .set("username", request.getPlayerName());

        this.playerRepositoryTemplate.upsert(existsQuery, update, OnlinePlayer.class);
    }

    public void proxyPlayerDisconnect(PlayerTrackerProto.PlayerDisconnectRequest request) {
        this.playerRepository.deleteById(UUID.fromString(request.getPlayerId()));
    }

    public PlayerTrackerProto.OnlineServer getPlayerServer(PlayerTrackerProto.PlayerRequest request) {
        UUID playerId = UUID.fromString(request.getPlayerId());
        Optional<OnlinePlayer> optionalPlayer = this.playerRepository.findById(playerId);

        return optionalPlayer.map(player -> PlayerTrackerProto.OnlineServer.newBuilder()
                        .setServerId(player.getServerId())
                        .setProxyId(player.getProxyId())
                        .build())
                .orElse(null);
    }

    public Map<String, PlayerTrackerProto.OnlineServer> getPlayerServers(PlayerTrackerProto.PlayersRequest request) {
        Iterable<OnlinePlayer> onlinePlayers = this.playerRepository.findAllById(request.getPlayerIdsList().stream().map(UUID::fromString).toList());

        Map<String, PlayerTrackerProto.OnlineServer> playerServers = new HashMap<>();
        for (OnlinePlayer onlinePlayer : onlinePlayers) {
            playerServers.put(onlinePlayer.getId().toString(), PlayerTrackerProto.OnlineServer.newBuilder()
                    .setServerId(onlinePlayer.getServerId())
                    .setProxyId(onlinePlayer.getProxyId())
                    .build());
        }

        return playerServers;
    }

    public int getServerPlayerCount(PlayerTrackerProto.ServerIdRequest request) {
        return this.playerRepository.countAllByServerId(request.getServerId());
    }

    public List<OnlinePlayer> getServerPlayers(PlayerTrackerProto.ServerIdRequest request) {
        return this.playerRepository.findAllByServerId(request.getServerId());
    }
}
