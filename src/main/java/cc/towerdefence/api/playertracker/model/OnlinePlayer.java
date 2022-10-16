package cc.towerdefence.api.playertracker.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "players")
public class OnlinePlayer {
    @Id
    private UUID id;

    private String username;

    private String serverId;

    private String proxyId;

    public OnlinePlayer(UUID id, String username, String proxyId) {
        this.id = id;
        this.username = username;
        this.proxyId = proxyId;
    }
}
