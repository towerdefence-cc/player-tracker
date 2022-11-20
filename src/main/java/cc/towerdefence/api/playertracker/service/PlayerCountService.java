package cc.towerdefence.api.playertracker.service;

import cc.towerdefence.api.playertracker.repository.PlayerRepository;
import cc.towerdefence.api.utils.spring.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PlayerCountService {
    private final PlayerRepository playerRepository;

    private final Map<String, Integer> playerCounts = new HashMap<>();

    public int getPlayerCount(String serverType) {
        Integer count = this.playerCounts.get(serverType);
        if (count == null)
            throw new ResourceNotFoundException("Server type %s not found (%s)"
                    .formatted(serverType, Strings.join(this.playerCounts.keySet().iterator(), ',')));

        return count;
    }

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.SECONDS)
    @PostConstruct
    public void updatePlayerCounts() {
        this.playerCounts.put("velocity", (int) this.playerRepository.count());
        this.playerCounts.put("lobby", this.playerRepository.countAllByServerIdStartingWith("lobby-"));
        this.playerCounts.put("tower-defence-game", this.playerRepository.countAllByServerIdStartingWith("tower-defence-game-"));
    }
}
