package cc.towerdefence.api.playertracker.service;

import cc.towerdefence.api.playertracker.model.OnlinePlayer;
import cc.towerdefence.api.playertracker.repository.PlayerRepository;
import cc.towerdefence.api.service.GetPlayerServerRequest;
import cc.towerdefence.api.service.GetPlayerServersRequest;
import cc.towerdefence.api.service.OnlineServer;
import cc.towerdefence.api.service.PlayerDisconnectRequest;
import cc.towerdefence.api.service.PlayerLoginRequest;
import cc.towerdefence.api.service.ServerIdRequest;
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

    public void proxyPlayerLogin(PlayerLoginRequest request) {
        Query existsQuery = new Query(Criteria.where("_id").is(UUID.fromString(request.getPlayerId())));

        Update update = new Update()
                .set("_id", UUID.fromString(request.getPlayerId()))
                .set("proxyId", request.getServerId())
                .set("username", request.getPlayerName());

        this.playerRepositoryTemplate.upsert(existsQuery, update, OnlinePlayer.class);
    }

    public void serverPlayerLogin(PlayerLoginRequest request) {
        Query existsQuery = new Query(Criteria.where("_id").is(UUID.fromString(request.getPlayerId())));

        Update update = new Update()
                .set("_id", UUID.fromString(request.getPlayerId()))
                .set("serverId", request.getServerId())
                .set("username", request.getPlayerName());

        this.playerRepositoryTemplate.upsert(existsQuery, update, OnlinePlayer.class);
    }

    public void proxyPlayerDisconnect(PlayerDisconnectRequest request) {
        this.playerRepository.deleteById(UUID.fromString(request.getPlayerId()));
    }

    public OnlineServer getPlayerServer(GetPlayerServerRequest request) {
        UUID playerId = UUID.fromString(request.getPlayerId());
        Optional<OnlinePlayer> optionalPlayer = this.playerRepository.findById(playerId);


        return optionalPlayer.map(player -> OnlineServer.newBuilder()
                        .setServerId(player.getServerId())
                        .setProxyId(player.getProxyId())
                        .build())
                .orElse(null);
    }

    public Map<String, OnlineServer> getPlayerServers(GetPlayerServersRequest request) {
        Query query = Query.query(Criteria.where("_id").in(request.getPlayerIdsList()));
        List<OnlinePlayer> onlinePlayers = this.playerRepositoryTemplate.find(query, OnlinePlayer.class);

        Map<String, OnlineServer> playerServers = new HashMap<>();
        for (OnlinePlayer onlinePlayer : onlinePlayers) {
            playerServers.put(onlinePlayer.getId().toString(), OnlineServer.newBuilder()
                    .setServerId(onlinePlayer.getServerId())
                    .setProxyId(onlinePlayer.getProxyId())
                    .build());
        }

        return playerServers;
    }

    public int getServerPlayerCount(ServerIdRequest request) {
        return (int) this.playerRepositoryTemplate.count(Query.query(Criteria.where("serverId").is(request.getServerId())), OnlinePlayer.class);
    }

    public List<OnlinePlayer> getServerPlayers(ServerIdRequest request) {
        Query query = Query.query(Criteria.where("serverId").is(request.getServerId()));
        return this.playerRepositoryTemplate.find(query, OnlinePlayer.class);
    }
}
