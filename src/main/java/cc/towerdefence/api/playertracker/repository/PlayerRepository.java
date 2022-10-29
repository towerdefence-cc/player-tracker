package cc.towerdefence.api.playertracker.repository;

import cc.towerdefence.api.playertracker.model.OnlinePlayer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlayerRepository extends MongoRepository<OnlinePlayer, UUID> {

    List<OnlinePlayer> findAllByServerId(String serverId);

    int countAllByServerId(String serverId);

    List<OnlinePlayer> findAllByProxyId(String proxyId);
}
