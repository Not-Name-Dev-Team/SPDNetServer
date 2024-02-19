package me.catand.spdnetserver;

import me.catand.spdnetserver.entitys.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, String> {
	boolean existsByQq(long qq);

	boolean existsByName(String name);

	boolean existsByKey(String key);

	Player findByQq(long qq);

	Player findByName(String name);

	Player findByKey(String key);
}