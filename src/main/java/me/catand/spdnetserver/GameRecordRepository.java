package me.catand.spdnetserver;

import org.springframework.data.jpa.repository.JpaRepository;
import me.catand.spdnetserver.entitys.GameRecord;

public interface GameRecordRepository extends JpaRepository<GameRecord, String> {
}